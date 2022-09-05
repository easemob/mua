package com.easemob.mua.pojo.dto.rsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;


/**
 * @author easemob_developer
 * @date 2022/6/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteRspDto {
    /**
     * 便签ID
     */

    private String noteId;

    /**
     * 用户匹配码
     */

    private String matchingCode;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 内容
     */

    private String content;

    /**
     * 便签时间戳
     */

    private String noteTimeStamp;

    /**
     * 置顶时间戳
     */
    private String toppingTimeStamp;

    /**
     * 注册时间
     */
    private String createTime;
}
