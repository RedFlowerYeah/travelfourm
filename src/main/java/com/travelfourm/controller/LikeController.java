package com.travelfourm.controller;

import com.travelfourm.Util.CommunityConstant;
import com.travelfourm.Util.CommunityUtil;
import com.travelfourm.Util.HostHolder;
import com.travelfourm.Util.RedisKeyUtil;
import com.travelfourm.entity.Event;
import com.travelfourm.entity.User;
import com.travelfourm.event.EventProducer;
import com.travelfourm.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞事件*/
    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        User user = hostHolder.getUser();

        // 点赞
        if (user == null){
            return CommunityUtil.getJsonString(1,"该用户未登录，请先登录！");
        }else
        {
            likeService.like(user.getId(), entityType, entityId, entityUserId);
        }

        // 数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        // 返回的结果
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        /**
         * 触发点赞事件
         * 先将点赞事件发布到相应的主题中，在通过生产者-消费者的方式进行事件的完成*/
        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }

        if(entityType == ENTITY_TYPE_POST) {
            // 计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, postId);
        }

        return CommunityUtil.getJsonString(0, null, map);
    }
}
