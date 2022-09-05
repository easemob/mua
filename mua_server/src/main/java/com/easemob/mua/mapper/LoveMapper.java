package com.easemob.mua.mapper;

import com.easemob.mua.pojo.dto.rsp.AnniversaryRspDto;
import com.easemob.mua.pojo.dto.rsp.LoveRspDto;
import com.easemob.mua.pojo.po.AnniversaryPo;
import com.easemob.mua.pojo.po.LovePo;
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
public interface LoveMapper extends MyMapper<LovePo>{

    /**
     * 更新恋爱清单
     * @param imgUrl
     * @param userId
     * @param position
     */
    void updateLove(@Param("imgUrl") String imgUrl,@Param("userId") String userId,@Param("position")int position);


    /**
     * 删除恋爱清单
     * @param userId
     * @param position
     * @return
     */
    void deleteLove(@Param("userId") String userId,@Param("position")int position);

    /**
     * 查询恋爱清单
     * @param userId
     * @param position
     * @return
     */
    List<LoveRspDto> selectLoveByUserId(@Param("userId") String userId,@Param("position")int position);
    /**
     * 查询恋爱清单
     * @param userId
     * @return
     */
    List<LoveRspDto> selectLoveByOnlyUserId(@Param("userId") String userId);

    /**
     * 查询恋爱清单
     * @param userId
     * @param position
     * @return
     */
    int selectLove(@Param("userId") String userId,@Param("position")int position);
}
