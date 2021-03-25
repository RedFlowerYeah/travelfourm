package com.travelfourm.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.travelfourm.Util.CommunityConstant;
import com.travelfourm.Util.HostHolder;
import com.travelfourm.Util.RedisKeyUtil;
import com.travelfourm.entity.Comment;
import com.travelfourm.entity.DiscussPost;
import com.travelfourm.entity.Event;
import com.travelfourm.entity.User;
import com.travelfourm.event.EventProducer;
import com.travelfourm.service.CommentService;
import com.travelfourm.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private RedisTemplate redisTemplate;


    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // 触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            // 触发发帖事件
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);
            // 计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, discussPostId);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }

    //表格跳转页面
    @GetMapping("/showLayui2")
    public String showAllCommentLayui(){
        return "/backup/showAllComment";
    }

    //评论信息接口
    @GetMapping("/showAllComment")
    @ResponseBody
    public Map<String ,Object> showAllUser(@RequestParam(required = false,defaultValue = "0")String type,
                                           @RequestParam(required = false,defaultValue = "")String content,
                                           @RequestParam(required = false,defaultValue = "1")int page,
                                           @RequestParam(required = false,defaultValue = "10")int limit){
        //开始分页
        PageHelper.startPage(page,limit);

        //分页查询
        List<Comment> list = new ArrayList<>();
        if (type.equals("0")){
            list = commentService.findAllComment();
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
