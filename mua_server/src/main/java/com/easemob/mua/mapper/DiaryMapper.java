package com.easemob.mua.mapper;

import com.easemob.mua.pojo.dto.req.DiaryReqDto;
import com.easemob.mua.pojo.dto.rsp.DiaryRspDto;
import com.easemob.mua.pojo.po.DiaryPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

/**
 * @author easemob_developer
 * @date 2022/6/15
 */
@Mapper
@Repository
public interface DiaryMapper extends MyMapper<DiaryPo> {
    /**
     * 写日记
     * @param dto
     */
    void writeDiary(DiaryPo dto);


    /**
     * 删除日记
     * @param diaryId
     */
    void deleteDiary(String diaryId);

    /**
     * 解除匹配删除匹配对应的日记
     * @param matchingCode
     */
    void deleteDiaryByMatchingCode(@Param("matchingCode")String matchingCode);


    /**
     * 查询当月的日记
     * @param matchingCode
     * @param diaryMonth
     * @return
     */
    List<DiaryRspDto> getDiaryList(String matchingCode, String diaryMonth);

    /**
     * 通过日记ID查询日记
     * @param diaryId
     * @return
     */
    DiaryRspDto queryDiary(@Param("diaryId")String diaryId);
}
