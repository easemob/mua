package com.easemob.mua.services.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.easemob.mua.mapper.LoveMapper;
import com.easemob.mua.pojo.dto.req.LoveReqDto;
import com.easemob.mua.pojo.dto.rsp.LoveRspDto;
import com.easemob.mua.pojo.po.LovePo;
import com.easemob.mua.services.ILoveService;
import com.easemob.mua.utils.SnowflakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author easemob_developer
 * @date 2022/7/25
 */
@Slf4j
@Service
public class LoveServiceImpl implements ILoveService {


    @Autowired
    LoveMapper loveMapper;
    @Override
    public void createLove(LoveReqDto dto) {
        LovePo lovePo = new LovePo();
        lovePo.setId(new SnowflakeUtil().snowflakeId());
        lovePo.setUserId(dto.getUserId());
        lovePo.setImgUrl(dto.getImgUrl());
        lovePo.setPosition(dto.getPosition());
        lovePo.setCreateTime(DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
        loveMapper.insert(lovePo);
    }

    @Override
    public void editLove(LoveReqDto dto) {
        String userId = dto.getUserId();
        int position = dto.getPosition();
        String imgUrl = dto.getImgUrl();
        loveMapper.updateLove(imgUrl,userId,position);
    }

    @Override
    public void deleteLove(String userId,int position) {
        loveMapper.deleteLove(userId,position);
    }

    @Override
    public List<LoveRspDto> selectLoveByUserId(String userId,int position) {

        return loveMapper.selectLoveByUserId(userId,position);
    }

    @Override
    public List<LoveRspDto> selectLoveByUserId(String userId) {
        return loveMapper.selectLoveByOnlyUserId(userId);
    }

    @Override
    public int selectLove(String userId, int position) {
        int selectLove = loveMapper.selectLove(userId, position);
        return selectLove;
    }
}
