package com.easemob.mua.services.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.easemob.mua.mapper.LoginRecordMapper;
import com.easemob.mua.mapper.UserMapper;
import com.easemob.mua.pojo.dto.req.LoginRecordReqDto;
import com.easemob.mua.pojo.dto.rsp.LoginRecordRspDto;
import com.easemob.mua.pojo.dto.rsp.UserRspDto;
import com.easemob.mua.pojo.po.LoginRecordPo;
import com.easemob.mua.services.ILoginRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author easemob_developer
 * @date 2022/7/6
 */
@Slf4j
@Service
public class LoginRecordServiceImpl implements ILoginRecordService {
    @Autowired
    LoginRecordMapper loginRecordMapper;
    @Autowired
    UserMapper userMapper;

    @Override
    public void insertRecord(LoginRecordReqDto dto) {
        LoginRecordPo recordPo = new LoginRecordPo();
        recordPo.setRecordId(IdUtil.fastSimpleUUID());
        recordPo.setUserId(dto.getUserId());
        recordPo.setTimeStampLogin(dto.getTimeStampLogin());
        recordPo.setLoginAddress(dto.getLoginAddress());
        recordPo.setLatitude(dto.getLatitude());
        recordPo.setLongitude(dto.getLongitude());
        recordPo.setMobileInfo(dto.getMobileInfo());
        recordPo.setCreateTime(DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
        loginRecordMapper.insert(recordPo);
    }

    @Override
    public List<LoginRecordRspDto> getList(String mineId, String taId) {

        List<LoginRecordRspDto> mineList = loginRecordMapper.getList(mineId);
        List<LoginRecordRspDto> taList = loginRecordMapper.getList(taId);
        UserRspDto user = userMapper.getUserById(taId);
        if (user.isRecordPrivate()) {
            mineList.addAll(taList);
        }

        return mineList;
    }

    @Override
    public LoginRecordRspDto getLastRecord(String userId) {
        LoginRecordRspDto rspDto = loginRecordMapper.getLastList(userId);
        return rspDto;
    }

    @Override
    public List<LoginRecordRspDto> getPairLastRecord(String mineId, String taId) {
        LoginRecordRspDto mine = loginRecordMapper.getLastList(mineId);
        LoginRecordRspDto ta = loginRecordMapper.getLastList(taId);
        UserRspDto user = userMapper.getUserById(taId);
        List<LoginRecordRspDto> loginRecordRspDtos = new ArrayList<>();
        loginRecordRspDtos.add(mine);
        if (ta != null && user.isSeeLocation()) {
            loginRecordRspDtos.add(ta);
        }

        return loginRecordRspDtos;
    }

    @Override
    public void setIsPrivate(String userId, boolean isPrivate) {
        userMapper.setIsPrivate(userId,isPrivate);
    }

    @Override
    public void setIsSeeLocation(String userId, boolean isSeeLocation) {
        userMapper.setSeeLocation(userId,isSeeLocation);
    }
}
