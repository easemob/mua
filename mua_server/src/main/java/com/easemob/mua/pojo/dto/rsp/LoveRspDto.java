package com.easemob.mua.pojo.dto.rsp;

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
public class LoveRspDto {

    /**
     * 纪念日ID
     */
    private String id;

    /**
     * 创建者ID
     */

    private String userId;
    /**
     * 纪念日名称
     */

    private String imgUrl;


    /**
     * 是否重复
     */
    private Integer position;

    /**
     * 注册时间
     */
    private String createTime;
}
