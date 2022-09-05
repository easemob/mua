package com.easemob.mua.pojo.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author easemob_developer
 * @date 2022/5/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReqDto {

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



}
