package com.easemob.mua.pojo.dto.rsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author easemob_developer
 * @date 2022/5/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRspDto {

    /**
     * 用户ID
     */
    private String userid;


    /**
     * 用户环信ID
     */
    private String chatId;


    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户性别;
     */
    private int gender;


    /**
     * 用户生日;
     */
    private String birth;


    /**
     * 用户自己匹配码;
     */
    private String mineCode;


    /**
     * 是否匹配成功
     */
    private boolean isMatching;

    /**
     * 配对码
     */
    private String matchingCode;


    private boolean isRecordPrivate;


    private boolean isSeeLocation;
    /**
     * 注册时间
     */
    private String createTime;

}
