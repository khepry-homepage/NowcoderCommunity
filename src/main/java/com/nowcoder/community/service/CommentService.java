package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    public Comment findComent(int id) {
        return commentMapper.selectById(id);
    }

    public List<Comment> findComments(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectComments(entityType, entityId, offset, limit);
    }

    public int findCommentRows(int entityType, int entityId) {
        return commentMapper.selectCommentCount(entityType, entityId);
    }
    public int addComment(Comment comment) {

        comment.setContent(sensitiveFilter.filterMatch(comment.getContent()));
        return commentMapper.insertComment(comment);
    }
}
