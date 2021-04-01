package com.travelfourm.quartz;

import com.travelfourm.Util.CommunityConstant;
import com.travelfourm.Util.RedisKeyUtil;
import com.travelfourm.entity.DiscussPost;
import com.travelfourm.service.DiscussPostService;
import com.travelfourm.service.ElasticsearchService;
import com.travelfourm.service.LikeService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 34612
 */

public class PostScoreRefreshJob implements Job, CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    /**
     * 论坛开始时间*/
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-03-05 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化失败!", e);
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if (operations.size() == 0) {
            logger.info("[任务取消] 没有需要刷新的帖子!");
            return;
        }

        logger.info("[任务开始] 正在刷新帖子分数: " + operations.size());

        while (operations.size() > 0) {
            this.refresh((Integer) operations.pop());
        }
        logger.info("[任务结束] 帖子分数刷新完毕!");
    }

    private void refresh(int postId) {
        DiscussPost post = discussPostService.findDiscussPostById(postId);

        if (post == null) {
            logger.error("该帖子不存在: id = " + postId);
            return;
        }

        // 是否精华
        boolean wonderful = post.getStatus() == 1;
        // 评论数量
        int commentCount = post.getCommentCount();
        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        /**
         * 计算权重
         * 权重 = （加精？75：0）+ 评论数 *10 + 点赞数 * 2*/
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;

        /**
         * 计算分数
         * 分数 = log10（Math.max(权重，1）） + （帖子发布的时间 - 论坛创建的时间）/ 标准一天的时间*/
        double score = Math.log10(Math.max(w, 1))
                + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);

        // 更新帖子分数
        discussPostService.updateScore(postId, score);

        // 同步搜索数据
        post.setScore(score);

        /**
         * 这里貌似存在统计分数去数据库调用的问题*/
        elasticsearchService.saveDiscussPost(post);
    }

}
