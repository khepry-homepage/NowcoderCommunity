package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    DiscussPost selectDiscussPostById(int id);
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);
    int selectDiscussPostRows(int userId);
    int insertDiscussPost(DiscussPost discussPost);
    int updateCommentCount(int id, int commentCount);
    int updatePostType(int id, int type);
    int updatePostStatus(int id, int status);
}
