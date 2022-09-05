package com.easemob.mua.services.impl;

import cn.hutool.core.util.StrUtil;
import com.easemob.mua.base.CodeEnum;
import com.easemob.mua.exception.BizException;
import com.easemob.mua.mapper.MatchingMapper;
import com.easemob.mua.mapper.NoteMapper;
import com.easemob.mua.mapper.UserMapper;
import com.easemob.mua.pojo.dto.req.DiaryReqDto;
import com.easemob.mua.pojo.dto.rsp.NoteRspDto;
import com.easemob.mua.pojo.po.NotePo;
import com.easemob.mua.services.INoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author easemob_developer
 * @date 2022/6/29
 */
@Slf4j
@Service
public class NoteServiceImpl implements INoteService {
    @Autowired
    NoteMapper noteMapper;
    @Autowired
    MatchingMapper matchingMapper;

    @Override
    public void createNote(NotePo notePo) {

        int have = matchingMapper.selectMatchingCode(notePo.getMatchingCode());
        if (have == 0) {
            throw new BizException(CodeEnum.MISS_PARAM,"匹配码不存在!");
        }
        noteMapper.insert(notePo);
    }

    @Override
    public void editNote(NotePo dto) {
    }

    @Override
    public void deleteNote(String noteId) {
        if (StrUtil.isEmpty(noteId)) {
            throw new BizException(CodeEnum.MISS_PARAM,"便签Id不能为空!");
        }
        noteMapper.deleteByPrimaryKey(noteId);
    }

    @Override
    public void updateToppingTime(String noteId, String toppingTimeStamp) {
        if (StrUtil.isEmpty(noteId)) {
            throw new BizException(CodeEnum.MISS_PARAM,"便签Id不能为空!");
        }
        noteMapper.updateToppingTime(noteId,toppingTimeStamp);
    }

    @Override
    public List<NoteRspDto> getNoteList(String matchingCode) {
        if (StrUtil.isEmpty(matchingCode)) {
            throw new BizException(CodeEnum.MISS_PARAM,"查询条件不能为空!");
        }
        List<NoteRspDto> noteList = noteMapper.getNoteList(matchingCode);

        return noteList;
    }
}
