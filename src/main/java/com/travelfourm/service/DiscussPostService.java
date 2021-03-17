package com.travelfourm.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.travelfourm.Util.AuthService;
//import com.travelfourm.Util.CheckContent;
import com.travelfourm.Util.HttpUtil;
import com.travelfourm.Util.SensitiveFilter;
import com.travelfourm.config.BaiduSensitiveConfig;
import com.travelfourm.dao.DiscussPostMapper;
import com.travelfourm.entity.DiscussPost;
import com.travelfourm.entity.TextCheckReturn;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import javax.xml.soap.Text;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


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

    //帖子缓存列表
    private LoadingCache<String ,List<DiscussPost>> postListCache;

    //帖子总数缓存
    private LoadingCache<Integer , Integer> postRowsCache;

    @PostConstruct
    public void init(){
        //初始化帖子缓存列表
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

                        //二级缓存 Redis->mysql
                        logger.debug("load post list from DataBase");

                        return discussPostMapper.selectDiscussPosts(0,offset,limit,1);
                    }
                });

        //初始化帖子总数缓存
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

    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit,int orderMode){
//        if (userId == 0 && orderMode == 1){
//            return postListCache.get(offset + ":" + limit);
//        }

        logger.debug("load post list from DataBase");

        return discussPostMapper.selectDiscussPosts(userId, offset, limit,orderMode);
    }

    public int findDiscussPostRows(int userId){

//        if (userId == 0){
//            return postRowsCache.get(userId);
//        }

        logger.debug("load post rows from DataBase");
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    //增加帖子
    public int addDiscussPost(DiscussPost discussPost){
        if (discussPost == null){
             throw new IllegalArgumentException("参数不能为空！");
        }

        //转义HTML标记
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        /**
         * 通过百度智能云过滤敏感词*/
        //获取标题和文本内容
        String title = discussPost.getTitle();
        String content = discussPost.getContent();

//        //获取access_token
        String access_token = AuthService.getAuth();
        try {
            //设置请求的编码
            String param = "text=" + URLEncoder.encode(title, "UTF-8");
            String param1 = "text=" + URLEncoder.encode(content,"UTF-8");

            //调用文本审核接口并取得结果（标题）
            String result = HttpUtil.post(BaiduSensitiveConfig.CHECK_TEXT_URL,access_token,param);

            //调用文本审核接口并取得结果（内容）
            String result1 = HttpUtil.post(BaiduSensitiveConfig.CHECK_TEXT_URL,access_token,param1);

            // JSON解析对象（标题和内容）
            TextCheckReturn tcr = JSONObject.parseObject(result, TextCheckReturn.class);
            TextCheckReturn tcr1 = JSONObject.parseObject(result1,TextCheckReturn.class);

            Integer conclusionType = tcr.getConclusionType();
            Integer conclusionType1 = tcr1.getConclusionType();

            //先判断标题是否符合规定，再判断内容是否符合规定
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

    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    //更新评论总数
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    //更新帖子的形式
    public int updateType(int id,int type){
        return discussPostMapper.updateType(id, type);
    }

    //更新帖子的状态
    public int updateStatus(int id,int status){
        return discussPostMapper.updateStatus(id,status);
    }

    //更新帖子的分数
    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }
}
