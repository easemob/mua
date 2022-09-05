package com.easemob.mua.services;

import com.easemob.mua.pojo.dto.req.AnniversaryReqDto;
import com.easemob.mua.pojo.dto.req.LoveReqDto;
import com.easemob.mua.pojo.dto.rsp.LoveRspDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author easemob_developer
 * @date 2022/7/25
 */
public interface ILoveService {

    /**
     * 创建纪念日
     * @param dto
     */
    void createLove(LoveReqDto dto);

    /**
     * 编辑纪念日
     * @param dto
     */
    void editLove(LoveReqDto dto);


    /**
     * 删除恋爱清单
     * @param userId
     * @param position
     */
    void deleteLove(String userId,int position);

    /**
     * 查询恋爱清单
     * @param userId
     * @param position
     * @return
     */
    List<LoveRspDto> selectLoveByUserId(String userId,int position);

    /**
     * 查询恋爱清单
     * @param userId
     * @return
     */
    List<LoveRspDto> selectLoveByUserId(String userId);
    /**
     * 查询恋爱清单
     * @param userId
     * @param position
     * @return
     */
    int selectLove(String userId,int position);

}
