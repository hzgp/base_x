package com.frame.base.core.constant;

/**
 * Desc:
 * Author:zhujb
 * Date:2020/8/5
 */
public interface BaseConstants {

    String MY_SHARED_PREFERENCE = "my_shared_preference";
    String AUTHORIZATION = "Authorization";
    String BEARER = "Bearer";
    int SUCCESS = 200;
    String TOKEN_EXPIRED=" 403 "; //无权限
    String TOKEN_INVALID=" 401 ";  //TOKEN失效
    int TOKEN_INVALID_CODE= 401 ;  //TOKEN失效
    int TOKEN_VALID_OFFSET =10;
    int OPER_TYPE_MESSAGE=2;
    int OPER_TYPE_PHONE=1;

    int FOLLOWUP_STATE_HAS =1;
    int FOLLOWUP_STATE_WAITING=0;
    /**
     * url
     */
    String COOKIE = "Cookie";

    String TOKEN = "token";
    String TOKEN_INFO = "token_info";
    String VISIT_PLAN_ID = "VisitPlanId";

    String VISITOR_KEY = "visitorId";
    String PAGE = "page";
    String STATUS = "status";
    String OPER_TYPE="OperType";
    String PHONE_NUMBER="PhoneNumber";
    String SDK_APP_ID = "sdkAppId";
    String SEAT_USER_ID = "seatUserId";
    String DOCTOR_ID="doctorId";
    String PATIENT_ID="patientId";

    String VISIT_PLAN_ID_KEY = "visitPlanId";
    String _ID = "id";
    /**
     * Avoid double click time area
     */

    long DOUBLE_INTERVAL_TIME = 2000;

    String DB_NAME = "forgetsky_wan_android.db";
    String DB_HX_MESSAGE_CACHE="message_cache.db";
    /**
     * cache
     */
    String VERSION = "version";

    String CONFIG = "config";

    String HisDocSearch = "HisDocSearch";

    String HX_MESSAGE_CACHE_STATUS="messageCacheStatus";
    String IMAGE_CACHE_DIR="imageCache";
    int IMAGE_CACHE_MAX_SIZE=1024 * 1024 * 200;

    /**
     * Shared Preference key
     */
    String USER_TOKEN = "user_token";

    String LOGIN_STATUS = "login_status";

    String PUSH_TOKEN = "push_token";

    String USER_INFO = "user_info";

    String LONGITUDE = "longitude";

    String LATITUDE = "latitude";

    String HOME_BADGE_COUNT = "home_badge_count";

    String MES_BADGE_COUNT = "mes_badge_count";
    String FROM_OPEN = "from_open";
}
