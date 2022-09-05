package com.easemob.mua.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author easemob_developer
 * @date 2022/7/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mua_love")
public class LovePo {
//    @GeneratedValue(generator = "JDBC")
    /**
     * 纪念日ID
     */
    @Id
    @Column(name = "id")
    private String id;


    /**
     * 创建者ID
     */
    @Column(name = "user_id")
    private String userId;
    /**
     * 纪念日名称
     */
    @Column(name = "img_url")
    private String imgUrl;

    /**
     * 是否重复
     */
    @Column(name = "position")
    private Integer position;

    /**
     * 注册时间
     */
    @Column(name = "create_time")
    private String createTime;
}
