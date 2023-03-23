package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class AlphaServiceImpl implements AlphaService {
    @Autowired
    private AlphaDao alphaDao;
    @Autowired
    private SimpleDateFormat simpleDateFormat;
    @Override
    public String getUserInfo() {
        return alphaDao.find();
    }
    @PostConstruct
    private void init() {
        System.out.println("initialize member..." + simpleDateFormat.format(new Date()));
    }
    @PreDestroy
    private void destroy() {
        System.out.println("destroy member..." + simpleDateFormat.format(new Date()));
    }
}
