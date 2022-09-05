package com.easemob.mua.pojo.dto.rsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author easemob_developer
 * @date 2022/5/26
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatAccountRspDto {
    /**
     * 环信ID
     */
    private String chatId;

    /**
     * 注册时间
     */
    private String createTime;



}
