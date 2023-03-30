package com.nowcoder.community.utils;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserHolder {
    private ThreadLocal<User> threadLocal = new ThreadLocal<User>();
    public void set(User user) {
        threadLocal.set(user);
    }
    public User get() {
        return threadLocal.get();
    }
    public void remove() {
        threadLocal.remove();
    }
}
