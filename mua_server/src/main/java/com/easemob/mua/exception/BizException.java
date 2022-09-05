package com.easemob.mua.exception;


import com.easemob.mua.base.IDataCode;

/**
 * @author easemob_developer
 * @date 2022/2/18
 */
public class BizException extends RuntimeException{
    private static final long serialVersionUID = 2798917240468315404L;
    private IDataCode code;
    private String msg;

    public BizException(IDataCode code) {
        super();
        this.code = code;
    }

    public BizException(IDataCode code, String msg) {
        super();
        this.code = code;
        this.msg=msg;
    }
    public BizException(IDataCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    /**
     * @return the code
     */
    public IDataCode getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }



}
