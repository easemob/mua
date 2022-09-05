package com.easemob.mua.mapper;

import com.easemob.mua.pojo.dto.rsp.DiaryRspDto;
import com.easemob.mua.pojo.dto.rsp.NoteRspDto;
import com.easemob.mua.pojo.po.DiaryPo;
import com.easemob.mua.pojo.po.NotePo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author easemob_developer
 * @date 2022/6/15
 */
@Mapper
@Repository
public interface NoteMapper extends MyMapper<NotePo> {
    /**
     * 创建便签
     * @param notePo
     */
    void createNote(NotePo notePo);


    /**
     * 删除便签
     * @param noteId
     */
    void deleteNote(String noteId);


    /**
     * 查询便签列表
     * @param matchingCode
     * @return
     */
    List<NoteRspDto> getNoteList(@Param("matchingCode")String matchingCode);


    /**
     * 修改置顶时间戳
     * @param noteId
     * @param toppingTimeStamp
     */
    void updateToppingTime(@Param("noteId")String noteId,@Param("toppingTimeStamp")String toppingTimeStamp);

    /**
     * 通过日记ID查询日记
     * @param diaryId
     * @return
     */
//    DiaryRspDto queryDiary(@Param("diaryId")String diaryId);
}
