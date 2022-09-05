package com.easemob.mua.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author easemob_developer
 * @date 2022/2/10
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseRspHead implements Serializable {
    private Integer code;
    private String msg;
    private Boolean success;
    private Long timestamp;

    public BaseRspHead ok(){
        this.code = 200;
        this.msg = "success";
        this.success = true;
        this.timestamp = System.currentTimeMillis();
        return this;
    }

}
