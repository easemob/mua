package com.easemob.mua.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author easemob_developer
 * @date 2022/6/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mua_diary")
public class DiaryPo {
    /**
     * 日记ID
     */
    @Id
    @Column(name = "diary_id")
    private String diaryId;

    /**
     * 用户匹配码
     */
    @Column(name = "matching_code")
    private String matchingCode;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private String userId;
    /**
     * 日记月份
     */
    @Column(name = "diary_month")
    private String diaryMonth;

    /**
     * 日记时间戳
     */
    @Column(name = "diary_time_stamp")
    private String diaryTimeStamp;
    /**
     * 心情
     */
    @Column(name = "mood")
    private String mood;


    /**
     * 心情场景
     */
    @Column(name = "mood_reason")
    private String moodReason;



    /**
     * 内容
     */
    @Column(name = "content")
    private String content;


    /**
     * 照片
     */
    @Column(name = "pic")
    private String pic;


    /**
     * 是否私密
     */
    @Column(name = "is_private")
    private Boolean isPrivate;

    /**
     * 注册时间
     */
    @Column(name = "create_time")
    private String createTime;
}
