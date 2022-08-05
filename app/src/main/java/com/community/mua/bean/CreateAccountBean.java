package com.community.mua.bean;

import java.io.Serializable;

public class CreateAccountBean implements Serializable {
    /**
     * data : {"chatId":"mua_7xqoqlekbjla1hubdhb7jr4nq19v","createTime":"2022-05-27T08:39:11.057+00:00"}
     */

        /**
         * chatId : mua_7xqoqlekbjla1hubdhb7jr4nq19v
         * createTime : 2022-05-27T08:39:11.057+00:00
         */

        private String chatId;
        private String createTime;

        public String getChatId() {
            return chatId;
        }

        public void setChatId(String chatId) {
            this.chatId = chatId;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
