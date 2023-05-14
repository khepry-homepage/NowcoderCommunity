package com.nowcoder.community.utils;

public class Constants {
    public static final int ACTIVATION_SUCCESS = 0;
    public static final int ACTIVATION_ERROR = 1;
    public static final int ACTIVATION_REPEAT = 2;
    public static final int POST_ORDER_MODE_LATEST = 0;                     //  帖子排序方式: 最新排序
    public static final int POST_ORDER_MODE_HOTTEST = 1;                     //  帖子排序方式: 最热排序
    public static final int RESET_PASSWORD_CAPTCHA_DURATION = 300;          //  单位: s
    public static final String LOGIN_TICKET = "login_ticket";               //  登录凭证cookie名
    public static final String KAPTCHA_SESSION_KEY = "kaptcha_session_key";
    public static final String RESET_PASSWORD_KAPTCHA_SESSION_KEY = "reset_password_kaptcha_session_key";
    public static final int ENTITY_TYPE_POST = 1;                           //  评论类型为帖子评论
    public static final int ENTITY_TYPE_COMMENT = 2;                        //  评论类型为回复评论
    public static final int FOLLOW_ENTITY_TYPE_USER = 3;                    //  关注类型为用户
    public static final int FOLLOW_ENTITY_TYPE_POST = 4;                    //  关注类型为帖子
    public static final String EVENT_TYPE_LIKE = "like";                    //  事件类型：点赞
    public static final String EVENT_TYPE_COMMENT = "comment";              //  事件类型：评论
    public static final String EVENT_TYPE_FOLLOW = "follow";                //  事件类型：关注
    public static final String EVENT_TYPE_PUBLISH = "publish";              //  事件类型：发布帖子
    public static final String EVENT_TYPE_DELETE_POST = "delete-post";              //  事件类型：删除帖子
    public static final int SYSTEM_USER_ID = 1;                             //  系统用户id
    //  用户权限
    public static final int AUTHORIZATION_USER = 0;
    public static final int AUTHORIZATION_ADMIN = 1;
    public static final int AUTHORIZATION_MODERATOR = 2;
    public static final int TOKEN_DURATION = 60 * 60 * 24 * 7;              //  登录有效期，单位s
    public static final int DELETED_POST_STATUS = 2;                        //  帖子删除状态码
}
