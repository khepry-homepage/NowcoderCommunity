package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.utils.Constants;
import com.nowcoder.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

@Service
public class FollowService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserService userService;
    public void follow(int userId, int entityType, int followeeId) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(followeeId, entityType);
                BoundZSetOperations<String, Object> followeeOperation = redisTemplate.boundZSetOps(followeeKey);
                BoundZSetOperations<String, Object> followerOperation = redisTemplate.boundZSetOps(followerKey);
                Long isMember = followeeOperation.rank(followeeId);
                operations.multi();
                if (isMember != null) {
                    followeeOperation.remove(followeeId);
                    followerOperation.remove(userId);
                } else {
                    long score = new Date().getTime();
                    followeeOperation.add(followeeId, score);
                    followerOperation.add(userId, score);
                }
                return operations.exec();
            }
        });
    }
    /**
     *
     * @param userId
     * @param entityType
     * @param isFollower true为获取用户特定实体类型的关注数，否则为被关注数
     * @return
     */
    public long getFollowCount(int userId, int entityType, boolean isFollower) {
        String key = isFollower ? RedisKeyUtil.getFollowerKey(userId, entityType)
                                : RedisKeyUtil.getFolloweeKey(userId, entityType);
        Long count = redisTemplate.opsForZSet().size(key);
        return count == null ? 0 : count;
    }
    public boolean isFollower(int userId, int followeeId, int entityType) {
        String key = RedisKeyUtil.getFollowerKey(followeeId, entityType);
        return redisTemplate.opsForZSet().rank(key, userId) != null;
    }
    public List<Map<String, Object>> findFollowees(int userId, int offset, int limit) {
        String key = RedisKeyUtil.getFolloweeKey(userId, Constants.FOLLOW_ENTITY_TYPE_USER);
        Set<Object> ids = redisTemplate.opsForZSet().reverseRange(key, offset, offset + limit - 1);
        if (ids == null) {
            return null;
        }
        List<Map<String, Object>> followees = new ArrayList<>();
        for (Object id : ids) {
            Map<String, Object> map = new HashMap<>();
            int followeeId = (Integer) id;
            User user = userService.findUserById(followeeId);
            map.put("user", user);
            map.put("followTime", new Date(redisTemplate.opsForZSet().score(key, followeeId).longValue()));
            followees.add(map);
        }
        return followees;
    }
    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit) {
        String key = RedisKeyUtil.getFollowerKey(userId, Constants.FOLLOW_ENTITY_TYPE_USER);
        Set<Object> ids = redisTemplate.opsForZSet().reverseRange(key, offset, offset + limit - 1);
        if (ids == null) {
            return null;
        }
        List<Map<String, Object>> followers = new ArrayList<>();
        for (Object id : ids) {
            Map<String, Object> map = new HashMap<>();
            int followerId = (Integer) id;
            User user = userService.findUserById(followerId);
            map.put("user", user);
            map.put("followTime", new Date(redisTemplate.opsForZSet().score(key, followerId).longValue()));
            followers.add(map);
        }
        return followers;
    }
}
