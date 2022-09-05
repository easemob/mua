package com.easemob.mua.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author easemob_developer
 * @date 2022/7/11
 */
@Component
public class SnowflakeUtil {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    //为终端ID
    private long workerId = 0;
    //数据中心ID
    private long dataCenterId = 1;
    private Snowflake snowflake = IdUtil.createSnowflake(workerId,dataCenterId);
    @PostConstruct
    public void init(){
        workerId = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());
    }
    public synchronized String snowflakeId(){
        return String.valueOf(snowflake.nextId());
    }
    public synchronized long snowflakeId(long workerId,long dataCenterId){
        Snowflake snowflake = IdUtil.createSnowflake(workerId, dataCenterId);
        return snowflake.nextId();
    }
}
