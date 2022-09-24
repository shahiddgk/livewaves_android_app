package com.app.livewave.retrofit;

import com.app.livewave.models.CommentReplyRequestModel;
import com.app.livewave.models.CommentRequestModel;
import com.app.livewave.models.JoinStreamModel;
import com.app.livewave.models.ParameterModels.AuthModel;
import com.app.livewave.models.ParameterModels.ChangePasswordRequestModel;
import com.app.livewave.models.ParameterModels.CreateEventRequestModel;
import com.app.livewave.models.ParameterModels.CreatePostModel;
import com.app.livewave.models.ParameterModels.CreateStreamModel;
import com.app.livewave.models.ParameterModels.HashtagsModel;
import com.app.livewave.models.ReadUnreadMessagesNotification;
import com.app.livewave.models.RequestModels.AddTrackToPlaylistModel;
import com.app.livewave.models.RequestModels.ChangeSubscriptionStatus;
import com.app.livewave.models.RequestModels.CreateSubscriptionRequestModel;
import com.app.livewave.models.RequestModels.PlaylistCreateModel;
import com.app.livewave.models.RequestModels.TrackUploadModel;
import com.app.livewave.models.ResponseModels.AccountAnalytics;
import com.app.livewave.models.ResponseModels.AlertModel;
import com.app.livewave.models.ResponseModels.AlertModelNew;
import com.app.livewave.models.ResponseModels.ApiResponse;
import com.app.livewave.models.ResponseModels.AverageEarnings;
import com.app.livewave.models.ResponseModels.CategoryModel;
import com.app.livewave.models.ResponseModels.CommentModel;
import com.app.livewave.models.ResponseModels.CreateStreamResponseModel;
import com.app.livewave.models.ResponseModels.EventModel;
import com.app.livewave.models.ResponseModels.FollowModel;
import com.app.livewave.models.ResponseModels.GenericDataModel;
import com.app.livewave.models.ResponseModels.MonthlyEarnings;
import com.app.livewave.models.ResponseModels.PlayListModel;
import com.app.livewave.models.ResponseModels.PostModel;
import com.app.livewave.models.ResponseModels.PurchasedSubscription;
import com.app.livewave.models.ResponseModels.PurchasedSubscriptionModel;
import com.app.livewave.models.ResponseModels.ReactionModel;
import com.app.livewave.models.ResponseModels.ReceivingModel;
import com.app.livewave.models.ResponseModels.ReplyModel;
import com.app.livewave.models.ResponseModels.SaveReactionOnComment;
import com.app.livewave.models.ResponseModels.SubscriptionModel;
import com.app.livewave.models.ResponseModels.SubscriptionPlan;
import com.app.livewave.models.ResponseModels.Track;
import com.app.livewave.models.ResponseModels.TransferModel;
import com.app.livewave.models.ResponseModels.TrendingTrack;
import com.app.livewave.models.ResponseModels.UserModel;
import com.app.livewave.models.ResponseModels.UserPublicTracks;
import com.app.livewave.models.ResponseModels.WavesModelResponse;
import com.app.livewave.models.ResponseModels.WithdrawalModel;
import com.app.livewave.models.SaveStreamAsPost;
import com.app.livewave.models.SettingsApiModel;
import com.app.livewave.models.StreamModel;
import com.app.livewave.models.SuggestedPeopleModel;
import com.app.livewave.utils.Constants;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    @POST(Constants.USER_LOGIN)
    Call<ApiResponse<UserModel>> loginUser(@Body AuthModel authModel);

    @POST(Constants.USER_REGISTER)
    Call<ApiResponse<UserModel>> registerUser(@Body AuthModel authModel);

    @POST(Constants.FORGET_PASSWORD)
    Call<ApiResponse<String>> forgetPassword(@Body AuthModel authModel);

    @POST(Constants.RESET_PASSWORD)
    Call<ApiResponse<String>> resetPassword(@Body HashMap<String, Object> body);

    @POST(Constants.CREATE_STREAM_API)
    Call<ApiResponse<CreateStreamResponseModel>> createStream(@Body CreateStreamModel createStreamModel);

    @POST("updateFCM")
    Call<ApiResponse<UserModel>> updateFCMToken(@Body String token);

    @POST(Constants.CREATE_EVENT_API)
    Call<ApiResponse<EventModel>> createEvent(@Body CreateEventRequestModel createEventRequestModel);

    @POST(Constants.CREATE_SUBSCRIPTION)
    Call<ApiResponse<Object>> createSubscription(@Body CreateSubscriptionRequestModel createSubscriptionRequestModel);

    @POST(Constants.EDIT_SUBSCRIPTION)
    Call<ApiResponse<Object>> editSubscriptionPlanNew(@Body CreateSubscriptionRequestModel createSubscriptionRequestModel);

    @POST(Constants.EDIT_EVENT_API)
    Call<ApiResponse<EventModel>> editEvent(@Body CreateEventRequestModel createEventRequestModel);

    @FormUrlEncoded
    @POST(Constants.EVENT_CHECK_IN_API)
    Call<ApiResponse<EventModel>> checkInEvent(@Field("event_id") String eventId);

    @FormUrlEncoded
    @POST(Constants.CREATE_STREAM_EVENT_API)
    Call<ApiResponse<StreamModel>> streamEvent(@Field("event_id") int eventId, @Field("platformID") String platformID);

    @GET(Constants.START_STREAM)
    Call<ApiResponse<StreamModel>> startStream(@Path("id") Integer id);

    @GET(Constants.COMPLETE_STREAM)
    Call<ApiResponse<StreamModel>> completeStream(@Path("id") String id);

    @POST(Constants.UPDATE_PROFILE_API)
    Call<ApiResponse<UserModel>> upDateProfile(@Body UserModel eventId);

    @POST(Constants.CHANGE_PASSWORD_API)
    Call<ApiResponse<String>> changePasswordApi(@Body ChangePasswordRequestModel changePasswordRequestModel);

    @POST(Constants.ADD_COMMENT_API)
    Call<ApiResponse<CommentModel>> postComment(@Body CommentRequestModel commentRequestModel);

    @POST(Constants.ADD_COMMENT_REPLY_API)
    Call<ApiResponse<ReplyModel>> postCommentReply(@Body CommentReplyRequestModel commentReplyRequestModel);

    @POST(Constants.EDIT_COMMENT_API)
    Call<ApiResponse<CommentModel>> editComment(@Body CommentRequestModel commentRequestModel);

    @POST(Constants.EDIT_COMMENT_REPLY_API)
    Call<ApiResponse<ReplyModel>> editCommentReply(@Body CommentReplyRequestModel commentReplyRequestModel);

    @GET(Constants.SAVE_REACTION_ON_A_COMMENT)
    Call<ApiResponse<SaveReactionOnComment>> reactionOnComment(@Path("post_id") int postId, @Path("reaction") int reaction, @Path("comment_id") int commentId);

    @FormUrlEncoded
    @POST(Constants.UPDATE_PROFILE_PHOTO)
    Call<ApiResponse<UserModel>> updateProfile(@Field("photo") String photo);

    @FormUrlEncoded
    @POST(Constants.UPDATE_FCM)
    Call<ApiResponse<UserModel>> updateFCM(@Field("fcm_token") String fcm_token);

    @FormUrlEncoded
    @POST(Constants.UPDATE_COVER)
    Call<ApiResponse<UserModel>> updateCover(@Field("cover_photo") String cover_photo);

    @FormUrlEncoded
    @POST(Constants.STREAM_JOIN_API)
    Call<ApiResponse<JoinStreamModel>> joinStream(@Field("stream_id") String streamId, @Query("type") String type);

    @GET(Constants.USER_PROFILE + "/{id}")
    Call<ApiResponse<UserModel>> getProfile(@Path("id") String id);

    @GET(Constants.GLOBAL_STREAM_API)
    Call<ApiResponse<GenericDataModel<StreamModel>>> getGlobalStreams();

    @GET(Constants.FOLLOW_UNFOLLOW_ALI)
    Call<ApiResponse<String>> followUnfollowUser(@Path("id") int id);

    @GET(Constants.BLOCK_UNBLOCK)
    Call<ApiResponse<UserModel>> blockUnblockUser(@Path("id") int id);

    @GET(Constants.FOLLOWING_STREAMS_API)
    Call<ApiResponse<GenericDataModel<StreamModel>>> getFollowingStreams();

    @GET(Constants.USER_POST + "/{id}")
    Call<ApiResponse<GenericDataModel<PostModel>>> getPost(@Path("id") int id, @Query("page") int page);

    @GET(Constants.CATEGORIES_API)
    Call<ApiResponse<List<CategoryModel>>> getCategories();

    @GET(Constants.USER_REACTION + "/{postId}/reaction/{id}")
    Call<ApiResponse<ReactionModel>> react(@Path("postId") int postId, @Path("id") int id);

    @GET(Constants.GET_POST_API)
    Call<ApiResponse<PostModel>> getPostById(@Path("id") String id);

    @GET(Constants.GET_COMMENTS_API)
    Call<ApiResponse<GenericDataModel<CommentModel>>> getCommentByPostId(@Path("id") int id, @Query("page") int page);

    @GET(Constants.REMOVE_COMMENTS_API)
    Call<ApiResponse<GenericDataModel<String>>> removeComment(@Path("id") String id);

    @GET(Constants.HASHTAG_POSTS)
    Call<ApiResponse<GenericDataModel<PostModel>>> getPostByHashTag(@Path("tag") String tag, @Query("page") int page);

    @GET(Constants.NEWS_FEED)
    Call<ApiResponse<GenericDataModel<PostModel>>> getNewsFeed(@Query("page") int page);

    @GET(Constants.WAVES_ITEMS_LIST)
    Call<ApiResponse<List<WavesModelResponse>>> getWavesListItems(@Query("page") int page);

    @GET(Constants.WAVES_ITEMS_LIST_WITH_ID)
    Call<ApiResponse<List<WavesModelResponse>>> getWavesListItemsWithId(@Path("id")  int id);

    @GET(Constants.PAID_POSTS)
    Call<ApiResponse<GenericDataModel<PostModel>>> getPaidPosts(@Query("page") int page);

