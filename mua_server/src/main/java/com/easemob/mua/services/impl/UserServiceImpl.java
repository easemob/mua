package com.easemob.mua.services.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.easemob.mua.base.CodeEnum;
import com.easemob.mua.exception.BizException;
import com.easemob.mua.mapper.*;
import com.easemob.mua.pojo.dto.req.UserReqDto;
import com.easemob.mua.pojo.dto.rsp.ChatAccountRspDto;
import com.easemob.mua.pojo.dto.rsp.MatchingRspDto;
import com.easemob.mua.pojo.dto.rsp.UserRspDto;
import com.easemob.mua.pojo.po.AnniversaryPo;
import com.easemob.mua.pojo.po.MatchingPo;
import com.easemob.mua.pojo.po.UserPo;
import com.easemob.mua.services.IUserService;
import com.easemob.mua.utils.ImHttp;
import com.easemob.mua.utils.SnowflakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author easemob_developer
 * @date 2022/5/23
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    MatchingMapper matchingMapper;
    @Autowired
    ChatMapper chatMapper;

    @Autowired
    DiaryMapper diaryMapper;
    @Autowired
    AnniversaryMapper anniversaryMapper;

    @Override
    public ChatAccountRspDto createChatAccount() {
        String chatId = RandomUtil.randomString(28);
        String  chatAccount = "mua_"+chatId;
        chatMapper.createChatAccount(chatAccount, DateUtil.format(new Date(),DatePattern.NORM_DATETIME_PATTERN));
        ChatAccountRspDto accountRspDto = chatMapper.getChatInfoById(chatAccount);
        return accountRspDto;
    }

    @Override
    public UserRspDto saveUser(UserReqDto reqDto) throws BizException {
        UserPo userPo = new UserPo();
        userPo.setUserid(IdUtil.fastSimpleUUID());
        String chatAccount = creatChatId();
        chatMapper.createChatAccount(chatAccount, DateUtil.format(new Date(),DatePattern.NORM_DATETIME_PATTERN));
        userPo.setChatId(chatAccount);
        userPo.setNickname(reqDto.getNickname());
        userPo.setAvatar(reqDto.getAvatar());
        userPo.setGender(reqDto.getGender());
        userPo.setBirth(reqDto.getBirth());
        userPo.setMineCode(creatUserCodeU());
        userPo.setMatching(false);
        userPo.setMatchingCode("");
        userPo.setRecordPrivate(true);
        userPo.setSeeLocation(true);
        userPo.setCreateTime(DateUtil.format(new Date(),DatePattern.NORM_DATETIME_PATTERN));

        ImHttp.getAppToken();
        int code = ImHttp.regIm(chatAccount, "1qaz!QAZ", reqDto.getNickname());
        if (code!=ImHttp.OK){
            throw new BizException(CodeEnum.ERROR,"IM注册失败!");
        }
        userMapper.saveUser(userPo);


        System.out.println("ID : "+userPo.getUserid());
        return getUserById(userPo.getUserid());
    }



    @Override
    public UserRspDto getUserById(String userId) throws BizException {
        if (StrUtil.isEmpty(userId)) {
            throw new BizException(CodeEnum.MISS_PARAM,"用户ID不能为空!");
        }
        int have = userMapper.selectUserId(userId);
        if (have == 0){
            throw new BizException(CodeEnum.NO_RESULT,"用户ID不存在!");
        }
        UserRspDto userRspDto = userMapper.getUserById(userId);
        return userRspDto;
    }

    @Override
    public UserRspDto getUserByCode(String mineCode) throws BizException {
        if (StrUtil.isEmpty(mineCode)) {
            throw new BizException(CodeEnum.MISS_PARAM,"用户匹配码不能为空!");
        }
        int have = userMapper.selectUserCode(mineCode);
        if (have == 0){
            throw new BizException(CodeEnum.NO_RESULT,"用户匹配码不存在!");
        }
        UserRspDto userRspDto = userMapper.getUserByCode(mineCode);
        return userRspDto;
    }




    @Override
    public Integer updateUserBirth(String userId, String birth) throws BizException {
        if (StrUtil.isEmpty(userId)) {
            throw new BizException(CodeEnum.MISS_PARAM,"用户ID不能为空!");
        }
        int have = userMapper.selectUserId(userId);
        if (have == 0){
            throw new BizException(CodeEnum.NO_RESULT,"用户ID不存在!");
        }
        Integer integer = userMapper.updateUserBirth(userId, birth);
        return integer;
    }

    @Override
    public Integer updateUserNickName(String userId, String nickName) throws BizException {
        if (StrUtil.isEmpty(userId)) {
            throw new BizException(CodeEnum.MISS_PARAM,"用户ID不能为空!");
        }
        int have = userMapper.selectUserId(userId);
        if (have == 0){
            throw new BizException(CodeEnum.NO_RESULT,"用户ID不存在!");
        }
        Integer integer = userMapper.updateUserNickName(userId, nickName);
        return integer;
    }

    @Override
    public Integer updateUserAvatar(String avatar, String userId) {
        Integer integer = userMapper.updateUserAvatar(userId, avatar);
        return integer;
    }

    @Override
    public Integer updateUserMatchingStatus(String userId,String matchingCode, boolean isMatching) throws BizException {
        Integer integer = userMapper.updateMatchingStatus(userId,matchingCode, isMatching);
        return integer;
    }

    @Override
    public MatchingRspDto matching(String code1, String code2) {
        if (StrUtil.isEmpty(code1) || StrUtil.isEmpty(code2)) {
            throw new BizException(CodeEnum.MISS_PARAM,"匹配码不能为空!");
        }
        UserRspDto user1 = userMapper.getUserByCode(code1);
        UserRspDto user2 = userMapper.getUserByCode(code2);
        if (user1 == null || user2 == null){
            throw new BizException(CodeEnum.NO_RESULT,"匹配码不存在!");
        }

        if (user1.isMatching() || user2.isMatching()){
            throw new BizException(CodeEnum.NO_RESULT,"用户已匹配不能再次匹配!");
        }

        if (user1.getGender() == user2.getGender()){
            throw new BizException(CodeEnum.ERROR,"同性别不能匹配!");
        }
        if (StrUtil.equals(user1.getMineCode(),user2.getMineCode())){
            throw new BizException(CodeEnum.ERROR,"自己不能和自己匹配!");
        }

        MatchingPo matchingPo = new MatchingPo();
        matchingPo.setMatchingId(IdUtil.fastSimpleUUID());
        String matchingCode = creatMatchingCode();
        matchingPo.setMatchingCode(matchingCode);
        matchingPo.setSplashUrl("");
        matchingPo.setAlbumUrl("");
        matchingPo.setMatchingTime(System.currentTimeMillis());
        matchingPo.setMatchingDate(DateUtil.format(new Date(),DatePattern.NORM_DATETIME_PATTERN));
        matchingPo.setKittyStatusTime(0);
        matchingMapper.userMatching(matchingPo);
        MatchingRspDto matchingRspDto = matchingMapper.getMatchingInfoById(matchingPo.getMatchingId());
        if (matchingRspDto == null) {
            throw new BizException(CodeEnum.NO_RESULT,"匹配失败!");
        }
        updateUserMatchingStatus(user1.getUserid(),matchingCode,true);
        updateUserMatchingStatus(user2.getUserid(),matchingCode,true);

        List<UserRspDto> userRspDtos = new ArrayList<>();
        userRspDtos.add(userMapper.getUserByCode(code1));
        userRspDtos.add(userMapper.getUserByCode(code2));
        matchingRspDto.setUserRsp(userRspDtos);

        AnniversaryPo po = new AnniversaryPo();
        po.setId(new SnowflakeUtil().snowflakeId());
        po.setUserId(user1.getUserid());
        po.setMatchingCode(matchingCode);
        po.setName("我们在一起");
        po.setTime(DateUtil.current());
        po.setRepeat(false);
        po.setCreateTime(DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
        anniversaryMapper.insert(po);

        return matchingRspDto;
    }

    @Override
    public Integer unMatch(String matchingId) {
        if (StrUtil.isEmpty(matchingId) ) {
            throw new BizException(CodeEnum.MISS_PARAM,"参数不能为空!");
        }

        MatchingRspDto matchingRspDto = matchingMapper.getMatchingInfoById(matchingId);
        if (matchingRspDto == null){
            throw new BizException(CodeEnum.NO_RESULT,"数据不存在!");
        }
        String matchingCode = matchingRspDto.getMatchingCode();
        List<UserRspDto> userRspDtos = userMapper.selectMatchingCode(matchingCode);

        Integer integer = matchingMapper.unMatch(matchingId);
        for (UserRspDto user: userRspDtos) {
            updateUserMatchingStatus(user.getUserid(),"",false);
        }

        //TODO 解除匹配 删除日记 便签
//        diaryMapper.deleteDiaryByMatchingCode(matchingCode);
//
//        Example example = new Example(AnniversaryPo.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("matching_code",matchingCode);
//        anniversaryMapper.deleteByExample(example);

        return integer;
    }



    @Override
    public MatchingRspDto getSplash(String userId){

        if (StrUtil.isEmpty(userId)) {
            throw new BizException(CodeEnum.MISS_PARAM,"用户ID不能为空!");
        }
        int have = userMapper.selectUserId(userId);

        if (have == 0){
            throw new BizException(CodeEnum.NO_RESULT,"用户ID不存在!");
        }

        UserRspDto userRspDto = getUserById(userId);
        if (!userRspDto.isMatching()){
            throw new BizException(CodeEnum.UN_MATCH,"该用户未匹配成功，不能定制欢迎页!");
        }
        log.info("MatchingCode: ", userRspDto.getMatchingCode());

        MatchingRspDto matchingRspDto = matchingMapper.getMatchingInfoByCode(userRspDto.getMatchingCode());
        List<UserRspDto> userRspDtoList = userMapper.selectMatchingCode(userRspDto.getMatchingCode());
        matchingRspDto.setUserRsp(userRspDtoList);
        return matchingRspDto;
    }

    @Override
    public void setKittyStatusTime(String matchingCode, String time) {
        if (StrUtil.isEmpty(matchingCode)) {
            throw new BizException(CodeEnum.MISS_PARAM,"匹配码不能为空!");
        }

        int have = matchingMapper.selectMatchingCode(matchingCode);
        if (have == 0) {
            throw new BizException(CodeEnum.MISS_PARAM,"匹配码不存在!");
        }
        matchingMapper.setKittyStatusTime(matchingCode,time);
    }


    /**
     * 生成匹配码
     * @return 匹配码
     */
    private String creatUserCodeU() {
        boolean b = true;
        String usercode = "";
        while (b) {
            usercode = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
            int have = userMapper.selectUserCode(usercode);
            if (have == 0) {
                b = false;
            }
        }
        return usercode;
    }


    /**
     * 生成配对码
     * @return 配对码
     */
    private String creatMatchingCode() {
        boolean b = true;
        String matchingCode = "";
        while (b) {
            matchingCode = RandomUtil.randomString(32);
            int have = matchingMapper.selectMatchingCode(matchingCode);
            if (have == 0) {
                b = false;
            }
        }
        return matchingCode;
    }

    /**
     * 生成聊天ID
     * @return 配对码
     */
    private String creatChatId() {
        boolean b = true;
        String chatAccount = "";
        while (b) {
             chatAccount = "mua_"+RandomUtil.randomString(12);
            int have = chatMapper.selectChatId(chatAccount);
            if (have == 0) {
                b = false;
            }
        }
        return chatAccount;
    }
}
