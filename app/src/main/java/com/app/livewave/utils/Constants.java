package com.app.livewave.utils;

public class Constants {

    public static float screenWidth = 0.0F;
    public static float screenHeight = 0.0F;
    public static final String currentUser = "currentUser";
    public static final String STREAMS_LIST = "streams_list";
    public static final String isLogin = "isLogin";
    public static final String token = "token";
    public static final String categoryList = "categoryList";
    public static String FirestoreBaseDir = "LiveWaves2.0/";
    public static String firebaseDatabaseRoot = "Conversations";
    public static final String forceLogout = "forceLogout";

    //Subscription durations
    public static final String[] duration = {"1 Day", "1 Week (7 Days)", "1 Month (30 Days)", "3 Months (90 Days)", "6 Months (180 Days)", "1 Year (365 Days)", "Lifetime"};

    public static final String HIDE_HEADER = "hideHeader";
    public static final String HEADER_TITLE = "headerTitle";
    public static final String FRAGMENT_TITLE = "fragmentTitle";
    public static final String HAS_EXTRA = "has_extra";


    public static final String tokenId = "tokenId";
    public static final String serverURL = "wss://stream-prod.livewaves.app/WebRTCAppEE/websocket";//"wss://www.streaming.globalfansy.fi:5443/WebRTCAppEE/websocket";


    //if app is in APP_NOT_IN_REVIEW show every thing normally if APP_NOT_IN_REVIEW hide everything that says payment
    public static final String APP_NOT_IN_REVIEW = "app_not_in_review";
    //API
    //staging url
//     public static String BASE_URL = "https://livewavesapp.com/" ;
    // live url
    public static String BASE_URL = "https://livewaves.app/";//"https://livewaves.app/" ;
    public static String BASE_APIS_URL = "https://livewaves.app/api/";//"https://livewaves.app/api/";
    public static String API_VERSION = "v1/";
    public static final String USER_LOGIN = "login";
    public static final String USER_REGISTER = "register";
    public static final String FORGET_PASSWORD = "forgetPassword";
    public static final String RESET_PASSWORD = "resetPassword";
    public static final String CREATE_STREAM_API = "stream/create";
    public static final String USER_PROFILE = "profile";
    public static final String USER_POST = "posts/profile";
    public static final String GLOBAL_STREAM_API = "streams/global";
    public static final String FOLLOW_UNFOLLOW_ALI = "follow/{id}";
    public static final String BLOCK_UNBLOCK = "block/{id}";
    public static final String FOLLOWING_STREAMS_API = "streams/following";
    public static final String CATEGORIES_API = "categories";
    public static final String USER_REACTION = "post";
    public static final String SHARE_POSTS_COUNT = "posts/{id}/shares/{sharingId}";
    public static final String VIDEO_POSTS_COUNT = "waves/{id}/viewers/{sharingId}";
    public static final String READ_MESSAGES = "read/messages/{id}";
    public static final String UNREAD_MESSAGES = "unread/messages/count";
    public static final String NEWS_FEED = "newsfeed";
    public static final String WAVES_ITEMS_LIST = "waves";
    public static final String WAVES_ITEMS_LIST_WITH_ID = "waves/{id}";
    public static final String TRENDING_TRACK = "tracks/trending";
    public static final String TRACK_COUNT_NUMBER = "tracks/{id}/viewers/{trackId}";
    public static final String REELS_COUNT_NUMBER = "waves/{id}/viewers/{postId}";
    public static final String PAID_POSTS = "user/paidposts";
    public static final String PAID_EVENTS = "user/paidevents";
    public static final String NOTIFICATIONS = "notifications";
    public static final String NOTIFICATIONS_COUNT = "notifications/unreadnotificationcount";
    public static final String GET_EVENTS_API = "events";
    public static final String GET_SUBSCRIPTION_API = "subscriptionlisting";
    public static final String DELETE_SUBSCRIPTION_API = "subscription/delete";
    public static final String STATUS_SUBSCRIPTION_API = "subscriptionstatuschange";
    public static final String STATUS_PURCHASED_SUBSCRIPTION_API = "purchasedsubscriptionstatuschange";
    public static final String DELETE_PURCHASED_SUBSCRIPTION_API = "purchasedsubscription/delete";
    public static final String GET_PURCHASED_SUBSCRIPTION = "purchasedsubscriptionlisting";
    public static final String TRENDING_HASHTAGS_API = "hashtags/trending";
    public static final String SUGGESTED_PEOPLE = "suggestedProfiles";
    public static final String CREATE_POST = "post";
    public static final String ADD_POST = "post/add";
    public static final String EDIT_POST_API = "post/edit";
    public static final String FOLLOWERS_API = "followers";
    public static final String FOLLOWING_API = "following";
    public static final String FRIEND_REQUEST_API = "requests";
    public static final String CREATE_EVENT_API = "event/create";
    public static final String EDIT_EVENT_API = "event/edit";
    public static final String EVENT_CHECK_IN_API = "event/checkin";
    public static final String CREATE_STREAM_EVENT_API = "event/stream";
    public static final String UPDATE_PROFILE_API = "updateProfile";
    public static final String CHANGE_PASSWORD_API = "updatePassword";
    public static final String GET_EVENT_API = "event/{id}";
    public static final String DELETE_EVENT_API = "event/delete/{id}";
    public static final String GET_POST_API = "post/{id}";
    public static final String GET_COMMENTS_API = "post/{id}/comments";
    public static final String SAVE_REACTION_ON_A_COMMENT = "post/{post_id}/comment/{reaction}/reaction/{comment_id}";
    public static final String REMOVE_COMMENTS_API = "comment/delete/{id}";
    public static final String SEARCH_API = "search";
    public static final String UPDATE_PROFILE_PHOTO = "updateProfilePhoto";
    public static final String UPDATE_COVER = "updateCover";
    public static final String ADD_COMMENT_API = "comment";
    public static final String ADD_COMMENT_REPLY_API = "comment/reply";
    public static final String EDIT_COMMENT_API = "comment/edit";
    public static final String EDIT_COMMENT_REPLY_API = "comment/reply/edit";
    public static final String HASHTAG_POSTS = "posts/hashtag/{tag}";
    public static final String DELETE_POST = "post/delete/{id}";
    public static final String DELETE_NOTIFICATION = "notification/delete/{id}";
    public static final String WITHDRAWAL = "payments/withdrawal";
    public static final String RECEIVINGS = "payments/receivings";
    public static final String TRANSFERS = "payments/transfers";
    public static final String SETTINGS_API = "setting/app";
    public static final String LOGOUT_API = "setting/app";
    public static final String UPDATE_FCM = "updateFCM";
    public static final String INVITE_STREAM_API = "stream/{streamId}/profile/{profileId}/invite";
    public static final String STREAM_JOIN_API = "stream/join";
    public static final String GET_STREAM_BY_ID_API = "stream/{id}";

