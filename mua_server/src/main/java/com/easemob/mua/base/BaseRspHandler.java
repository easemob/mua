package com.easemob.mua.base;

/**
 * @author easemob_developer
 * @date 2022/2/10
 */
public abstract class BaseRspHandler {
    protected <Body> BaseRspDto setResponse(Body body){
        BaseRspHead baseRspHead = BaseRspHead.builder().build().ok();
        return BaseRspDto.builder().meta(baseRspHead).data(body).build();
    }

}
