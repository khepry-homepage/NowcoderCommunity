package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    //  获取用户的私信会话列表（分页），每个会话显示第一条私信内容
    public List<Message> selectConversationList(int userId, int offset, int limit);
    //  获取用户私信会话数量
    public int selectConversationRows(int userId);
    //  获取某个会话包含的私信详情
    public List<Message> selectLetters(String conversationId, int offset, int limit);
    //  获取某个会话包含的私信条数
    public int selectLetterRows(String conversationId);
    //  获取用户的所有未读私信条数或者某个会话的所有未读私信条数
    /**
     *
     * @param userId
     * @param conversationId    :   return total unread letters if null
     * @return
     */
    public int selectUnReadCount(int userId, String conversationId);
    public int insertMessage(Message message);
    public int updateConversationStatus(int userId, int status, String conversationId);
}
