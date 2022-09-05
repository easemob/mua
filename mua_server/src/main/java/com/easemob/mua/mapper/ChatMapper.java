package com.easemob.mua.mapper;

import com.easemob.mua.pojo.dto.rsp.ChatAccountRspDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @author easemob_developer
 * @date 2022/5/26
 */
@Mapper
@Repository
public interface ChatMapper {

    /**
     * 注册环信账号ID
     * @param chatId
     * @param time
     * @return
     */
    Integer createChatAccount(String chatId, String time);

    /**
     * 根据ID查询环信账号信息
     * @param chatId
     * @return
     */
    ChatAccountRspDto getChatInfoById(@Param(value = "chatId") String chatId);

    /**
     *查询ChatId是否存在
     * @param chatId
     * @return
     */
    Integer selectChatId(String chatId);

}
