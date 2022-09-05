package com.easemob.mua.pojo.dto.rsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author easemob_developer
 * @date 2022/5/31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadImgRspDto {
    /**
     * 用户头像
     */
    private String avatar;


    /**
     * 用户欢迎页
     */
    private String splashUrl;

    /**
     * 相册
     */
    private String albumUrl;
}
