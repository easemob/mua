package com.easemob.mua.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author easemob_developer
 * @date 2022/5/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchingPo {

    /**
     * 用户ID
     */
    private String matchingId;


    /**
     * 匹配码1
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
    private long matchingTime;

    /**
     * 猫咪状态同步时间
     */
    private long kittyStatusTime;

    /**
     * 匹配时间
     */
    private String matchingDate;
}
