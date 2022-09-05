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
public class LoveReqDto {

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

}
