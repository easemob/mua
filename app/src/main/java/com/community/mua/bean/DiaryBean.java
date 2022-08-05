package com.community.mua.bean;

import java.io.Serializable;

public class DiaryBean implements Serializable {
    /**
     * 日记ID
     */
    private String diaryId;

    /**
     * 用户匹配码
     */
    private String matchingCode;

    /**
     * 用户ID
     */
    private String userId;
    /**
     * 日记月份
     */
    private String diaryMonth;

    /**
     * 日记时间戳
     */
    private long diaryTimeStamp;


    /**
     * 心情
     */
    private String mood;


    /**
     * 心情场景
     */
    private String moodReason;



    /**
     * 内容
     */
    private String content;


    /**
     * 照片
     */
    private String pic;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 是否私密
     */
    private boolean isPrivate;

    /**
     * 注册时间
     */
    private String updateTime;

    public String getDiaryId() {
        return diaryId;
    }

    public void setDiaryId(String diaryId) {
        this.diaryId = diaryId;
    }

    public String getMatchingCode() {
        return matchingCode;
    }

    public void setMatchingCode(String matchingCode) {
        this.matchingCode = matchingCode;
    }

    public String getDiaryMonth() {
        return diaryMonth;
    }

    public void setDiaryMonth(String diaryMonth) {
        this.diaryMonth = diaryMonth;
    }

    public long getDiaryTimeStamp() {
        return diaryTimeStamp;
    }

    public void setDiaryTimeStamp(long diaryTimeStamp) {
        this.diaryTimeStamp = diaryTimeStamp;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getMoodReason() {
        return moodReason;
    }

    public void setMoodReason(String moodReason) {
        this.moodReason = moodReason;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
