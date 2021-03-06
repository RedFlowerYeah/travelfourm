package com.travelfourm.dao;

import com.travelfourm.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 34612
 */
@Mapper
@Repository
public interface DiscussPostMapper {

    List<DiscussPost> selectAllDiscussPost();

    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit,int orderMode);

    List<DiscussPost> selectDiscussPostsHot(int limit);

    List<DiscussPost> selectDiscussPostsHotByModular(@Param("modular")String modular,int limit);

    /**
     * 通过modular查找属于相应模块的帖子*/
    List<DiscussPost> selectDiscussPostByModular(@Param("modular") String modular);

    /**
     * @Param注解用于给参数取别名
     * 如果只有一个参数，并且在</if>里面使用，则必须加别名*/
    int selectDiscussPostRows(@Param("userId")int userId);

    List<DiscussPost> selectDiscussUseId(@Param("userId") int userId);

    int selectCountDiscussPost();

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id,int commentCount);

    int updateType(int id,int type);

    int updateStatus(int id,int status);

    int updateScore(int id, double score);

    int updateModular(int id,String modular);

    int updateDiscussPostAll(int id,String title,String content,String modular);
}
