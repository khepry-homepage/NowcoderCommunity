package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redis配置类
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        /**
         * RedisTemplate默认存储的序列化方式是JDK的序列化方式，需要重新配置序列化方式
         * ValueOperations<String, String> value = redisTemplate.opsForValue();
         *         value.set("name","pzy");
         *         value.set("auther","pzyzx",10); //向右偏移10个字符
         *         value.set("auther1","pzyzx", Duration.ofSeconds(10)); //10秒后过期
         *         String name = value.get("name");//\xac\xed\x00\x05t\x00\x07auther1
         *
         * ————————————————
         * 版权声明：本文为CSDN博主「pingzhuyan」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
         * 原文链接：https://blog.csdn.net/pingzhuyan/article/details/125060695
         */
        template.setConnectionFactory(factory);
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(RedisSerializer.json());
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(RedisSerializer.json());

        return template;
    }
}
