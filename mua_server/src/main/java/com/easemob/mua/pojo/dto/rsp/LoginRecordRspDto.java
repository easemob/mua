package com.easemob.mua.pojo.dto.rsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

/**
 * @author easemob_developer
 * @date 2022/7/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRecordRspDto {

    private String recordId;


    private String userId;


    private String timeStampLogin;


    private String loginAddress;


    private String latitude;


    private String longitude;


    private String mobileInfo;

    private String createTime;
}
