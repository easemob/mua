package com.community.mua.bean;

import java.io.Serializable;

public class NoteBean implements Serializable {
    /**
     * noteId : aa51363afe204fe4b99fb8f89c7444ec
     * matchingCode : a7r707fyxmg5nl11pogerh4cwz85cdi4
     * content : 吃吃吃吃吃
     * noteTimeStamp : 1656514524834
     * createTime : null
     * userId :""
     */

    private String noteId;
    private String matchingCode;
    private String content;
    private String userId;
    private long toppingTimeStamp;

    public long getToppingTimeStamp() {
        return toppingTimeStamp;
    }

    public void setToppingTimeStamp(long toppingTimeStamp) {
        this.toppingTimeStamp = toppingTimeStamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String creator) {
        this.userId = creator;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getMatchingCode() {
        return matchingCode;
    }

    public void setMatchingCode(String matchingCode) {
        this.matchingCode = matchingCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getNoteTimeStamp() {
        return noteTimeStamp;
    }

    public void setNoteTimeStamp(long noteTimeStamp) {
        this.noteTimeStamp = noteTimeStamp;
    }

    public Object getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Object createTime) {
        this.createTime = createTime;
    }

    private long noteTimeStamp;
    private Object createTime;
}
