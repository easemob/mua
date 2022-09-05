package com.easemob.mua.pojo.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author easemob_developer
 * @date 2022/7/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRecordReqDto {


    private String userId;


    private String timeStampLogin;


    private String loginAddress;


    private String latitude;


    private String longitude;


    private String mobileInfo;

}
