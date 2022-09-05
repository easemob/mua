package com.easemob.mua.pojo.dto.rsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author easemob_developer
 * @date 2022/5/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMatchingDto {
    /**
     * 用户ID
     */
    private String uId;


    /**
     * 是否匹配成功
     */
    private boolean isMatching;

    /**
     * 匹配码1
     */
    private String code1;


    /**
     * 匹配码2
     */
    private String code2;
}
