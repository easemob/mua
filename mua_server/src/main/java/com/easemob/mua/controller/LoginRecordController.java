package com.easemob.mua.controller;

import com.easemob.mua.base.BaseResponse;
import com.easemob.mua.pojo.dto.req.LoginRecordReqDto;
import com.easemob.mua.pojo.dto.rsp.LoginRecordRspDto;
import com.easemob.mua.pojo.dto.rsp.MatchingRspDto;
import com.easemob.mua.services.ILoginRecordService;
import com.easemob.mua.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author easemob_developer
 * @date 2022/7/6
 */
@RestController
@RequestMapping("user/loginRecord")
public class LoginRecordController {

    @Autowired
    ILoginRecordService loginRecordService;

    @PostMapping("insertRecord")
    @ResponseBody
    public BaseResponse insertRecord(@RequestBody Map map) {

        String userId =  (String) map.get("userId");
        String loginAddress =  (String) map.get("loginAddress");
        String timeStamp = (String) map.get("timeStamp");
        String latitude =  (String) map.get("latitude");
        String longitude =  (String) map.get("longitude");
        String mobileInfo =  (String) map.get("mobileInfo");

        LoginRecordReqDto reqDto = new LoginRecordReqDto();
        reqDto.setUserId(userId);
        reqDto.setLoginAddress(loginAddress);
        reqDto.setTimeStampLogin(timeStamp);
        reqDto.setLatitude(latitude);
        reqDto.setLongitude(longitude);
        reqDto.setMobileInfo(mobileInfo);
        loginRecordService.insertRecord(reqDto);
        return ResultUtil.ok();
    }

    @PostMapping("getList")
    @ResponseBody
    public BaseResponse getList(@RequestBody Map map) {
        String mineId =  (String) map.get("mineId");
        String taId =  (String) map.get("taId");
        List<LoginRecordRspDto> recordRspDtos = loginRecordService.getList(mineId,taId);
        return ResultUtil.ok(recordRspDtos);
    }

    @PostMapping("getLastRecord")
    @ResponseBody
    public BaseResponse getLastRecord(@RequestBody Map map) {
        String userId =  (String) map.get("userId");
        LoginRecordRspDto recordRspDto = loginRecordService.getLastRecord(userId);
        return ResultUtil.ok(recordRspDto);
    }




    @PostMapping("setIsPrivate")
    @ResponseBody
    public BaseResponse setIsPrivate(@RequestBody Map map) {
        String userId =  (String) map.get("userId");
        boolean isPrivate =  (boolean) map.get("isPrivate");
        loginRecordService.setIsPrivate(userId,isPrivate);
        return ResultUtil.ok();
    }



    @PostMapping("getPairLastRecord")
    @ResponseBody
    public BaseResponse getPairLastRecord(@RequestBody Map map) {
        String mineId =  (String) map.get("mineId");
        String taId =  (String) map.get("taId");
        List<LoginRecordRspDto> recordRspDtos = loginRecordService.getPairLastRecord(mineId,taId);
        return ResultUtil.ok(recordRspDtos);
    }

    @PostMapping("setIsSeeLocation")
    @ResponseBody
    public BaseResponse setIsSeeLocation(@RequestBody Map map) {
        String userId =  (String) map.get("userId");
        boolean isSeeLocation =  (boolean) map.get("isSeeLocation");
        loginRecordService.setIsSeeLocation(userId,isSeeLocation);
        return ResultUtil.ok();
    }
}
