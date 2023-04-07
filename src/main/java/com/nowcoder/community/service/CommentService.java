package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    public List<Comment> findComments(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectComments(entityType, entityId, offset, limit);
    }

    public int findCommentRows(int entityType, int entityId) {
        return commentMapper.selectCommentCount(entityType, entityId);
    }
    public int addComment(Comment comment) {
        return commentMapper.insertComment(comment);
    }
}
