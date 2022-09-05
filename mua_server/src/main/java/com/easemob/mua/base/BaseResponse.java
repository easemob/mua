package com.easemob.mua.base;

import com.alibaba.druid.util.StringUtils;

/**
 * @author easemob_developer
 * @date 2022/2/18
 */
public class BaseResponse {
    /**
     * 响应码
     */
    private String code;
    /**
     * 响应描述
     */
    private String msg;

    public BaseResponse(IDataCode code) {
        this.code = code.getCode();
        this.msg = code.getMsg();
    }

    public BaseResponse(IDataCode code, String otherMsg) {
        this.code = code.getCode();
        if (StringUtils.isEmpty(otherMsg)) {
            this.msg = code.getMsg();
        } else {
            this.msg = code.getMsg() + ":"
                    + (otherMsg.endsWith(",") ? otherMsg.substring(0, otherMsg.length() - 1) : otherMsg);
        }
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }
}
