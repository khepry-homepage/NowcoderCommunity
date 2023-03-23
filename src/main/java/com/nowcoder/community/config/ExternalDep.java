package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class ExternalDep {
    @Bean
    public SimpleDateFormat getSimpleDateFormatInstance() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
