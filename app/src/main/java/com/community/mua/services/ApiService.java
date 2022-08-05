package com.community.mua.services;

import com.community.mua.bean.AlbumBean;
import com.community.mua.bean.AnniversaryBean;
import com.community.mua.bean.AvatarBean;
import com.community.mua.bean.CommonBean;
import com.community.mua.bean.CreateAccountBean;
import com.community.mua.bean.DiaryBean;
import com.community.mua.bean.Empty;
import com.community.mua.bean.LoginRecordBean;
import com.community.mua.bean.LoveCoverBean;
import com.community.mua.bean.NoteBean;
import com.community.mua.bean.PairBean;
import com.community.mua.bean.SplashBean;
import com.community.mua.bean.UserBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface ApiService {

    @POST("user/getSplash")
    Observable<CommonBean<PairBean>> getSplash(@Body Map<String, String> map);

    @POST("user/save")
    Observable<CommonBean<UserBean>> saveAccount(@Body Map<String, String> map);

    @POST("user/updateKittyStatusTime")
    Observable<CommonBean<Empty>> uploadCatFeedTime(@Body Map<String, Object> map);

    @POST("user/note/updateToppingTime")
    Observable<CommonBean<Empty>> updateToppingTime(@Body Map<String, Object> map);

    @POST("user/getUser")
    Observable<CommonBean<UserBean>> getUser(@Body Map<String, String> map);

    @POST("user/matching")
    Observable<CommonBean<PairBean>> pair(@Body Map<String, String> map);

    @POST("user/unMatch")
    Observable<CommonBean<Integer>> unPair(@Body Map<String, String> map);

    @POST("user/getUserByCode")
    Observable<CommonBean<UserBean>> getUserByCode(@Body Map<String, String> map);

    @POST("user/love/deleteLove")
    Observable<CommonBean<Empty>> deleteLoveCover(@Body Map<String, Object> map);

    @Multipart
    @POST("user/uploadHeadImg")
    Observable<CommonBean<AvatarBean>> uploadAvatar(@Part MultipartBody.Part part, @PartMap Map<String, RequestBody> map);

    @Multipart
    @POST("user/love/createLove")
    Observable<CommonBean<LoveCoverBean>> uploadLoveCover(@Part MultipartBody.Part part, @PartMap Map<String, RequestBody> map);

    @Multipart
    @POST("user/uploadAlbum")
    Observable<CommonBean<AlbumBean>> uploadAlbum(@Part MultipartBody.Part part, @PartMap Map<String, RequestBody> map);

    @Multipart
    @POST("user/uploadSplash")
    Observable<CommonBean<SplashBean>> uploadSplash(@Part MultipartBody.Part part, @PartMap Map<String, RequestBody> map);

    @POST("user/updateNickName")
    Observable<CommonBean<Object>> updateNickName(@Body Map<String, String> map);

    @POST("user/updateBirth")
    Observable<CommonBean<Object>> updateBirth(@Body Map<String, String> map);

    @Multipart
    @POST("user/diary/writeDiaryFile")
    Observable<CommonBean<Object>> writeDiaryFile(@Part MultipartBody.Part part, @PartMap Map<String, RequestBody> map);

    @POST("user/diary/writeDiary")
    Observable<CommonBean<Object>> writeDiary(@Body Map<String, String> map);

    @POST("user/note/createNote")
    Observable<CommonBean<Empty>> createNote(@Body Map<String, String> map);

    @POST("user/note/getNoteList")
    Observable<CommonBean<List<NoteBean>>> getNoteList(@Body Map<String, String> map);

    @POST("user/diary/getDiaryList")
    Observable<CommonBean<List<DiaryBean>>> getDiaryList(@Body Map<String, String> map);

    @POST("user/note/deleteNote")
    Observable<CommonBean<Empty>> deleteNote(@Body Map<String, String> map);

    @Multipart
    @POST("user/diary/editDiaryFile")
    Observable<CommonBean<Object>> editDiaryFile(@Part MultipartBody.Part part, @PartMap Map<String, RequestBody> map);

    @POST("user/diary/editDiary")
    Observable<CommonBean<Object>> editDiary(@Body Map<String, Object> map);

    @POST("user/diary/deleteDiary")
    Observable<CommonBean<Empty>> deleteDiary(@Body Map<String, String> map);

    @POST("user/loginRecord/insertRecord")
    Observable<CommonBean<Empty>> insertRecord(@Body Map<String, String> map);

    @POST("user/loginRecord/getLastRecord")
    Observable<CommonBean<LoginRecordBean>> getLastRecord(@Body Map<String, String> map);

    @POST("user/loginRecord/getPairLastRecord")
    Observable<CommonBean<List<LoginRecordBean>>> getPairLastRecord(@Body Map<String, String> map);

    @POST("user/loginRecord/getList")
    Observable<CommonBean<List<LoginRecordBean>>> getLoginRecordList(@Body Map<String, String> map);

    @POST("user/loginRecord/setIsPrivate")
    Observable<CommonBean<Empty>> setIsPrivate(@Body Map<String, Object> map);

    @POST("user/loginRecord/setIsSeeLocation")
    Observable<CommonBean<Empty>> setIsSeeLocation(@Body Map<String, Object> map);

    @POST("user/anniversary/create")
    Observable<CommonBean<Empty>> createAnniversary(@Body Map<String, Object> map);

    @POST("user/anniversary/getAllByMatchingCode")
    Observable<CommonBean<List<AnniversaryBean>>> getAllAnniversary(@Body Map<String, String> map);

    @POST("user/anniversary/edit")
    Observable<CommonBean<Empty>> updateAnniversary(@Body Map<String, Object> map);

    @POST("user/anniversary/delete")
    Observable<CommonBean<Empty>> deleteAnniversary(@Body Map<String, String> map);
}
