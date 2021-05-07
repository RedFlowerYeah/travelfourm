package com.travelfourm.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.travelfourm.Util.AuthService;
import com.travelfourm.Util.HttpUtil;
import com.travelfourm.Util.SensitiveFilter;
import com.travelfourm.config.BaiduSensitiveConfig;
import com.travelfourm.dao.DiscussPostMapper;
import com.travelfourm.entity.DiscussPost;
import com.travelfourm.entity.TextCheckReturn;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.thymeleaf.context.Context;

import javax.annotation.PostConstruct;
import javax.xml.soap.Text;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author 34612
 */
@Service
public class DiscussPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-second}")
    private int expireSeconds;

    //Caffeine核心接口，Cache，LoadingCache，AsyncLoadingCache

    /**
     * 帖子缓存列表*/
    private LoadingCache<String ,List<DiscussPost>> postListCache;

    /**
     * 帖子总数缓存*/
    private LoadingCache<Integer , Integer> postRowsCache;

    @PostConstruct
    public void init(){
        /**
         * 初始化帖子Caffeine（Caffeine为本地缓存）缓存列表
         * 手动加载
         * .maxmumSize 设置数量上限
         * .expireAfterWrite 过期时间
         * load方法为异步加载重写机制，从二级缓存Redis中获取数据*/
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String s) throws Exception {
                        if (s == null || s.length() == 0){
                            throw new IllegalArgumentException("参数错误！");
                        }

                        String[] params = s.split(":");
                        if (params == null || params.length != 2){
                            throw new IllegalArgumentException("参数错误！");
                        }

                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);

                        /**
                         * 二级缓存 Redis->mysql*/
                        logger.debug("load post list from DataBase");

                        return discussPostMapper.selectDiscussPosts(0,offset,limit,1);
                    }
                });

        /**
         * 初始化帖子总数缓存*/
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds,TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer integer) throws Exception {

                        logger.debug("load post rows from DataBase");
                        return discussPostMapper.selectDiscussPostRows(integer);
                    }
                });
    }

    /**
     * 查找全部帖子列表（不加入到缓存中）*/
    public List<DiscussPost> findAllDiscussPost(){
        return discussPostMapper.selectAllDiscussPost();
    }

    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit,int orderMode){

        logger.debug("load post list from DataBase");

        return discussPostMapper.selectDiscussPosts(userId, offset, limit,orderMode);
    }

    /**
     * 查找最热门的帖子*/
    public List<DiscussPost> findDiscussHot(int limit){
        return discussPostMapper.selectDiscussPostsHot(limit);
    }

    /**
     * 通过最热门模块查找帖子*/
    public List<DiscussPost> findDiscussHotByModular(String modular,int limit){
        return discussPostMapper.selectDiscussPostsHotByModular(modular,limit);
    }

    /**
     * 查找用户自己的帖子*/
    public int findDiscussPostRows(int userId){

        logger.debug("load post rows from DataBase");

        return discussPostMapper.selectDiscussPostRows(userId);
    }

    /**
     * 统计帖子*/
    public int countDiscussPosts(){
        return discussPostMapper.selectCountDiscussPost();
    }

    /**
     * 发布帖子*/
    public int addDiscussPost(DiscussPost discussPost){
        if (discussPost == null){
             throw new IllegalArgumentException("参数不能为空！");
        }

        /**
         * 转义HTML标记*/
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        /**
         * 通过百度智能云过滤敏感词（此处为新发布的帖子的评论和内容都需要进行过滤）*/
        /**
         * 获取标题和文本内容*/
        String title = discussPost.getTitle();
        String content = discussPost.getContent();

        /**
         * 获取accesstoken*/
        String access_token = AuthService.getAuth();
        try {
            /**
             * 设置请求编码*/
            String param = "text=" + URLEncoder.encode(title, "UTF-8");
            String param1 = "text=" + URLEncoder.encode(content,"UTF-8");

            /**
             * HttpUtil中的post为打开链接并提交内容
             * 调用文本审核接口并取得结果（标题）*/
            String result = HttpUtil.post(BaiduSensitiveConfig.CHECK_TEXT_URL,access_token,param);

            /**
             * 调用文本审核接口并取得结果（内容）*/
            String result1 = HttpUtil.post(BaiduSensitiveConfig.CHECK_TEXT_URL,access_token,param1);

            /**JSON解析对象（标题和内容）*/
            TextCheckReturn tcr = JSONObject.parseObject(result, TextCheckReturn.class);
            TextCheckReturn tcr1 = JSONObject.parseObject(result1,TextCheckReturn.class);

            /**
             * 获取返回的成功类型是0或1
             * 0表示正常
             * 1表示含有敏感词*/
            Integer conclusionType = tcr.getConclusionType();
            Integer conclusionType1 = tcr1.getConclusionType();

            /**
             * 先判断标题是否符合规定，再判断内容是否符合规定*/
            if (conclusionType != 1 && !conclusionType.equals("1")){
                return -1;
            }else if (conclusionType1 != 1 && !conclusionType1.equals("1")){
                return -1;
            }else{
                discussPost.setTitle(title);
                discussPost.setContent(content);
                return discussPostMapper.insertDiscussPost(discussPost);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 根据id查询帖子*/
    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    /**
     * 根据User_Id查询帖子*/
    public List<DiscussPost> findDiscussPostByUserId(int userId){
        return discussPostMapper.selectDiscussUseId(userId);
    }

    /**
     * 根据modular来查询帖子，可以进行分模块*/
    public List<DiscussPost> findDiscussPostByModular(String modular){
        return discussPostMapper.selectDiscussPostByModular(modular);
    }

    /**
     * 更新评论总数*/
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    /**
     * 更新帖子的形式：置顶或不置顶*/
    public int updateType(int id,int type){
        return discussPostMapper.updateType(id, type);
    }

    /**
     * 更细帖子的状态*/
    public int updateStatus(int id,int status){
        return discussPostMapper.updateStatus(id,status);
    }

    /**
     * 更新帖子的分数*/
    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }

    /**
     * 更新帖子所属板块*/
    public int updateModular(int id ,String modular){
        return discussPostMapper.updateModular(id , modular);
    }

    /**
     * 用户更新帖子*/
    public int updateDiscussPostAll(int id,String title,String content,String modular){
        return discussPostMapper.updateDiscussPostAll(id,title,content,modular);
    }
}
