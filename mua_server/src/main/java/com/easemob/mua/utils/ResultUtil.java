package com.easemob.mua.utils;


import com.easemob.mua.base.BaseResponse;
import com.easemob.mua.base.CodeEnum;
import com.easemob.mua.base.ResponseData;
import com.easemob.mua.exception.BizException;

/**
 * @author easemob_developer
 * @date 2022/2/18
 */
public class ResultUtil {
    /**
     * 封装业务异常返回结果
     *
     * @param exception
     * @return
     * @author easemob_developer
     * @date 2020/05/07 09:39:11
     */
    public static BaseResponse bizException(BizException exception) {
        if(null==exception.getMsg()) {
            return new BaseResponse(exception.getCode());
        }else {
            return new BaseResponse(exception.getCode(),exception.getMsg());
        }
    }

    /**
     * 封装其它异常返回结果
     *
     * @return
     * @author easemob_developer
     * @date 2020/05/07 09:39:00
     */
    public static BaseResponse otherException() {
        return new BaseResponse(CodeEnum.ERROR);
    }



    /**
     * 封装正常返回结果
     *
     * @return
     * @author easemob_developer
     * @date 2020/05/07 09:39:16
     */
    public static BaseResponse ok() {
        return new BaseResponse(CodeEnum.SUCCESS);
    }

    /**
     * 封装带数据的正常返回结果
     *
     * @param data
     * @return
     * @author easemob_developer
     * @date 2020/05/07 09:39:20
     */
    public static <T> ResponseData<T> ok(T data) {
        return new ResponseData<>(CodeEnum.SUCCESS, data);
    }


    /**
     * 封装带数据的失败返回结果
     *
     * @param data
     * @return
     * @author haoxiaolei
     * @date 2020/07/02 09:39:20
     */
    public static <T> ResponseData<T> fail(T data) {
        return new ResponseData<>(CodeEnum.VALIDATION_ERROR, data);
    }
}
