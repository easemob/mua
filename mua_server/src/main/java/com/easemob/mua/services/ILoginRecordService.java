package com.easemob.mua.services;

import com.easemob.mua.pojo.dto.req.LoginRecordReqDto;
import com.easemob.mua.pojo.dto.rsp.LoginRecordRspDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author easemob_developer
 * @date 2022/5/23
 */
public interface ILoginRecordService {
    /**
     * 插入记录
     * @param dto
     */
   void insertRecord(LoginRecordReqDto dto);


    /**
     * 获取登录记录
     * @param mineId
     * @param taId
     * @return
     */
   List<LoginRecordRspDto> getList(String mineId,String taId);

    /**
     * 获取最后一次登录记录
     * @param userId
     * @return
     */
   LoginRecordRspDto getLastRecord(String userId);


    /**
     * 获取最后一次登录记录
     * @param mineId
     * @param taId
     * @return
     */
    List<LoginRecordRspDto> getPairLastRecord(String mineId,String taId);

    /**
     * 是否允许查看登录记录
     * @param userId
     * @param isPrivate
     */
    void setIsPrivate(String userId, boolean isPrivate);


    /**
     * 是否允许查看位置
     * @param userId
     * @param isSeeLocation
     */
    void setIsSeeLocation(String userId, boolean isSeeLocation);
}
