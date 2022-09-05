package com.easemob.mua.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author easemob_developer
 * @date 2022/7/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mua_anniversary")
public class AnniversaryPo {
//    @GeneratedValue(generator = "JDBC")
    /**
     * 纪念日ID
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 用户匹配码
     */
    @Column(name = "matching_code")
    private String matchingCode;

    /**
     * 创建者ID
     */
    @Column(name = "user_id")
    private String userId;
    /**
     * 纪念日名称
     */
    @Column(name = "anniversary_name")
    private String name;

    /**
     * 创建时间戳
     */
    @Column(name = "anniversary_time")
    private Long time;


    /**
     * 是否重复
     */
    @Column(name = "is_repeat")
    private Boolean repeat;

    /**
     * 注册时间
     */
    @Column(name = "create_time")
    private String createTime;
}
