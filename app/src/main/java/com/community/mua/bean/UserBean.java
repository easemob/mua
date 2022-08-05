package com.community.mua.bean;

import java.io.Serializable;

public class UserBean implements Serializable {
    private String userid;
    private String chatId;
    private String nickname;
    private String avatar;
    private Integer gender;
    private String birth;
    private String mineCode;
    private Object matchingCode;
    private boolean recordPrivate;

    private boolean seeLocation;
    private String createTime;
    private Boolean matching;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getMineCode() {
        return mineCode;
    }

    public void setMineCode(String mineCode) {
        this.mineCode = mineCode;
    }

    public Object getMatchingCode() {
        return matchingCode;
    }

    public void setMatchingCode(Object matchingCode) {
        this.matchingCode = matchingCode;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Boolean isMatching() {
        return matching;
    }

    public void setMatching(Boolean matching) {
        this.matching = matching;
    }

    public boolean isRecordPrivate() {
        return recordPrivate;
    }

    public void setRecordPrivate(boolean recordPrivate) {
        this.recordPrivate = recordPrivate;
    }

    public boolean isSeeLocation() {
        return seeLocation;
    }

    public void setSeeLocation(boolean seeLocation) {
        this.seeLocation = seeLocation;
    }
} 
