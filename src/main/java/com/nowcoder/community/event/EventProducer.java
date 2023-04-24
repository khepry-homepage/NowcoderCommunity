package com.nowcoder.community.event;

import com.alibaba.fastjson2.JSONObject;
import com.nowcoder.community.entity.Event;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;
    public void commitEvent(Event event) {
        if (event == null || StringUtils.isBlank(event.getEventType())) {
            throw new IllegalArgumentException("传递无效事件实体！");
        }
        kafkaTemplate.send(event.getEventType(), JSONObject.toJSONString(event));
    }
}