//    @GET(Constants.NOTIFICATIONS)
//    Call<ApiResponse<GenericDataModel<List<AlertModelNew>>>> getNotifications(@Query("page") int page);

    @GET(Constants.NOTIFICATIONS)
    Call<ApiResponse<List<AlertModelNew>>> getNotifications();

    @GET(Constants.NOTIFICATIONS+ "/{senderId}/{receiverId}")
    Call<ApiResponse<Object>> saveMessageNotifications(@Path("senderId") String senderId,@Path("receiverId") String receiverId);

    @GET(Constants.NOTIFICATIONS_COUNT)
    Call<ApiResponse<Object>> getNotificationsCount();

    @POST(Constants.CREATE_POST)
    Call<ApiResponse<PostModel>> createPost(@Body CreatePostModel createPostModel);

    @POST(Constants.EDIT_POST_API)
    Call<ApiResponse<PostModel>> editPost(@Body CreatePostModel createPostModel);

    @GET(Constants.FOLLOWERS_API)
    Call<ApiResponse<GenericDataModel<FollowModel>>> getFollowers(@Query("q") String q);

    @GET(Constants.FOLLOWING_API)
    Call<ApiResponse<GenericDataModel<FollowModel>>> getFollowings(@Query("q") String q);

    @GET(Constants.INVITE_STREAM_API)
    Call<ApiResponse<GenericDataModel<String>>> inviteToStream(@Path("streamId") int streamId, @Path("profileId") int profileId, @Query("type") String type);

    @GET(Constants.FOLLOWERS_API + "/{userId}")
    Call<ApiResponse<GenericDataModel<FollowModel>>> getFollowers(@Path("userId") String userId, @Query("page") int page);

    //for check_in
    @GET(Constants.GET_EVENTS_API)
    Call<ApiResponse<GenericDataModel<EventModel>>> getEvents(@Query("user_id") String userID, @Query("status") String status, @Query("dateTime") String dateTime);

    @GET(Constants.GET_SUBSCRIPTION_API + "/{user_id}")
    Call<ApiResponse<List<SubscriptionModel>>> getSubscription(@Path("user_id") String userID);

    @GET(Constants.DELETE_SUBSCRIPTION_API + "/{subscription_id}")
    Call<ApiResponse<List<Object>>> deleteSubscriptionPlan(@Path("subscription_id") Integer subscriptionID);

    @POST(Constants.STATUS_SUBSCRIPTION_API)
    Call<ApiResponse<Object>> statusSubscriptionPlan(@Body ChangeSubscriptionStatus changeSubscriptionStatus);

    @POST(Constants.STATUS_PURCHASED_SUBSCRIPTION_API)
    Call<ApiResponse<Object>> statusPurchasedSubscriptionPlan(@Body ChangeSubscriptionStatus changeSubscriptionStatus);

    @GET(Constants.DELETE_PURCHASED_SUBSCRIPTION_API + "/{subscription_id}")
    Call<ApiResponse<List<Object>>> deletePurchasedSubscriptionPlan(@Path("subscription_id") Integer subscriptionID);

    @GET(Constants.GET_PURCHASED_SUBSCRIPTION)
    Call<ApiResponse<List<PurchasedSubscriptionModel>>> getPurchasedSubscription();

    @GET(Constants.GET_EVENTS_API)
    Call<ApiResponse<GenericDataModel<EventModel>>> getPaidEvents();

    @GET(Constants.GET_EVENT_API)
    Call<ApiResponse<EventModel>> getEvent(@Path("id") String id);

    @GET(Constants.DELETE_EVENT_API)
    Call<ApiResponse<EventModel>> deleteEvent(@Path("id") String path);

    @GET(Constants.FOLLOWING_API + "/{userId}")
    Call<ApiResponse<GenericDataModel<FollowModel>>> getFollowings(@Path("userId") String userId, @Query("page") int page);

    @GET(Constants.FRIEND_REQUEST_API)
    Call<ApiResponse<GenericDataModel<UserModel>>> getFriends(@Query("id") int id, @Query("page") int page);

    @GET(Constants.USER_PROFILE + "/{id}/{acceptOrReject}")
    Call<ApiResponse<String>> acceptOrReject(@Path("id") int id, @Path("acceptOrReject") String acceptOrReject);

    @GET(Constants.TRENDING_HASHTAGS_API)
    Call<ApiResponse<List<HashtagsModel>>> getTrendingHashtags();

    @GET(Constants.SUGGESTED_PEOPLE)
    Call<ApiResponse<List<SuggestedPeopleModel>>> getSuggestPeople();

    @GET(Constants.ADD_POST+"/{id}")
    Call<ApiResponse<Object>> AddPostToTimeLine(@Path("id") int id);

    @GET(Constants.SEARCH_API)
    Call<ApiResponse<List<FollowModel>>> searchUser(@Query("q") String q, @Query("type") String type);

    @GET(Constants.SEARCH_API)
    Call<ApiResponse<List<HashtagsModel>>> searchHashTags(@Query("q") String q, @Query("type") String type);

    @GET(Constants.SEARCH_API)
    Call<ApiResponse<List<EventModel>>> searchEvent(@Query("q") String q, @Query("type") String type);

    @GET(Constants.DELETE_POST)
    Call<ApiResponse<PostModel>> deletePostById(@Path("id") String postId);

    @GET(Constants.DELETE_NOTIFICATION)
    Call<ApiResponse<Object>> deleteNotificationById(@Path("id") String postId);

    @GET(Constants.WITHDRAWAL)
    Call<ApiResponse<GenericDataModel<WithdrawalModel>>> getWithdrawal(@Query("page") int page);

    @GET(Constants.RECEIVINGS)
    Call<ApiResponse<GenericDataModel<ReceivingModel>>> getReceivings(@Query("page") int page);

    @GET(Constants.TRANSFERS)
    Call<ApiResponse<GenericDataModel<TransferModel>>> getTransfers(@Query("page") int page);

    @GET(Constants.SETTINGS_API)
    Call<ApiResponse<SettingsApiModel>> getSettings();

    @GET(Constants.LOGOUT_API)
    Call<ApiResponse<String>> logout();

    @GET(Constants.GET_STREAM_BY_ID_API)
    Call<ApiResponse<StreamModel>> getStream(@Path("id") String id);

    @GET(Constants.ACCOUNT_ANALYTICS)
    Call<ApiResponse<AccountAnalytics>> getAccountAnalytics();

    @GET(Constants.AVERAGE_EARNINGS)
    Call<ApiResponse<AverageEarnings>> getAverageEarnings();

    @GET(Constants.MONTHLY_EARNINGS)
    Call<ApiResponse<MonthlyEarnings>> getMonthlyEarnings();

    @GET("post/{id}/process")
    Call<ApiResponse<String>> postProcess(@Path("id") int id);

    @GET("post/{postId}/reactions")
    Call<ApiResponse<GenericDataModel<ReactionModel>>> getPostReactions(@Path("postId") String postId);

    @GET("comment/{commentId}/reactions")
    Call<ApiResponse<GenericDataModel<ReactionModel>>> getCommentReactions(@Path("commentId") String commentId);

    @POST("report")
    Call<ApiResponse<UserModel>> report(@Body HashMap<String, Object> body);

    @POST("post/share")
    Call<ApiResponse<PostModel>> sharePost(@Body HashMap<String, Object> body);

    @GET("my-tracks")
    Call<ApiResponse<List<Track>>> getTracks();

    @GET(Constants.TRENDING_TRACK)
    Call<ApiResponse<List<Track>>> getTrendingTracks(@Query("user_id") Integer user_id);

    @GET(Constants.TRACK_COUNT_NUMBER)
    Call<ApiResponse<Object>> addCountToTracks(@Path("id") Integer id,@Path("trackId") Integer trackId);

    @GET(Constants.SHARE_POSTS_COUNT)
    Call<ApiResponse<Object>> addCountToPostForShare(@Path("id") Integer id,@Path("sharingId") String sharingId);

    @GET(Constants.VIDEO_POSTS_COUNT)
    Call<ApiResponse<Object>> addCountToVideoPostForViews(@Path("id") Integer id, @Path("sharingId") String sharingId);

    @GET(Constants.READ_MESSAGES)
    Call<ApiResponse<List<ReadUnreadMessagesNotification>>> readMessages(@Path("id") Integer id);

    @GET(Constants.UNREAD_MESSAGES)
    Call<ApiResponse<List<ReadUnreadMessagesNotification>>> unReadMessages();

    @GET(Constants.REELS_COUNT_NUMBER)
    Call<ApiResponse<Object>> addCountToWaves(@Path("id") Integer id,@Path("postId") Integer postId);

