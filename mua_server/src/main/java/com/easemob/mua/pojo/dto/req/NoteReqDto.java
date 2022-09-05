package com.easemob.mua.pojo.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author easemob_developer
 * @date 2022/6/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteReqDto {
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
     * 置顶时间戳
     */
    private String toppingTime;

}
