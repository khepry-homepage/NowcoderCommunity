package com.nowcoder.community.service;

import com.nowcoder.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    /**
     *
     * @param userId        点赞人
     * @param entityType    点赞是帖子或评论
     * @param entityId      点赞的帖子或者评论id
     */
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                String entityKey = RedisKeyUtil.getLikeEntityKey(entityType, entityId);
                String likeKey = RedisKeyUtil.getLikeUserKey(entityUserId);
                BoundSetOperations setOperations = redisTemplate.boundSetOps(entityKey);
                BoundValueOperations valueOperations = redisTemplate.boundValueOps(likeKey);
                boolean isMember = setOperations.isMember(userId);
                operations.multi();
                //  取消点赞 or 点赞
                if (isMember) {
                    setOperations.remove(userId);
                    valueOperations.decrement();
                } else {
                    setOperations.add(userId);
                    valueOperations.increment();
                }
                return operations.exec();
            }
        });
    }

    /**
     * 获取帖子或评论的点赞数量
     * @param entityType
     * @param entityId
     * @return
     */
    public long findLikeCount(int entityType, int entityId) {
        String key = RedisKeyUtil.getLikeEntityKey(entityType, entityId);
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 获取某个用户对特定帖子或评论的点赞状态
     * @param userId
     * @param entityType
     * @param entityId
     * @return  已点赞返回1，否则0
     */
    public int findLikeStatusById(int userId, int entityType, int entityId) {
        String key = RedisKeyUtil.getLikeEntityKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(key, userId) ? 1 : 0;
    }
    public int findUserLikeCount(int userId) {
        String key = RedisKeyUtil.getLikeUserKey(userId);
        return (Integer) redisTemplate.opsForValue().get(key);
    }
}
