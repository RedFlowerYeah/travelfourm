package com.travelfourm.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.travelfourm.Util.*;
import com.travelfourm.entity.*;
import com.travelfourm.event.EventProducer;
import com.travelfourm.service.CommentService;
import com.travelfourm.service.DiscussPostService;
import com.travelfourm.service.LikeService;
import com.travelfourm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

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
    private LikeService likeService;

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

    //增加帖子
    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();

        if (user == null) {
            return CommunityUtil.getJsonString(403, "你还没有登录哦!");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());

        //如果此时post等于-1，则抛出异常
        //如果等于0，则继续执行
        if (discussPostService.addDiscussPost(post) == -1){
            return CommunityUtil.getJsonString(1,"发布失败，帖子标题或内容存在敏感内容，请重新输入");
        }

        // 触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);

        // 计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, post.getId());

        // 报错的情况,将来统一处理.
        return CommunityUtil.getJsonString(0, "发布成功!");

    }

    /**查看帖子详情*/
    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        // 帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        if (post.getStatus() != 2) {
            model.addAttribute("post", post);
            // 作者
            User user = userService.findUserById(post.getUserId());
            model.addAttribute("user", user);
            // 点赞数量
            long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
            model.addAttribute("likeCount", likeCount);
            // 点赞状态
            int likeStatus = hostHolder.getUser() == null ? 0 :
                    likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, discussPostId);
            model.addAttribute("likeStatus", likeStatus);

            // 评论分页信息
            page.setLimit(5);
            page.setPath("/discuss/detail/" + discussPostId);
            page.setRows(post.getCommentCount());

            // 评论: 给帖子的评论
            // 回复: 给评论的评论
            // 评论列表
            List<Comment> commentList = commentService.findCommentsByEntity(
                    ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
            // 评论VO列表
            List<Map<String, Object>> commentVoList = new ArrayList<>();
            if (commentList != null) {
                for (Comment comment : commentList) {
                    // 评论VO
                    Map<String, Object> commentVo = new HashMap<>();
                    // 评论
                    commentVo.put("comment", comment);
                    // 作者
                    commentVo.put("user", userService.findUserById(comment.getUserId()));
                    // 点赞数量
                    likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                    commentVo.put("likeCount", likeCount);
                    // 点赞状态
                    likeStatus = hostHolder.getUser() == null ? 0 :
                            likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                    commentVo.put("likeStatus", likeStatus);

                    // 回复列表
                    List<Comment> replyList = commentService.findCommentsByEntity(
                            ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                    // 回复VO列表
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

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());

        // http://localhost:8080/travelfourm/deletediscusspost
        String content = templateEngine.process("/mail/deletediscusspost",context);
        mailClient.sendMail(user.getEmail(), "删除帖子", content);

        //目前是假删，后续需要真删
        //这里目前只假删了帖子，后续如果需要删除帖子，还需要把此帖子的评论一并删除掉
        //这里要进行判断，要先删除贴子中的评论，再删除帖子
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
//            list = userService.findAllUser();
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
