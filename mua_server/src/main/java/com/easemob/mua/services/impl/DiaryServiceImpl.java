package com.easemob.mua.services.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.FontUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.easemob.mua.base.CodeEnum;
import com.easemob.mua.exception.BizException;
import com.easemob.mua.mapper.DiaryMapper;
import com.easemob.mua.pojo.dto.req.DiaryReqDto;
import com.easemob.mua.pojo.dto.rsp.DiaryRspDto;
import com.easemob.mua.pojo.dto.rsp.UploadImgRspDto;
import com.easemob.mua.pojo.po.DiaryPo;
import com.easemob.mua.services.IDiaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author easemob_developer
 * @date 2022/5/23
 */
@Slf4j
@Service
public class DiaryServiceImpl implements IDiaryService {

    @Value("${diaryPic.dir}")
    private String diaryPic;
    @Autowired
    DiaryMapper diaryMapper;
    @Override

    public void writeDiary(MultipartFile file,DiaryReqDto dto) {
        if (StrUtil.isEmpty(dto.getMatchingCode())) {
            throw new BizException(CodeEnum.MISS_PARAM,"关键参数不能为空!");
        }

        DiaryPo diaryPo = new DiaryPo();
        diaryPo.setDiaryId(IdUtil.fastSimpleUUID());
        diaryPo.setMatchingCode(dto.getMatchingCode());
        diaryPo.setUserId(dto.getUserId());
        String format = DateUtil.format(new Date(Long.valueOf(dto.getDiaryTimeStamp())), DatePattern.NORM_MONTH_PATTERN);
        diaryPo.setDiaryMonth(format);
        diaryPo.setDiaryTimeStamp(dto.getDiaryTimeStamp());
        diaryPo.setMood(dto.getMood());
        diaryPo.setContent(dto.getContent());
        diaryPo.setMoodReason(dto.getMoodReason());
        diaryPo.setIsPrivate(dto.isPrivate());
        diaryPo.setCreateTime(DateUtil.format(new Date(),DatePattern.NORM_DATETIME_PATTERN));

        if (StrUtil.isEmpty(file.getOriginalFilename())) {
            diaryPo.setPic("");
        } else {
            diaryPo.setPic(uploadDiaryPic(file,dto.getMatchingCode()));
        }
        diaryMapper.writeDiary(diaryPo);
    }

    @Override
    public void writeDiary(DiaryReqDto dto) {
        if (StrUtil.isEmpty(dto.getMatchingCode())) {
            throw new BizException(CodeEnum.MISS_PARAM,"关键参数不能为空!");
        }

        DiaryPo diaryPo = new DiaryPo();
        diaryPo.setDiaryId(IdUtil.fastSimpleUUID());
        diaryPo.setMatchingCode(dto.getMatchingCode());
        diaryPo.setUserId(dto.getUserId());
        String format = DateUtil.format(new Date(Long.valueOf(dto.getDiaryTimeStamp())), DatePattern.NORM_MONTH_PATTERN);
        diaryPo.setDiaryMonth(format);
        diaryPo.setDiaryTimeStamp(dto.getDiaryTimeStamp());
        diaryPo.setMood(dto.getMood());
        diaryPo.setContent(dto.getContent());
        diaryPo.setMoodReason(dto.getMoodReason());
        diaryPo.setIsPrivate(dto.isPrivate());
        diaryPo.setCreateTime(DateUtil.format(new Date(),DatePattern.NORM_DATETIME_PATTERN));
        diaryPo.setPic("");
        diaryMapper.writeDiary(diaryPo);
    }

    @Override
    public void editDiary(MultipartFile file,DiaryReqDto dto) {
        DiaryPo diaryPo = new DiaryPo();
        diaryPo.setDiaryId(dto.getDiaryId());
        diaryPo.setMatchingCode(dto.getMatchingCode());
        diaryPo.setDiaryMonth(dto.getDiaryMonth());
        diaryPo.setDiaryTimeStamp(dto.getDiaryTimeStamp());
        diaryPo.setMood(dto.getMood());
        diaryPo.setMoodReason(dto.getMoodReason());
        diaryPo.setContent(dto.getContent());
        diaryPo.setIsPrivate(dto.isPrivate());
        diaryPo.setCreateTime(DateUtil.format(new Date(),DatePattern.NORM_DATETIME_PATTERN));
        if (StrUtil.isEmpty(file.getOriginalFilename())) {
            diaryPo.setPic("");
        } else {
            diaryPo.setPic(uploadDiaryPic(file,dto.getMatchingCode()));
        }
        log.info("editDiary  DiaryId :", diaryPo.getDiaryId());
        int i = diaryMapper.updateByPrimaryKeySelective(diaryPo);
        log.info("editDiary  ", i);
    }

    @Override
    public void editDiary(DiaryReqDto dto) {
        DiaryPo diaryPo = new DiaryPo();
        diaryPo.setDiaryId(dto.getDiaryId());
        diaryPo.setMatchingCode(dto.getMatchingCode());
        diaryPo.setDiaryMonth(dto.getDiaryMonth());
        diaryPo.setDiaryTimeStamp(dto.getDiaryTimeStamp());
        diaryPo.setMood(dto.getMood());
        diaryPo.setMoodReason(dto.getMoodReason());
        diaryPo.setContent(dto.getContent());
        diaryPo.setIsPrivate(dto.isPrivate());
        diaryPo.setCreateTime(DateUtil.format(new Date(),DatePattern.NORM_DATETIME_PATTERN));
        log.info("editDiary  DiaryId :", diaryPo.getDiaryId());
        int i = diaryMapper.updateByPrimaryKeySelective(diaryPo);
        log.info("editDiary  ", i);
    }

    @Override
    public void deleteDiary(String diaryId) {
        if (StrUtil.isEmpty(diaryId)) {
            throw new BizException(CodeEnum.MISS_PARAM,"日记Id不能为空!");
        }
        diaryMapper.deleteDiary(diaryId);
    }

    @Override
    public List<DiaryRspDto> getDiaryList(String matchingCode, String month) {
        if (StrUtil.isEmpty(matchingCode)) {
            throw new BizException(CodeEnum.MISS_PARAM,"查询条件不能为空!");
        }
        String monthParams = month;
        if (StrUtil.isEmpty(month)) {
            monthParams = DateUtil.format(new Date(), DatePattern.NORM_MONTH_PATTERN);
        }

        List<DiaryRspDto> diaryList = diaryMapper.getDiaryList(matchingCode, monthParams);
        return diaryList;
    }

    @Override
    public DiaryRspDto queryDiary(String diaryId) {
        if (StrUtil.isEmpty(diaryId)) {
            throw new BizException(CodeEnum.MISS_PARAM,"日记Id不能为空!");
        }
        DiaryRspDto diaryRspDto = diaryMapper.queryDiary(diaryId);
        return diaryRspDto;
    }


    /**
     * 上传日记照片
     * @param multipartFile
     * @param code
     * @return
     */
    public String uploadDiaryPic(MultipartFile multipartFile,String code) {
        String fileName = code +"_"+DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN)+"_"+multipartFile.getOriginalFilename();
        File file = new File(diaryPic + fileName);
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            log.error("uploadDiaryPic error,{}", e.getMessage());
            throw new BizException(CodeEnum.ERROR,"图片上传失败!");
        }
        String fileUrl =  "file/" + fileName;
        return fileUrl;
    }
}
