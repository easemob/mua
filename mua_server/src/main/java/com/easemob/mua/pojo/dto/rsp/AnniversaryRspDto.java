package com.easemob.mua.pojo.dto.rsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author easemob_developer
 * @date 2022/7/11
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnniversaryRspDto {

    /**
     * 纪念日ID
     */
    private String id;

    /**
     * 用户匹配码
     */

    private String matchingCode;

    /**
     * 创建者ID
     */

    private String userId;
    /**
     * 纪念日名称
     */

    private String anniversaryName;

    /**
     * 创建时间戳
     */
    private Long anniversaryTime;


    /**
     * 是否重复
     */
    private Boolean isRepeat;

    /**
     * 注册时间
     */
    private String createTime;
}
