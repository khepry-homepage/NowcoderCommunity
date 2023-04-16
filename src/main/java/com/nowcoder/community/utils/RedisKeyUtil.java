package com.nowcoder.community.utils;

public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_LIKE_ENTITY = "like:entity";
    private static final String PREFIX_LIKE_USER = "like:user";
    public static String getLikeEntityKey(int entityType, int entityId) {
        return PREFIX_LIKE_ENTITY + SPLIT + entityType + SPLIT + entityId;
    }
    public static String getLikeUserKey(int userId) {
        return PREFIX_LIKE_USER + SPLIT + userId;
    }
}
