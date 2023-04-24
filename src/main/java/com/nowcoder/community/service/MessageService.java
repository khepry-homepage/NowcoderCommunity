package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.utils.Constants;
import com.nowcoder.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Message> findConversationList(int userId, int offset, int limit) {
        return messageMapper.selectConversationList(userId, offset, limit);
    }

    public int findConversationRows(int userId) {
        return messageMapper.selectConversationRows(userId);
    }

    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    public int findLetterRows(String conversationId) {
        return messageMapper.selectLetterRows(conversationId);
    }

    public List<Message> findNoticeList(int userId) {
        return messageMapper.selectNoticeList(userId);
    }
    public List<Message> findNotices(int userId, String conversationId, int offset, int limit) {
        if (conversationId == null ||
                (!conversationId.equals(Constants.EVENT_TYPE_COMMENT) && !conversationId.equals(Constants.EVENT_TYPE_LIKE) && !conversationId.equals(Constants.EVENT_TYPE_FOLLOW))) {
            throw new IllegalArgumentException("无效会话id！");
        }
        return messageMapper.selectNotices(userId, conversationId, offset, limit);
    }
    public int findNoticeRows(int userId, String conversationId) {
        return messageMapper.selectNoticeRows(userId, conversationId);
    }
    public int findLetterUnReadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnReadCount(userId, conversationId);
    }
    public int findNoticeUnReadCount(int userId, String conversationId) {
        return messageMapper.selectNoticeUnReadCount(userId, conversationId);
    }

    public int addMessage(Message message) {
        message.setContent(sensitiveFilter.filterMatch(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    public int updateConversationStatus(int userId, int status, String conversationId) {
        assert (status > 0 && status < 3);
        return messageMapper.updateConversationStatus(userId, status, conversationId);
    }
}