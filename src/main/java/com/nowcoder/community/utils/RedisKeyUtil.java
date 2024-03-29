package com.nowcoder.community.utils;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_LIKE_ENTITY = "like:entity";
    private static final String PREFIX_LIKE_USER = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_CAPTCHA = "captcha";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";
    private static final String PREFIX_SCHEDULED = "scheduled";
    public static String getLikeEntityKey(int entityType, int entityId) {
        return PREFIX_LIKE_ENTITY + SPLIT + entityType + SPLIT + entityId;
    }
    public static String getLikeUserKey(int userId) {
        return PREFIX_LIKE_USER + SPLIT + userId;
    }
    /**
     * key format type : followee:userId:entityType
     * 记录userId关注的实体id(用户、帖子等)
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }
    public static String getFollowerKey(int userId, int entityType) {
        return PREFIX_FOLLOWER + SPLIT + userId + SPLIT + entityType;
    }
    public static String getCaptchaKey(String ticket) {
        return PREFIX_CAPTCHA + SPLIT + ticket;
    }
    public static String getUserKey(int userId) { return PREFIX_USER + SPLIT + userId; }
    public static String getUVKey(String date) { return PREFIX_UV + SPLIT + date; }
    public static String getDAUKey(String date) { return PREFIX_DAU + SPLIT + date; }
    public static String getUVIntervalKey(Date start, Date end) { return PREFIX_UV + SPLIT + start + SPLIT + end; }
    public static String getDAUIntervalKey(Date start, Date end) { return PREFIX_DAU + SPLIT + start + SPLIT + end; }
    public static String getScheduledPostKey() { return PREFIX_SCHEDULED + SPLIT + "post"; }
}
