package com.community.mua.bean;

import java.io.Serializable;

public class AnniversaryBean implements Serializable {

    /**
     * id : 1546396650073882624
     * matchingCode : zkm8f01dgi2lgzxg51qmpe17z9lmcyru
     * userId : 7b6ad86fe41c4af98d152621e0970372
     * anniversaryName : 我们12的4444纪念555日
     * anniversaryTime : 1657520562393
     * isRepeat : false
     * createTime : 2022-07-11 16:05:41
     */

    private String id;
    private String matchingCode;
    private String userId;
    private String anniversaryName;
    private Long anniversaryTime;
    private Boolean isRepeat;
    private String createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMatchingCode() {
        return matchingCode;
    }

    public void setMatchingCode(String matchingCode) {
        this.matchingCode = matchingCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAnniversaryName() {
        return anniversaryName;
    }

    public void setAnniversaryName(String anniversaryName) {
        this.anniversaryName = anniversaryName;
    }

    public Long getAnniversaryTime() {
        return anniversaryTime;
    }

    public void setAnniversaryTime(Long anniversaryTime) {
        this.anniversaryTime = anniversaryTime;
    }

    public Boolean getRepeat() {
        return isRepeat;
    }

    public void setRepeat(Boolean repeat) {
        isRepeat = repeat;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
