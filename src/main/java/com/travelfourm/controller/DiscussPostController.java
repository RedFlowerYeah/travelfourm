package com.travelfourm.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.travelfourm.Util.*;
import com.travelfourm.entity.*;
import com.travelfourm.event.EventProducer;
import com.travelfourm.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 34612
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextpath;

    /**
     * 创建帖子*/
    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content,String modular) {

        /**
         * 获取当前用户的id*/
        User user = hostHolder.getUser();

        if (user == null) {
            return CommunityUtil.getJsonString(403, "你还没有登录哦!");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        post.setModular(modular);

        /**如果此时post等于-1，则抛出异常；否则继续执行*/
        if (discussPostService.addDiscussPost(post) == -1){
            return CommunityUtil.getJsonString(1,"发布失败，帖子标题或内容存在敏感内容，请重新输入");
        }

        /**
         * 触发发帖事件*/
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);

        /**
         * 在Redis中统计分数*/
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, post.getId());

        // 报错的情况,将来统一处理.
        return CommunityUtil.getJsonString(0, "发布成功!");

    }

    /**
     * 分模块请求*/
    @GetMapping("/section/{modular}")
    public String getDiscussPostByModular(@PathVariable("modular") String modular,Model model){

        List<DiscussPost> list = discussPostService.findDiscussPostByModular(modular);
        List<DiscussPost> list1 = discussPostService.findDiscussHotByModular(modular,3);
        List<Province> list2 = provinceService.findProvince();

        List<Map<String , Object>> discussPosts = new ArrayList<>();
        List<Map<String,Object>> discussPosts1 = new ArrayList<>();
        List<Map<String,Object>> provinces = new ArrayList<>();

        if (list != null){
            for (DiscussPost post : list){
                Map<String, Object> map = new HashMap<>();
                map.put("post",post);

                User user=userService.findUserById(post.getUserId());
                map.put("user",user);

                long likeCount  = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }

        if (list1 != null){
            for (DiscussPost post1 : list1){
                Map<String ,Object> map1 = new HashMap<>();
                map1.put("post1",post1);

                User user = userService.findUserById(post1.getUserId());
                map1.put("user",user);

                discussPosts1.add(map1);
            }
        }

        if (list2 != null){
            for (Province province : list2){
                Map<String,Object> map2 = new HashMap<>();
                map2.put("province",province);

                provinces.add(map2);
            }
        }

        model.addAttribute("provinces",provinces);
        model.addAttribute("discussPosts1",discussPosts1);
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("modular",modular);
        model.addAttribute("discussPosts",discussPosts);
        return "/site/modular";
    }

    /**查看帖子详情*/
    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        // 帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);

        /**
         * 如果帖子的状态没有被拉黑*/
        if (post.getStatus() != 2) {
            model.addAttribute("post", post);

            /**
             * 帖子发起人*/
            User user = userService.findUserById(post.getUserId());

            model.addAttribute("user", user);

            /**
             * 点赞数量*/
            long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);

            model.addAttribute("likeCount", likeCount);

            /**
             * 点赞状态*/
            int likeStatus = hostHolder.getUser() == null ? 0 :
                    likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, discussPostId);
            model.addAttribute("likeStatus", likeStatus);

            /**
             * 评论分页信息*/
            page.setLimit(5);
            page.setPath("/discuss/detail/" + discussPostId);
            page.setRows(post.getCommentCount());

            /**
             * 评论: 给帖子的评论
             * 回复: 给评论的评论
             * 评论列表*/
            List<Comment> commentList = commentService.findCommentsByEntity(
                    ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());

            /**
             * 评论VO列表*/
            List<Map<String, Object>> commentVoList = new ArrayList<>();
            if (commentList != null) {
                for (Comment comment : commentList) {

                    /**
                     * 评论VO*/
                    Map<String, Object> commentVo = new HashMap<>();

                    /**
                     * 评论*/
                    commentVo.put("comment", comment);

                    /**
                     * 帖子发起人*/
                    commentVo.put("user", userService.findUserById(comment.getUserId()));

                    /**
                     * 点赞数量*/
                    likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                    commentVo.put("likeCount", likeCount);

                    /**
                     * 点赞状态*/
                    likeStatus = hostHolder.getUser() == null ? 0 :
                            likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                    commentVo.put("likeStatus", likeStatus);

                    /**
                     * 回复列表*/
                    List<Comment> replyList = commentService.findCommentsByEntity(
                            ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);

                    /**
                     * 回复VO列表*/
                    List<Map<String, Object>> replyVoList = new ArrayList<>();
                    if (replyList != null) {
                        for (Comment reply : replyList) {
                            Map<String, Object> replyVo = new HashMap<>();
                            // 回复
                            replyVo.put("reply", reply);
                            // 作者
                            replyVo.put("user", userService.findUserById(reply.getUserId()));
                            // 回复目标
                            User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                            replyVo.put("target", target);
                            // 点赞数量
                            likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                            replyVo.put("likeCount", likeCount);
                            // 点赞状态
                            likeStatus = hostHolder.getUser() == null ? 0 :
                                    likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                            replyVo.put("likeStatus", likeStatus);

                            replyVoList.add(replyVo);
                        }
                    }
                    commentVo.put("replys", replyVoList);

                    // 回复数量
                    int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                    commentVo.put("replyCount", replyCount);

                    commentVoList.add(commentVo);
                }
            }

            model.addAttribute("comments", commentVoList);

            return "/site/discuss-detail";
        }
        return "/error/remove";
    }

    /**
     * 置顶*/
    @PostMapping("/top")
    @ResponseBody
    public String setTop(int id) {
        discussPostService.updateType(id, 1);

        // 触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJsonString(0);
    }

    /**
     * 管理员修改帖子模块*/
    @PostMapping("/updateModular")
    @ResponseBody
    public String setModular(int id,String modular){
        elasticsearchService.updateDiscussPostModular(id,modular);
        discussPostService.updateModular(id,modular);

        return CommunityUtil.getJsonString(0);
    }

    /**
     * 用户修改帖子*/
    @PostMapping("/updateTest")
    @ResponseBody
    public String update(int id,String title,String content,String modular){
        elasticsearchService.updateDiscussPost(id,title,content,modular);
        discussPostService.updateDiscussPostAll(id,title,content,modular);
        return CommunityUtil.getJsonString(0);
    }

    /**
     * 加精*/
    @PostMapping("/wonderful")
    @ResponseBody
    public String setWonderful(int id) {
        discussPostService.updateStatus(id, 1);

        // 触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        // 计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, id);

        return CommunityUtil.getJsonString(0);
    }

    /**
     * 管理员删除帖子*/
    @PostMapping("/delete")
    @ResponseBody
    public String setDelete1(int id) {

        //可以通过查找帖子的user_id再来获取相应的user的email
        DiscussPost post = discussPostService.findDiscussPostById(id);
        User user = userService.findUserById(post.getUserId());

        /**
         * 删除帖子邮件*/
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        context.setVariable("DiscussPostName",post.getTitle());

        /**
         * href地址：http://localhost:8080/travelfourm/discuss/feedback */
        String url = domain + contextpath + "/discuss/feedback";
        context.setVariable("url",url);

        /**
         * 邮件内容*/
        String content = templateEngine.process("/mail/deletediscusspost",context);
        mailClient.sendMail(user.getEmail(), "删除帖子", content);

        /**
         * 删除帖子仅做假删
         * 为了防止数据库需要恢复数据的原因
         * 其实还有一个办法是读写分离，但是考虑成本代价的问题放弃了*/
        discussPostService.updateStatus(id, 2);

        // 触发删帖事件
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJsonString(0);
    }

    /**
     * 个人删除自己的帖子*/
    @PostMapping("/delete1")
    @ResponseBody
    public String setDelete2(int id){
        discussPostService.updateStatus(id,2);

        //触发删帖事件
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJsonString(0);
    }

    /**
     * 跳转反馈界面*/
    @GetMapping("/feedback")
    public String goFeedBack(){
        return "/site/feedback";
    }

    /**
     * 用户申诉*/
    @PostMapping("/feedbackDiscussPost")
    @ResponseBody
    public String feedback(String title, String reason){
        Context context = new Context();
        context.setVariable("username",hostHolder.getUser().getUsername());

        context.setVariable("title",title);
        context.setVariable("reason",reason);

        String content1 = templateEngine.process("/mail/userFeedback",context);
        mailClient.sendMail("346125735@qq.com","用户申诉",content1);

        return CommunityUtil.getJsonString(0);
    }

    @PostMapping("updateStatusTo0")
    @ResponseBody
    public String updateStatusTo0(int id, int userId, String title, String content, int type, int status,Date createTime, int commentCount, double score, String modular){
        elasticsearchService.updateDiscussPostStatus(id, userId, title, content, type, status, createTime, commentCount, score, modular);
        return CommunityUtil.getJsonString(0);
    }

    /**
     * 修改的查询接口
     * @return*/
    @GetMapping("/showSome")
    @ResponseBody
    public String showSome(int id){
        DiscussPost discussPost = discussPostService.findDiscussPostById(id);
        String str = JSON.toJSONString(discussPost);
        return str;
    }

    /**表格跳转页面*/
    @GetMapping("/showLayui1")
    public String showAllUserLayui(){
        return "/backup/showDiscussPost";
    }

    /**帖子信息接口*/
    @GetMapping("/showAllDiscussPost")
    @ResponseBody
    public Map<String ,Object>  showAllDiscussPost(@RequestParam(required = false,defaultValue = "0")String type,
                                            @RequestParam(required = false,defaultValue = "")String content,
                                            @RequestParam(required = false,defaultValue = "1")int page,
                                            @RequestParam(required = false,defaultValue = "10")int limit){
        //开始分页
        PageHelper.startPage(page,limit);

        //分页查询
        List<DiscussPost> list = new ArrayList<>();
        if (type.equals("0")){
            list = discussPostService.findAllDiscussPost();
        }
        //封装数据
        PageInfo pageInfo = new PageInfo(list);
        Map<String ,Object> map = new HashMap<>();
        map.put("code",0);
        map.put("msg","查询成功");
        map.put("count",pageInfo.getTotal());
        map.put("data",pageInfo.getList());
        return map;
    }
}
