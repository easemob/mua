package com.community.mua.bean;

import com.hyphenate.chat.EMMessage;

import java.util.List;

public class ChatPicContainerBean {
    private String time;
    private List<EMMessage> mMessages;

    public ChatPicContainerBean() {
    }

    public ChatPicContainerBean(String time, List<EMMessage> messages) {
        this.time = time;
        mMessages = messages;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<EMMessage> getMessages() {
        return mMessages;
    }

    public void setMessages(List<EMMessage> messages) {
        mMessages = messages;
    }
}
