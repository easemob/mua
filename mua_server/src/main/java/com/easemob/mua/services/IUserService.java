package com.easemob.mua.services;

import com.easemob.mua.exception.BizException;
import com.easemob.mua.pojo.dto.req.UserReqDto;
import com.easemob.mua.pojo.dto.rsp.ChatAccountRspDto;
import com.easemob.mua.pojo.dto.rsp.MatchingRspDto;
import com.easemob.mua.pojo.dto.rsp.UserRspDto;
import org.apache.ibatis.annotations.Param;

/**
 * @author easemob_developer
 * @date 2022/5/23
 */
public interface IUserService {


    /**
     * 创建环信账号
     * @return
     */
    ChatAccountRspDto createChatAccount();

    /**
     * 存储用户信息
     * @param reqDto
     * @return
     * @throws BizException
     */
    UserRspDto saveUser(UserReqDto reqDto) throws BizException;

    /**
     * 查询用户信息
     * @param userId
     * @return
     * @throws BizException
     */
    UserRspDto getUserById(String userId) throws BizException;

    /**
     * 根据配对码获取用户信息
     * @param mineCode
     * @return
     */
    UserRspDto getUserByCode(String mineCode);

    /**
     * 修改用户生日
     * @param userId
     * @param birth
     * @return
     * @throws BizException
     */
    Integer updateUserBirth(String userId,String birth) throws BizException;

    /**
     * 修改用户昵称
     * @param userId
     * @param nickName
     * @return
     * @throws BizException
     */
    Integer updateUserNickName(String userId,String nickName) throws BizException;


    /**
     *  修改用户头像
     * @param avatar
     * @param userId
     * @return
     */
    Integer updateUserAvatar(String avatar,String userId);


    /**
     * 修改用户匹配状态
     * @param userId
     * @param matchingCode
     * @param isMatching
     * @return
     * @throws BizException
     */
    Integer updateUserMatchingStatus(String userId, String matchingCode, boolean isMatching) throws BizException;


    /**
     * 用户匹配
     * @param code1
     * @param code2
     * @return
     */
    MatchingRspDto matching(String code1,String code2);

    /**
     * 用户解除匹配
     * @param code
     * @return
     */
    Integer unMatch(String code);


    /**
     * 上传欢迎页
     * @param avatar
     * @param chatId
     * @return
     */
//    String uploadAvatar(String avatar,String chatId);

    /**
     * 获取欢迎页地址
     * @param userId
     * @return
     */
    MatchingRspDto getSplash(String userId);


    /**
     * 同步猫咪状态时间
     * @param matchingCode
     * @param time
     */
    void setKittyStatusTime(String matchingCode,String time);

}
