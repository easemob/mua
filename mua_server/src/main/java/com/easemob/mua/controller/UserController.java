package com.easemob.mua.controller;

import cn.hutool.core.util.StrUtil;
import com.easemob.mua.base.BaseResponse;
import com.easemob.mua.base.CodeEnum;
import com.easemob.mua.exception.BizException;
import com.easemob.mua.pojo.dto.req.UserReqDto;
import com.easemob.mua.pojo.dto.rsp.*;
import com.easemob.mua.services.FileService;
import com.easemob.mua.services.IUserService;
import com.easemob.mua.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author easemob_developer
 * @date 2022/5/23
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    IUserService userService;

    @Autowired
    FileService fileService;


//    @PostMapping("createChatAccount")
//    @ResponseBody
//    public BaseResponse createChatAccount(){
//        ChatAccountRspDto chatAccount = userService.createChatAccount();
//        return ResultUtil.ok(chatAccount);
//    }


    @PostMapping("save")
    @ResponseBody
    public BaseResponse saveUser(@RequestBody UserReqDto reqDto){
        UserRspDto user = userService.saveUser(reqDto);
        return ResultUtil.ok(user);
    }

    @PostMapping("getUser")
    @ResponseBody
    public BaseResponse getUserById(@RequestBody Map map) {
        String userId = (String) map.get("userId");
        UserRspDto userRspDto = userService.getUserById(userId);
        return ResultUtil.ok(userRspDto);
    }


    @PostMapping("getUserByCode")
    @ResponseBody
    public BaseResponse getUserByCode(@RequestBody Map map) {
        String mineCode = (String) map.get("mineCode");
        UserRspDto userRspDto = userService.getUserByCode(mineCode);
        return ResultUtil.ok(userRspDto);
    }

    @PostMapping("updateNickName")
    @ResponseBody
    public BaseResponse updateUserNickName(@RequestBody Map map)  {
        String userId = (String) map.get("userId");
        String nickname = (String) map.get("nickName");
        userService.updateUserNickName(userId,nickname);
        return ResultUtil.ok();
    }


    @PostMapping("updateBirth")
    @ResponseBody
    public BaseResponse updateUserBirth(@RequestBody Map map) {
        String userId = (String) map.get("userId");
        String birth = (String) map.get("birth");
        userService.updateUserBirth(userId,birth);
        return ResultUtil.ok();
    }

    @PostMapping("matching")
    @ResponseBody
    public BaseResponse matching(@RequestBody Map map) {
        String code1 = (String) map.get("code1");
        String code2 = (String) map.get("code2");
        MatchingRspDto rspDto = userService.matching(code1, code2);
        return ResultUtil.ok(rspDto);
    }


    @PostMapping("unMatch")
    @ResponseBody
    public BaseResponse unMatch(@RequestBody Map map) {
        String matchingId = (String) map.get("matchingId");
        int res = userService.unMatch(matchingId);
        return ResultUtil.ok();
    }


    @PostMapping("getSplash")
    @ResponseBody
    public BaseResponse getSplash(@RequestBody Map map) {
        String userId = (String) map.get("userId");
        MatchingRspDto matchingRspDto = userService.getSplash(userId);
        return ResultUtil.ok(matchingRspDto);
    }

    @PostMapping("updateKittyStatusTime")
    @ResponseBody
    public BaseResponse setKittyStatusTime(@RequestBody Map map) {
        String matchingCode = (String) map.get("matchingCode");
        String time = (String) map.get("time");
        userService.setKittyStatusTime(matchingCode,time);
        return ResultUtil.ok();
    }

    @PostMapping("/uploadSplash")
    public BaseResponse uploadSplash(@RequestParam("media") MultipartFile file,@RequestParam("userId") String userId) {
        UploadImgRspDto filePath = fileService.saveFile(file,userId);
        return ResultUtil.ok(filePath);
    }

    @PostMapping("/uploadHeadImg")
    public BaseResponse uploadHeadImg(@RequestParam("headImg") MultipartFile file,@RequestParam("userId") String userId) {
        UploadImgRspDto headImgRspDto = fileService.uploadHeadImg(file);
        if (!StrUtil.isEmpty(userId)){
          userService.updateUserAvatar(headImgRspDto.getAvatar(),userId);
        }
        return ResultUtil.ok(headImgRspDto);
    }


    @PostMapping("/uploadAlbum")
    public BaseResponse uploadAlbum(@RequestParam("album") MultipartFile file,@RequestParam("matchCode") String matchCode) {
        if (StrUtil.isEmpty(matchCode)) {
            throw new BizException(CodeEnum.MISS_PARAM,"匹配码不能为空!");
        }
        UploadImgRspDto headImgRspDto = fileService.uploadAlbum(matchCode,file);
        return ResultUtil.ok(headImgRspDto);
    }



    @GetMapping("/downloadSplash")
    public BaseResponse downloadFile(HttpServletResponse response,
                                   @RequestParam(value = "fileName") String fileName) {

        Boolean result = fileService.downloadFile(response, fileName);
        return ResultUtil.ok(result);
    }

}
