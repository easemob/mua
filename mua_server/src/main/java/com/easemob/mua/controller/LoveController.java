package com.easemob.mua.controller;

import com.easemob.mua.base.BaseResponse;
import com.easemob.mua.pojo.dto.req.LoveReqDto;
import com.easemob.mua.pojo.dto.rsp.LoveRspDto;
import com.easemob.mua.pojo.dto.rsp.UploadImgRspDto;
import com.easemob.mua.services.FileService;
import com.easemob.mua.services.ILoveService;
import com.easemob.mua.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author easemob_developer
 * @date 2022/7/25
 */
@RestController
@RequestMapping("user/love")
public class LoveController {
    @Autowired
    ILoveService loveService;
    @Autowired
    FileService fileService;


    @PostMapping("/createLove")
    public BaseResponse createLove(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId,@RequestParam("position") int position) {
        int selectLove = loveService.selectLove(userId, position);
        String loveFilePath = fileService.saveLoveFile(file, userId);
        LoveReqDto dto = new LoveReqDto();
        dto.setUserId(userId);
        dto.setImgUrl(loveFilePath);
        dto.setPosition(position);
        if (selectLove == 1) {
            loveService.editLove(dto);
        } else {
            loveService.createLove(dto);
        }
        LoveRspDto rspDto = new LoveRspDto();
        rspDto.setImgUrl(loveFilePath);
        return ResultUtil.ok(rspDto);
    }



    @PostMapping("/deleteLove")
    @ResponseBody
    public BaseResponse deleteLove(@RequestBody Map map) {
        String userId = (String) map.get("userId");
        int position = (Integer) map.get("position");
        loveService.deleteLove(userId,position);
        return ResultUtil.ok();
    }

    @PostMapping("/selectLoveByUserId")
    @ResponseBody
    public BaseResponse selectLoveByUserId(@RequestBody Map map) {
        String userId = (String) map.get("userId");
        if (map.containsKey("position")){
            int position = (Integer) map.get("position");
            List<LoveRspDto> loveRspDtos = loveService.selectLoveByUserId(userId,position);
            return ResultUtil.ok(loveRspDtos);
        } else {
            List<LoveRspDto> loveRspDtos = loveService.selectLoveByUserId(userId);
            return ResultUtil.ok(loveRspDtos);
        }
    }

}
