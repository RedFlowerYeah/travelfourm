package com.travelfourm.controller;

import com.travelfourm.Util.CommunityUtil;
import com.travelfourm.Util.HostHolder;
import com.travelfourm.entity.User;
import com.travelfourm.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId){
        User user = hostHolder.getUser();

        // 点赞
        likeService.like(user.getId(), entityType, entityId,entityUserId);

        //数量
        long likeCount = likeService.findEntityLikeCount(entityType,entityId);

        //状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(),entityType,entityId);

        //返回的结果
        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        return CommunityUtil.getJsonString(0,null,map);
    }
}
