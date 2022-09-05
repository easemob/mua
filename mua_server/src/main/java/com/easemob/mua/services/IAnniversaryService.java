package com.easemob.mua.services;

import com.easemob.mua.pojo.dto.req.AnniversaryReqDto;
import com.easemob.mua.pojo.dto.rsp.AnniversaryRspDto;

import java.util.List;

/**
 * @author easemob_developer
 * @date 2022/7/11
 */
public interface IAnniversaryService {

    /**
     * 创建纪念日
     * @param dto
     */
    void createAnniversary(AnniversaryReqDto dto);



    /**
     * 根据匹配码查询纪念日
     * @param matchingCode
     * @return
     */
    List<AnniversaryRspDto> getAllByMatchingCode(String matchingCode);


    /**
     * 编辑纪念日
     * @param dto
     */
    void editAnniversary(AnniversaryReqDto dto);

    /**
     * 删除纪念日
     * @param id
     */
    void deleteAnniversary(String id);
}
