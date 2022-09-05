package com.easemob.mua.mapper;

import com.easemob.mua.pojo.dto.rsp.UserRspDto;
import com.easemob.mua.pojo.po.UserPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author easemob_developer
 * @date 2022/5/24
 */
@Mapper
@Repository
public interface UserMapper {
    /**
     * 存储用户信息
     * @param userPo
     *  @return
     */
    Integer saveUser(UserPo userPo);


    /**
     * 修改用户信息
     * @param chatId
     * @param nickName
     * @param gender
     * @param birth
     */
    void updateUserInfo(String chatId,String nickName,int gender,String birth);

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    UserRspDto getUserById(@Param(value = "userId") String userId);

    /**
     * 查询用户ID是否存在
     * @param userId
     * @return
     */
    int selectUserId(@Param(value = "userId") String userId);

    /**
     * 获取用户信息
     * @param mineCode
     * @return
     */
    UserRspDto getUserByCode(@Param(value = "mineCode") String mineCode);

    /**
     * 查询匹配码是否存在
     * @param mineCode
     * @return
     */
    int selectUserCode(@Param(value = "mineCode") String mineCode);


    /**
     * 获取用户信息
     * @param chatId
     * @return
     */
    UserRspDto getUserByChatId(@Param(value = "chatId") String chatId);

    /**
     * 查询匹配码是否存在
     * @param chatId
     * @return
     */
    int selectChatId(@Param(value = "chatId") String chatId);
    /**
     * 配对码查询用户信息
     * @param matchingCode
     * @return
     */
    List<UserRspDto> selectMatchingCode(@Param(value = "matchingCode")String matchingCode);

    /**
     * 修改生日以及年龄
     * @param userId
     * @param birth
     * @return
     */
    Integer updateUserBirth(String userId,String birth);


    /**
     * 修改用户昵称
     * @param userId
     * @param nickName
     * @return
     */
    Integer updateUserNickName(String userId,String nickName);

    /**
     * 修改用户昵称
     * @param userId
     * @param avatar
     * @return
     */
    Integer updateUserAvatar(String userId,String avatar);

    /**
     * 修改用户匹配状态
     * @param userId
     * @param matchingCode
     * @param isMatching
     * @return
     */
    Integer updateMatchingStatus(String userId,String matchingCode,boolean isMatching);


    /**
     * 是否允许查看登录记录
     * @param userId
     * @param isPrivate
     */
    void setIsPrivate(@Param("userId") String userId,@Param("isRecordPrivate") boolean isPrivate);


    /**
     * 是否允许查看登录记录
     * @param userId
     * @param isPrivate
     */
    void setSeeLocation(@Param("userId") String userId,@Param("isSeeLocation") boolean isPrivate);
}
