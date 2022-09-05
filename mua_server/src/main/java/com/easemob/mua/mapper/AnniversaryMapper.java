package com.easemob.mua.mapper;

import com.easemob.mua.pojo.dto.rsp.AnniversaryRspDto;
import com.easemob.mua.pojo.po.AnniversaryPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author easemob_developer
 * @date 2022/7/11
 */
@Mapper
@Repository
public interface AnniversaryMapper extends MyMapper<AnniversaryPo>{

    /**
     * 创建纪念日
     * @param po
     * @return
     */
    int createAnniversary(AnniversaryPo po);

    /**
     * 根据匹配码查询纪念日
     * @param matchingCode
     * @return
     */
    List<AnniversaryRspDto> getAllByMatchingCode(@Param("matchingCode")String matchingCode);
}
