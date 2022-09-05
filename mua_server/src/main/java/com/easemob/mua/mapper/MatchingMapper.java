package com.easemob.mua.mapper;

import com.easemob.mua.pojo.dto.rsp.MatchingRspDto;
import com.easemob.mua.pojo.po.MatchingPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author easemob_developer
 * @date 2022/5/25
 */
@Mapper
@Repository
public interface MatchingMapper {

    /**
     * 存放匹配信息
     * @param po
     * @return
     */
    Integer userMatching(MatchingPo po);

    /**
     * 根据ID查询匹配信息
     * @param id
     * @return
     */
    MatchingRspDto getMatchingInfoById(@Param("matchingId") String id);


    /**
     * 解除匹配关系
     * @param id
     * @return
     */
    Integer unMatch(@Param("matchingId") String id);


    /**
     * 根据ID查询匹配信息
     * @param matchingCode
     * @return
     */
    MatchingRspDto getMatchingInfoByCode(@Param("matchingCode") String matchingCode);

    /**
     * 根据ID查询匹配信息
     * @param matchingCode
     * @return
     */
    Integer selectMatchingCode(@Param("matchingCode") String matchingCode);


    /**
     * 修改欢迎页
     * @param matchingCode
     * @param splashUrl
     * @return
     */
    Integer setSplashUrlByCode(@Param("matchingCode") String matchingCode,String splashUrl);


    /**
     * 修改相册
     * @param matchingCode
     * @param albumUrl
     * @return
     */
    Integer setAlbumUrlByCode(@Param("matchingCode") String matchingCode,String albumUrl);


    /**
     * 修改相册
     * @param matchingCode
     * @param time
     * @return
     */
    Integer setKittyStatusTime(@Param("matchingCode") String matchingCode,@Param("kittyStatusTime")String time);

}
