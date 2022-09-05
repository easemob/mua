package com.easemob.mua.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author easemob_developer
 * @date 2022/7/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mua_login_record")
public class LoginRecordPo {
    @Column(name = "record_id")
    private String recordId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "time_stamp")
    private String timeStampLogin;

    @Column(name = "login_address")
    private String loginAddress;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "mobile_info")
    private String mobileInfo;

    @Column(name = "create_time")
    private String createTime;
}
