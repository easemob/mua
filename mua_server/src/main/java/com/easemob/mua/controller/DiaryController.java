package com.easemob.mua.controller;

import com.easemob.mua.base.BaseResponse;
import com.easemob.mua.pojo.dto.req.DiaryReqDto;
import com.easemob.mua.pojo.dto.rsp.DiaryRspDto;
import com.easemob.mua.services.IDiaryService;
import com.easemob.mua.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author easemob_developer
 * @date 2022/6/15
 */
@RestController
@RequestMapping("user/diary")
public class DiaryController {

    @Autowired
    IDiaryService diaryService;

    @PostMapping("writeDiaryFile")
    public BaseResponse writeDiaryFile(@RequestParam("media") MultipartFile file,
                                   @RequestParam("matchingCode") String matchingCode,
                                   @RequestParam("userId") String userId,
                                   @RequestParam("mood") String mood,
                                   @RequestParam("moodReason") String moodReason,
                                       @RequestParam("diaryTimeStamp") String diaryTimeStamp,
                                   @RequestParam("content") String content,
                                   @RequestParam("isPrivate") String isPrivate
                                   ){

        DiaryReqDto reqDto = new DiaryReqDto();
//        String matchingCode = (String) map.get("matchingCode");
//        String mood = (String) map.get("mood");
//        String moodReason =(String)  map.get("moodReason");
//        String content = (String) map.get("content");
//        boolean isPrivate =(Boolean) map.get("isPrivate");

        reqDto.setMatchingCode(matchingCode);
        reqDto.setUserId(userId);
        reqDto.setMood(mood);
        reqDto.setDiaryTimeStamp(diaryTimeStamp);
        reqDto.setMoodReason(moodReason);
        reqDto.setContent(content);
        reqDto.setPrivate(false);
        diaryService.writeDiary(file,reqDto);

        return ResultUtil.ok();
    }

    @PostMapping("writeDiary")
    public BaseResponse writeDiary(@RequestBody Map map){
        DiaryReqDto reqDto = new DiaryReqDto();

        String matchingCode =  (String) map.get("matchingCode");
        String userId =  (String) map.get("userId");
        String mood =  (String) map.get("mood");
        String diaryTimeStamp = (String) map.get("diaryTimeStamp");
        String moodReason =  (String) map.get("moodReason");
        String content =  (String) map.get("content");

        reqDto.setMatchingCode(matchingCode);
        reqDto.setUserId(userId);
        reqDto.setMood(mood);
        reqDto.setDiaryTimeStamp(diaryTimeStamp);
        reqDto.setMoodReason(moodReason);
        reqDto.setContent(content);
        reqDto.setPrivate(false);
        diaryService.writeDiary(reqDto);

        return ResultUtil.ok();
    }


    @PostMapping("editDiaryFile")
    public BaseResponse editDiaryFile(@RequestParam("media") MultipartFile file,
                                  @RequestParam("diaryId") String diaryId,
                                  @RequestParam("matchingCode") String matchingCode,
                                  @RequestParam("mood") String mood,
                                  @RequestParam("diaryMonth") String diaryMonth,
                                  @RequestParam("diaryTimeStamp") String diaryTimeStamp,
                                  @RequestParam("moodReason") String moodReason,
                                  @RequestParam("content") String content,
                                  @RequestParam("isPrivate") String isPrivate){

        DiaryReqDto reqDto = new DiaryReqDto();
        reqDto.setDiaryId(diaryId);
        reqDto.setMatchingCode(matchingCode);
        reqDto.setMood(mood);
        reqDto.setMoodReason(moodReason);
        reqDto.setContent(content);
        reqDto.setDiaryMonth(diaryMonth);
        reqDto.setDiaryTimeStamp(diaryTimeStamp);
        reqDto.setPrivate(false);
        diaryService.editDiary(file,reqDto);

        return ResultUtil.ok();
    }


    @PostMapping("editDiary")
    public BaseResponse editDiary(@RequestBody Map map){
        String diaryId =  (String) map.get("diaryId");

        String matchingCode =  (String) map.get("matchingCode");
        String mood =  (String) map.get("mood");
        String moodReason =  (String) map.get("moodReason");
        String content =  (String) map.get("content");
        String diaryMonth =  (String) map.get("diaryMonth");
        String diaryTimeStamp =  (String) map.get("diaryTimeStamp");

        DiaryReqDto reqDto = new DiaryReqDto();
        reqDto.setDiaryId(diaryId);
        reqDto.setMatchingCode(matchingCode);
        reqDto.setMood(mood);
        reqDto.setMoodReason(moodReason);
        reqDto.setContent(content);
        reqDto.setDiaryMonth(diaryMonth);
        reqDto.setDiaryTimeStamp(diaryTimeStamp);
        reqDto.setPrivate(false);
        diaryService.editDiary(reqDto);

        return ResultUtil.ok();
    }


    @PostMapping("deleteDiary")
    public BaseResponse deleteDiary(@RequestBody Map map){
        String diaryId = (String) map.get("diaryId");
        diaryService.deleteDiary(diaryId);
        return ResultUtil.ok();
    }


    @PostMapping("getDiaryList")
    public BaseResponse getDiaryList(@RequestBody Map map){
        String matchingCode = (String) map.get("matchingCode");
        String month = (String) map.get("month");
        List<DiaryRspDto> diaryList = diaryService.getDiaryList(matchingCode, month);
        return ResultUtil.ok(diaryList);
    }

    @PostMapping("queryDiary")
    public BaseResponse queryDiary(@RequestBody Map map){
        String diaryId = (String) map.get("diaryId");
        DiaryRspDto rspDto = diaryService.queryDiary(diaryId);
        return ResultUtil.ok(rspDto);
    }
}
