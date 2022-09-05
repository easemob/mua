package com.easemob.mua.mapper;

import com.easemob.mua.pojo.dto.rsp.LoginRecordRspDto;
import com.easemob.mua.pojo.po.DiaryPo;
import com.easemob.mua.pojo.po.LoginRecordPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author easemob_developer
 * @date 2022/7/6
 */
@Mapper
@Repository
public interface LoginRecordMapper extends MyMapper<LoginRecordPo> {

    /**
     * 获取登录列表
     * @param userId
     * @return
     */
    List<LoginRecordRspDto> getList(@Param("userId") String userId);

    /**
     * 获取最后一次登录信息
     * @param userId
     * @return
     */
    LoginRecordRspDto getLastList(@Param("userId") String userId);
}
