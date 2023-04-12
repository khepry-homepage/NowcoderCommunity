package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
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

    public int findUnReadCount(int userId, String conversationId) {
        return messageMapper.selectUnReadCount(userId, conversationId);
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