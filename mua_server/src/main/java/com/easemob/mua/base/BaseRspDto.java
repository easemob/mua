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
public class BaseRspDto<HEAD,DATA> implements Serializable {

    private HEAD meta;

    private DATA data;
}
