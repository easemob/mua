package com.easemob.mua.services;

import com.easemob.mua.pojo.dto.req.DiaryReqDto;
import com.easemob.mua.pojo.dto.rsp.DiaryRspDto;
import com.easemob.mua.pojo.dto.rsp.NoteRspDto;
import com.easemob.mua.pojo.po.NotePo;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author easemob_developer
 * @date 2022/6/29
 */
public interface INoteService {

    /**
     * 创建标签
     * @param notePo
     */
    void createNote(NotePo notePo);

    /**
     * 编辑日记
     * @param dto
     */
    void editNote(NotePo dto);

    /**
     * 删除便签
     * @param noteId
     */
    void deleteNote(String noteId);

    /**
     * 修改置顶时间戳
     * @param noteId
     * @param toppingTimeStamp
     */
    void updateToppingTime(String noteId,String toppingTimeStamp);
    /**
     * 获取便签列表
     * @param matchingCode
     * @return
     */
    List<NoteRspDto> getNoteList(String matchingCode);
}
