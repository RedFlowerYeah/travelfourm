package com.travelfourm.dao;

import com.travelfourm.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


import java.util.List;

@Mapper
@Repository
public interface CommentMapper {

    List<Comment> selectAllComment();

    List<Comment> selectCommentsByEntity(int entityType,int entityId,int offset,int limit);

    int selectCountByEntity(int entityType,int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);

    List<Comment> selectCommentByUserId(int userId,int offset,int limit);

    int selectCountByUserId(int userId);
}
