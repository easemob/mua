package com.community.mua.bean;

import java.io.Serializable;

public class LoveCoverBean implements Serializable {
    /**
     * id : null
     * userId : null
     * imgUrl : file/046dc25e79ae4d6988e59b7a13eb3205_1658750142908_location.png
     * position : null
     * createTime : null
     */

    private String id;
    private String userId;
    private String imgUrl;
    private int position;
    private long createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
