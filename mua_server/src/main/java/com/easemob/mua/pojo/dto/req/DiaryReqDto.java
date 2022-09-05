package com.easemob.mua.pojo.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author easemob_developer
 * @date 2022/5/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiaryReqDto {
    //用户id、日期、心情、状态（最多3个）、内容、照片（1张）、是否私密
    /**
     * 日记ID
     */
    private String diaryId;

    /**
     * 用户匹配码
     */
    private String matchingCode;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 日记月份
     */
    private String diaryMonth;

    /**
     * 日记时间戳
     */
    private String diaryTimeStamp;


    /**
     * 心情
     */
    private String mood;


    /**
     * 心情场景
     */
    private String moodReason;


    /**
     * 内容
     */
    private String content;


    /**
     * 照片
     */
    private String pic;


    /**
     * 照片
     */
    private boolean isPrivate;
}
