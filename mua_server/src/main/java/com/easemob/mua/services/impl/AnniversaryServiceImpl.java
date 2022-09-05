package com.easemob.mua.services.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.easemob.mua.mapper.AnniversaryMapper;
import com.easemob.mua.pojo.dto.req.AnniversaryReqDto;
import com.easemob.mua.pojo.dto.rsp.AnniversaryRspDto;
import com.easemob.mua.pojo.po.AnniversaryPo;
import com.easemob.mua.services.IAnniversaryService;
import com.easemob.mua.utils.SnowflakeShardingKeyGenerator;
import com.easemob.mua.utils.SnowflakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author easemob_developer
 * @date 2022/7/11
 */
@Slf4j
@Service
public class AnniversaryServiceImpl implements IAnniversaryService {


    @Autowired
    AnniversaryMapper anniversaryMapper;

    @Override
    public void createAnniversary(AnniversaryReqDto dto) {
        AnniversaryPo po = new AnniversaryPo();
        po.setId(new SnowflakeUtil().snowflakeId());
        po.setUserId(dto.getUserId());
        po.setMatchingCode(dto.getMatchingCode());
        po.setName(dto.getName());
        po.setTime(dto.getTime());
        po.setRepeat(dto.getRepeat());
        po.setCreateTime(DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
        anniversaryMapper.insert(po);
//        anniversaryMapper.createAnniversary(po);
    }

    @Override
    public List<AnniversaryRspDto> getAllByMatchingCode(String matchingCode) {
        return anniversaryMapper.getAllByMatchingCode(matchingCode);
    }

    @Override
    public void editAnniversary(AnniversaryReqDto dto) {
        AnniversaryPo po = new AnniversaryPo();
        po.setId(dto.getId());
        po.setUserId(dto.getUserId());
        po.setMatchingCode(dto.getMatchingCode());
        po.setName(dto.getName());
        po.setTime(dto.getTime());
        po.setRepeat(dto.getRepeat());
        po.setCreateTime(DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
        anniversaryMapper.updateByPrimaryKeySelective(po);
    }

    @Override
    public void deleteAnniversary(String id) {
        anniversaryMapper.deleteByPrimaryKey(id);
    }
}
