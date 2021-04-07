package com.travelfourm.service;

import com.travelfourm.Util.CommunityConstant;
import com.travelfourm.Util.SensitiveFilter;
import com.travelfourm.dao.CommentMapper;
import com.travelfourm.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    /**
     * 查询全部评论*/
    public List<Comment> findAllComment(){
        return commentMapper.selectAllComment();
    }

    /**
     * 通过实体查询评论*/
    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    /**
     * 查询评论总数*/
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    /**
     * READ_COMMITTED授权读取级别
     * 以操作同一行数据为前提，读事务允许其他读事务和写事务，未提交的写事务禁止其他读事务和写事务。
     * 此隔离级别可以防止更新丢失、脏读，但不能防止不可重复读、幻读。
     * REQUIRED是常用的事务传播行为，如果当前没有事务，就新建一个事务，如果已经存在一个事务中，加入到这个事务中。*/
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        //转义HTML的评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));

        int rows = commentMapper.insertComment(comment);

        /**
         * 更新帖子评论数量*/
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }

        return rows;
    }

    /**
     * 通过id查询评论*/
    public Comment findCommentById(int id){
        return commentMapper.selectCommentById(id);
    }

    /**
     * 通过userId查询所有评论*/
    public List<Comment> findCommentsByUserId(int userId,int offset,int limit){
        return commentMapper.selectCommentByUserId(userId,offset,limit);
    }

    /**
     * 查询评论总数*/
    public int findCommentCount(int userId){
        return commentMapper.selectCountByUserId(userId);
    }

    /**
     * 删除评论*/
    public int deleteComment(int id){return  commentMapper.deleteComment(id);}
}
