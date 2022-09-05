package com.easemob.mua.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.easemob.mua.base.BaseResponse;
import com.easemob.mua.base.CodeEnum;
import com.easemob.mua.exception.BizException;
import com.easemob.mua.pojo.dto.req.NoteReqDto;
import com.easemob.mua.pojo.dto.rsp.DiaryRspDto;
import com.easemob.mua.pojo.dto.rsp.NoteRspDto;
import com.easemob.mua.pojo.po.NotePo;
import com.easemob.mua.services.IDiaryService;
import com.easemob.mua.services.INoteService;
import com.easemob.mua.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author easemob_developer
 * @date 2022/6/29
 */
@RestController
@RequestMapping("user/note")
public class NoteController {

    @Autowired
    INoteService noteService;


    @PostMapping("createNote")
    public BaseResponse createNote(@RequestBody NoteReqDto noteReqDto){
        if (noteReqDto == null || StrUtil.isEmpty(noteReqDto.getMatchingCode())) {
            throw new BizException(CodeEnum.MISS_PARAM,"参数不能为空!");
        }
        NotePo po = new NotePo();
        po.setNoteId(IdUtil.fastSimpleUUID());
        po.setMatchingCode(noteReqDto.getMatchingCode());
        po.setUserId(noteReqDto.getUserId());
        po.setContent(noteReqDto.getContent());
        po.setNoteTimeStamp(System.currentTimeMillis()+"");
        po.setToppingTimeStamp(noteReqDto.getToppingTime());
        po.setCreateTime(DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
        noteService.createNote(po);
        return ResultUtil.ok();
    }


    @PostMapping("deleteNote")
    public BaseResponse deleteNote(@RequestBody Map map){
        String noteId = (String) map.get("noteId");
        noteService.deleteNote(noteId);
        return ResultUtil.ok();
    }

    @PostMapping("updateToppingTime")
    public BaseResponse updateToppingTime(@RequestBody Map map){
        String noteId = (String) map.get("noteId");
        String toppingTimeStamp = (String) map.get("time");
        noteService.updateToppingTime(noteId,toppingTimeStamp);
        return ResultUtil.ok();
    }


    @PostMapping("getNoteList")
    public BaseResponse getNoteList(@RequestBody Map map){
        String matchingCode = (String) map.get("matchingCode");
        List<NoteRspDto> noteList = noteService.getNoteList(matchingCode);
        return ResultUtil.ok(noteList);
    }

}
