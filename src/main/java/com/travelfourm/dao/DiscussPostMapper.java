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

    //@Param注解用于给参数取别名
    //如果只有一个参数，并且在</if>里面使用，则必须加别名
    int selectDiscussPostRows(@Param("userId")int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id,int commentCount);

    int updateType(int id,int type);

    int updateStatus(int id,int status);

    int updateScore(int id, double score);
}
