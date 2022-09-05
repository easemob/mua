package com.easemob.mua.pojo.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author easemob_developer
 * @date 2022/7/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnniversaryReqDto {

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

    private String name;

    /**
     * 创建时间戳
     */
    private Long time;


    /**
     * 是否重复
     */
    private Boolean repeat;

    /**
     * 注册时间
     */
//    private String createTime;
}