//    @GET("tracks/getuserspublictracks/{userId}")
//    Call<ApiResponse<List<Track>>> getUserTracks(@Path("id") String id);

    @GET("tracks/getuserspublictracks/{id}")
    Call<ApiResponse<List<Track>>> getUserTracks(@Path("id") String id);

    @POST("tracks/create")
    Call<ApiResponse<List<Track>>> uploadTrack(@Body TrackUploadModel trackUploadModel);

    @POST("tracks/edit")
    Call<ApiResponse<List<Track>>> editTrack(@Body TrackUploadModel trackUploadModel);

    @GET("tracks/delete/{id}")
    Call<ApiResponse<List<Track>>> deleteTrack(@Path("id") int id);

    //Subscription APIs

    @GET("subscriptions")
    Call<ApiResponse<List<SubscriptionPlan>>> subscriptionPlans();

    @GET("subscription/{id}")
    Call<ApiResponse<List<SubscriptionPlan>>> subscriptionPlans(@Path("id") String id);

    @POST("subscriptions/create")
    Call<ApiResponse<List<SubscriptionPlan>>> createNewSubscriptionPlan(@Body SubscriptionPlan subscriptionPlan);

    @POST("subscription/update")
    Call<ApiResponse<List<SubscriptionPlan>>> editSubscription(@Body SubscriptionPlan subscriptionPlan);

    @GET("subscription/delete/{id}")
    Call<ApiResponse<List<SubscriptionPlan>>> deleteSubscription(@Path("id") int id);

    @GET("purchasedsubscription/delete/{id}")
    Call<ApiResponse<List<SubscriptionPlan>>> unsubscribeSubscription(@Path("id") int id);

    @GET("purchasesubscription/{sid}/{uid}")
    Call<ApiResponse<List<SubscriptionPlan>>> subscribeSubscription(@Path("sid") int sid, @Path("uid") int uid);

    @POST("subscriptionstatuschange")
    Call<ApiResponse<List<SubscriptionPlan>>> subscriptionStatusChange(@Body SubscriptionPlan subscriptionPlan);

    @GET("purchasedsubscriptionlisting")
    Call<ApiResponse<List<PurchasedSubscription>>> getPurchasedSubscriptions();

    //Playlist API's

    @GET("playlist")
    Call<ApiResponse<List<PlayListModel>>> getMyPlaylist();

    @GET("usersplaylist/{id}")
    Call<ApiResponse<List<PlayListModel>>> getUserPlaylist(@Path("id") String id);

    @POST("usersplaylists/create")
    Call<ApiResponse<List<PlaylistCreateModel>>> createNewPlaylist(@Body PlaylistCreateModel playlistCreateModel);

    @POST("updateusersplaylists/update")
    Call<ApiResponse<List<PlaylistCreateModel>>> updatePlaylist(@Body PlaylistCreateModel playlistCreateModel);

    @POST("track/add-to-playlist")
    Call<ApiResponse<List<PlayListModel>>> addTrackToPlaylist(@Body AddTrackToPlaylistModel addTrackToPlaylistModel);

    @GET("track/add-to-playlist/{id}")
    Call<ApiResponse<String>> addTrackToPlaylist(@Path("id") int id);

    @GET("playlist/remove/{id}")
    Call<ApiResponse<List<PlayListModel>>> deletePlaylist(@Path("id") int id);

    @GET("playlist/removePlayListTrack/{pid}/{tid}")
    Call<ApiResponse<List<Track>>> removeTrackFromPlaylist(@Path("pid") int pid, @Path("tid") int tid);

    @GET("tracks")
    Call<ApiResponse<List<Track>>> getFollowingTracks();

    // save stream as post
    @POST("post/updateStreamSavedAsPost")
    Call<ApiResponse<SaveStreamAsPost>> saveStreamAsPost(@Body SaveStreamAsPost saveStreamAsPost);

    @GET("post/updateStreamSavedAsPost/{id}")
    Call<ApiResponse<List<SaveStreamAsPost>>> getSavedStream(@Path("id") int userId);
}
