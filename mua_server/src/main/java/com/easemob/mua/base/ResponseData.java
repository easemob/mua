package com.easemob.mua.base;

/**
 * @author easemob_developer
 * @date 2022/2/18
 */
public class ResponseData<T> extends BaseResponse {
    /**
     * 要返回的数据对象
     */
    private T data;

    public ResponseData(IDataCode code, T data) {
        super(code);
        this.data = data;
    }

    /**
     * @return the data
     */
    public T getData() {
        return data;
    }
}
