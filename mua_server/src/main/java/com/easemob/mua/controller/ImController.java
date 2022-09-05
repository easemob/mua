package com.easemob.mua.controller;

import com.easemob.mua.base.BaseResponse;
import com.easemob.mua.pojo.dto.req.UserReqDto;
import com.easemob.mua.pojo.dto.rsp.UserRspDto;
import com.easemob.mua.utils.ImHttp;
import com.easemob.mua.utils.ResultUtil;
import org.springframework.web.bind.annotation.*;

/**
 * @author easemob_developer
 * @date 2022/6/2
 */
@RestController
@RequestMapping("user")
public class ImController {
    @PostMapping("getAppToken")
    @ResponseBody
    public BaseResponse saveUser(){
//        ImHttp.getAppToken();
        return ResultUtil.ok();
    }

    @PostMapping("regIm")
    @ResponseBody
    public BaseResponse regIm(){
//        ImHttp.regIm("1234","123","123");
        return ResultUtil.ok();
    }
}
