package com.nowcoder.community.event;

import com.alibaba.fastjson2.JSONObject;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticSearchService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.utils.Constants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;

@Component
public class EventConsumer {
    @Autowired
    private CommentService commentService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private ElasticSearchService elasticSearchService;
    @KafkaListener(topics = {Constants.EVENT_TYPE_LIKE, Constants.EVENT_TYPE_COMMENT, Constants.EVENT_TYPE_FOLLOW})
    public void handleMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            throw new IllegalArgumentException("消息内容不能为空！");
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            throw new IllegalArgumentException("无效的消息格式！");
        }
        Message message = new Message();
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getEventType());
        message.setFromId(Constants.SYSTEM_USER_ID);
        message.setCreateTime(new Date());
        Map<String, Object> content = new HashMap<>();
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());
        content.put("userId", event.getUserId());
        if (event.getEntityType() == Constants.ENTITY_TYPE_POST) {
            content.put("postId", event.getEntityId());
        } else if (event.getEntityType() == Constants.ENTITY_TYPE_COMMENT) {
            content.put("postId", commentService.findCommentById(event.getEntityId()).getEntityId());
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }
    @KafkaListener(topics = {Constants.EVENT_TYPE_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            throw new IllegalArgumentException("消息内容不能为空！");
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            throw new IllegalArgumentException("无效的消息格式！");
        }
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticSearchService.saveDiscussPost(post);
    }
}
