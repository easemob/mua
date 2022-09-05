package com.easemob.mua.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author easemob_developer
 * @date 2022/6/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mua_note")
public class NotePo {
    /**
     * 便签ID
     */
    @Id
    @Column(name = "note_id")
    private String noteId;

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
     * 内容
     */
    @Column(name = "content")
    private String content;

    /**
     * 便签时间戳
     */
    @Column(name = "note_time_stamp")
    private String noteTimeStamp;

    /**
     * 置顶时间戳
     */
    @Column(name = "topping_time_stamp")
    private String toppingTimeStamp;

    /**
     * 注册时间
     */
    @Column(name = "create_time")
    private String createTime;
}
