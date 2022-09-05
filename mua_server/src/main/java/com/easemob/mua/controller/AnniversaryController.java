package com.easemob.mua.controller;

import com.easemob.mua.base.BaseResponse;
import com.easemob.mua.pojo.dto.req.AnniversaryReqDto;
import com.easemob.mua.pojo.dto.rsp.AnniversaryRspDto;
import com.easemob.mua.services.IAnniversaryService;
import com.easemob.mua.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author easemob_developer
 * @date 2022/7/11
 */
@RestController
@RequestMapping("user/anniversary")
public class AnniversaryController {

    @Autowired
    IAnniversaryService anniversaryService;

    @PostMapping("create")
    @ResponseBody
    public BaseResponse createAnniversary(@RequestBody AnniversaryReqDto reqDto){
        anniversaryService.createAnniversary(reqDto);
        return ResultUtil.ok();
    }

    @PostMapping("getAllByMatchingCode")
    @ResponseBody
    public BaseResponse getAllByMatchingCode(@RequestBody Map map){
        String matchingCode =  (String) map.get("matchingCode");
        List<AnniversaryRspDto> allByMatchingCode = anniversaryService.getAllByMatchingCode(matchingCode);
        return ResultUtil.ok(allByMatchingCode);
    }

    @PostMapping("edit")
    @ResponseBody
    public BaseResponse editAnniversary(@RequestBody AnniversaryReqDto reqDto){
        anniversaryService.editAnniversary(reqDto);
        return ResultUtil.ok();
    }

    @PostMapping("delete")
    @ResponseBody
    public BaseResponse deleteAnniversary(@RequestBody Map map){
        String id =  (String) map.get("id");
        anniversaryService.deleteAnniversary(id);
        return ResultUtil.ok();
    }
}
