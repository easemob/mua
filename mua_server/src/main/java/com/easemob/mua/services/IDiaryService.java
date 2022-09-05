package com.easemob.mua.services;

import com.easemob.mua.pojo.dto.req.DiaryReqDto;
import com.easemob.mua.pojo.dto.rsp.DiaryRspDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author easemob_developer
 * @date 2022/5/23
 */
public interface IDiaryService {
    // 写日记、删除日记、编辑日记 、获取日记列表、跟进ID查询日记

    /**
     * 写日记
     * @param file
     * @param dto
     */
    void writeDiary(MultipartFile file ,DiaryReqDto dto);

    /**
     * 写日记
     * @param dto
     */
    void writeDiary(DiaryReqDto dto);

    /**
     * 编辑日记
     * @param file
     * @param dto
     */
    void editDiary(MultipartFile file,DiaryReqDto dto);

    /**
     * 编辑日记
     * @param dto
     */
    void editDiary(DiaryReqDto dto);

    /**
     * 删除日记
     * @param diaryId
     */
    void deleteDiary(String diaryId);


    /**
     * 查询当月的日记
     * @param matchingCode
     * @param month
     * @return
     */
    List<DiaryRspDto> getDiaryList(String matchingCode, String month);

    /**
     * 通过日记ID查询日记
     * @param diaryId
     * @return
     */
    DiaryRspDto queryDiary(String diaryId);

}
