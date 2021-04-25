package com.travelfourm.Util;

/**
 * Redis中的缓存key-value值的名称
 * @author 34612*/
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE ="like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_UER = "user";
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU ="dau";
    private static final String PREFIX_POST = "post";

    /**
     * 某个实体（这里的实体包括评论和帖子）的赞
     * like : entity : entityType :entityId ->set(userId)*/
    public static String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType +SPLIT + entityId;
    }

    /**
     * 某个用户收到的点赞
     * like : user :userId -> int*/
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    /**
     * 某个用户关注的实体（实体包括帖子、回复等）
     * followee :userId : entityType -> zset(entityId,now)*/
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 某个用户所拥有的粉丝
     * follower : entityType : entityId -> zset(userId,now)*/
    public static String getFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER + SPLIT +entityType + SPLIT +entityId;
    }

    /**
     * 登录验证码(redis缓存）*/
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    /**
     * 登录凭证*/
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }

    /**
     * 用户信息*/
    public static String getUserKey(int userId){
        return PREFIX_UER + SPLIT + userId;
    }

    /**
     * 单日统计*/
    public static String getUVkey(String date){
        return PREFIX_UV + SPLIT +date;
    }

    /**
     * 区间统计*/
    public static String getUVKey(String startDate,String endDate){
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    /**
     * 单日活跃用户*/
    public static String getDAUKey(String date){
        return PREFIX_DAU + SPLIT + date;
    }

    /**
     * 区间活跃用户*/
    public static String getDAUKey(String startDate,String endDate){
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    /**
     * 帖子分数*/
    public static String getPostScoreKey() {
        return PREFIX_POST + SPLIT + "score";
    }
}
