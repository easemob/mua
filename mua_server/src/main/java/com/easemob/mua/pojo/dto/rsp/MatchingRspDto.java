package com.easemob.mua.pojo.dto.rsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author easemob_developer
 * @date 2022/5/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchingRspDto {
    /**
     * 用户ID
     */
    private String matchingId;


    /**
     * 匹配码
     */
    private String matchingCode;

    /**
     * 欢迎页
     */
    private String splashUrl;

    /**
     * 相册
     */
    private String albumUrl;

    /**
     * 匹配时间
     */
    private String matchingTime;
    /**
     * 匹配时间
     */
    private String matchingDate;

    /**
     * 猫咪状态同步时间
     */
    private String kittyStatusTime;


    private List<UserRspDto> userRsp;
}
