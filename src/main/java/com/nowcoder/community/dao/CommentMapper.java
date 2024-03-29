package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
@Mapper
public interface CommentMapper {
    Comment selectById(int id);
    List<Comment> selectComments(int userId, int entityType, int entityId, int offset, int limit);
    int selectCommentCount(int userId, int entityType, int entityId);
    int insertComment(Comment comment);
}
