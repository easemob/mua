package com.easemob.mua.mapper;


import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author easemob_developer
 * @date 2022/6/16
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