    public static final String START_STREAM = "stream/start/{id}";
    public static final String COMPLETE_STREAM = "stream/complete/{id}";
    public static final String REMOVED = "removed";
    public static final String GLOBAL_STREAMS = "Global";
    public static final String FOLLOWING = "Following";

    public static final String CATEGORY = "category";
    public static final String CATEGORY_ID = "category_id";
    public static final String SUB_CATEGORY = "sub_category";
    public static final String SUB_CATEGORY_ID = "sub_category_id";

    public static final String POST_CREATE_DIALOG = "post_create_dialog";
    public static final String WAVE_CREATE_DIALOG = "wave_create_dialog";
    public static final String POST_EDIT_DIALOG = "post_edit_dialog";


    public static final String SPECIFIC_USER_ID = "specific_user_id";
    public static final String SPECIFIC_POST_ID = "specific_post_id";

    public static final String MY_EVENTS = "my_events";
    public static final String MY_SUBSCRIPTIONS = "my_subscriptions";

    public static final String CREATE_SUBSCRIPTION = "subscriptions/create";
    public static final String EDIT_SUBSCRIPTION = "subscription/update";

    public static final String GOING_EVENTS = "going_events";
    public static final String USER_EVENTS = "user_EVENTS";
    public static final String USER_ID = "user_id";
    public static final String DATE_TIME = "date_time";
    public static final String SELECTED_DATE_TIME = "selected_date_time";
    public static final String STATUS = "status";
    public static final String CHECKED_IN = "checkedin";
    public static final String PURCHASED_SUBSCRIPTIONS = "purchased_subscriptions";
    public static final String SPECIFIC_DATE = "specific_date";
    public static final String EVENT_ID = "event_id";
    public static final String EVENT_OBJ = "event obj";
    public static final String SUBSCRIPTION_OBJ = "subscription obj";
    public static final String EDIT_EVENT = "edit_event";
    public static final String EDIT_SUBSCRIPTION_PLAN = "edit_event";
    public static final String TYPE = "type";
    public static final String EVENT_TIME = "time";
    public static final String USER_NAME = "user_name";
    public static final String FCM_TOKEN = "fcm_token";
    public static final String ACCOUNT_ANALYTICS = "analytics/account";
    public static final String AVERAGE_EARNINGS = "analytics/peractivityearning";
    public static final String MONTHLY_EARNINGS = "analytics/yearlyearning";

    public static final String POST_MODEL = "post_model";
    public static final String POST_ID = "post_id";
    public static final String MY_PROFILE = "my_profile";
    public static final String OTHER_PERSON_PROFILE = "other_profile";

    public static final int AUTOCOMPLETE_REQUEST_CODE = 111;
    public static final int CATEGORY_SELECTION_REQUEST_CODE = 100;
    public static final int EVENT_SCREEN_REQUEST_CODE = 101;
    public static final int REQUEST_IMAGE_CAPTURE = 0;
    public static final int REQUEST_VIDEO_CAPTURE = 0;
    public static final int REQUEST_GALLERY_IMAGE = 1;
    public static final int REQUEST_GALLERY_VIDEO = 2;
    public static final int REQUEST_EVENT_IMAGE = 3;

    private String baseImage = "posts/images/";//for Live
    private String baseVideo = "posts/videos/";//for Live
    public static String baseEvents = "events/";//for Live

    public static final String[] imagesExtensions = new String[]{"jpeg", "png", "jpg", "gif", "svg"};
    public static final String[] videosExtensions = new String[]{"mp4", "webm", "avi", "3gp", "flv", "ogg"};

    public static final String URL = "URL";
    public static final String VIDEO_VIEW_COUNT = "Video_View_Count";
    public static final String VIDEO_SHARE_COUNT = "Video_Share_Count";
    public static final String HASH_TAG = "hashTag";
    public static final String VIEWS = "views";
    public static final String LIKE = "like";
    public static ENV APPENV = ENV.development;

}
