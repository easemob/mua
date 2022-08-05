package com.community.mua.bean;

import java.io.Serializable;
import java.util.List;

public class PairBean implements Serializable {
    /**
     * code : 0000
     * msg : 操作成功
     * data : {"matchingId":"88c4499cecf647289e953d0e1479919e","matchingCode":"h8y2dqej0htj9vkpfmikv8w8yj696twn","splashUrl":"","matchingTime":"2022-05-27T06:41:27.940+00:00","userRsp":[{"userid":"1653567924042","chatId":"mua_wxbebyia1a1ez69aee21b0aj3c1h","nickname":"吃喝111","avatar":"头像","gender":1,"birth":"2018-03-22","mineCode":"276817","matchingCode":null,"createTime":"2022-05-26T12:25:24.096+00:00","matching":true},{"userid":"1653568121608","chatId":"mua_0gr1sso1z86q3nnltwfhpvbhy9qx","nickname":"吃喝玩乐","avatar":"头像","gender":1,"birth":"2022-05-18","mineCode":"828586","matchingCode":null,"createTime":"2022-05-26T12:28:41.653+00:00","matching":true}]}
     */

    /**
     * matchingId : 88c4499cecf647289e953d0e1479919e
     * matchingCode : h8y2dqej0htj9vkpfmikv8w8yj696twn
     * splashUrl :
     * "albumUrl": "",
     * matchingTime : 2022-05-27T06:41:27.940+00:00
     * userRsp : [{"userid":"1653567924042","chatId":"mua_wxbebyia1a1ez69aee21b0aj3c1h","nickname":"吃喝111","avatar":"头像","gender":1,"birth":"2018-03-22","mineCode":"276817","matchingCode":null,"createTime":"2022-05-26T12:25:24.096+00:00","matching":true},{"userid":"1653568121608","chatId":"mua_0gr1sso1z86q3nnltwfhpvbhy9qx","nickname":"吃喝玩乐","avatar":"头像","gender":1,"birth":"2022-05-18","mineCode":"828586","matchingCode":null,"createTime":"2022-05-26T12:28:41.653+00:00","matching":true}]
     */

    private String matchingId;
    private String matchingCode;
    private String splashUrl;
    private String albumUrl;
    private long kittyStatusTime;

    public long getKittyStatusTime() {
        return kittyStatusTime;
    }

    public void setKittyStatusTime(long kittyStatusTime) {
        this.kittyStatusTime = kittyStatusTime;
    }

    public String getAlbumUrl() {
        return albumUrl;
    }

    public void setAlbumUrl(String albumUrl) {
        this.albumUrl = albumUrl;
    }

    private String matchingTime;
    private List<UserBean> userRsp;

    public String getMatchingId() {
        return matchingId;
    }

    public void setMatchingId(String matchingId) {
        this.matchingId = matchingId;
    }

    public String getMatchingCode() {
        return matchingCode;
    }

    public void setMatchingCode(String matchingCode) {
        this.matchingCode = matchingCode;
    }

    public String getSplashUrl() {
        return splashUrl;
    }

    public void setSplashUrl(String splashUrl) {
        this.splashUrl = splashUrl;
    }

    public String getMatchingTime() {
        return matchingTime;
    }

    public void setMatchingTime(String matchingTime) {
        this.matchingTime = matchingTime;
    }

    public List<UserBean> getUserRsp() {
        return userRsp;
    }

    public void setUserRsp(List<UserBean> userRsp) {
        this.userRsp = userRsp;
    }

}
