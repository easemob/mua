package com.easemob.mua.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.easemob.mua.base.CodeEnum;
import com.easemob.mua.bean.TokenBean;
import com.easemob.mua.exception.BizException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author easemob_developer
 * @date 2022/6/2
 */
@Slf4j
public class ImHttp {

    static String HOST = "https://a1.easecdn.com/";

    static String DEV_CLIENT_KEY ="";
    static String DEV_CLIENT_SECRET = "";
    static String DEV_APP_KEY = "";
    static String DEV_APP_TOKEN = HOST+DEV_APP_KEY+"/token";
    static String DEV_REG_IM = HOST+DEV_APP_KEY+"/users";

    static String PRODUCT_CLIENT_KEY ="";
    static String PRODUCT_CLIENT_SECRET = "";
    static String PRODUCT_APP_KEY = "";
    static String PRODUCT_APP_TOKEN = HOST+PRODUCT_APP_KEY+"/token";
    static String PRODUCT_REG_IM = HOST+PRODUCT_APP_KEY+"/users";

    private static String envConfig = "product";
    static String ENV_CONFIG = "product";

    public static int OK = 200;
    static long TTL = 24 * 60 * 60 * 365;

    static Map APP_TOKEN = new HashMap();

    public static void getAppToken(){
        Map map = new HashMap();
        String path = "";
        if (StrUtil.equals(envConfig,ENV_CONFIG)) {
            map.put("grant_type","client_credentials");
            map.put("client_id", PRODUCT_CLIENT_KEY);
            map.put("client_secret", PRODUCT_CLIENT_SECRET);
            map.put("ttl",TTL);
            path = PRODUCT_APP_TOKEN;
        } else {
            map.put("grant_type","client_credentials");
            map.put("client_id", DEV_CLIENT_KEY);
            map.put("client_secret", DEV_CLIENT_SECRET);
            map.put("ttl",TTL);
            path = DEV_APP_TOKEN;
        }

        try {
            String jsonString = JsonUtil.toJsonString(map);
            log.info("getApp : "+jsonString);
            HttpResponse response = HttpRequest.post(path)
                    .header(Header.CONTENT_TYPE, "application/json")
                    .body(jsonString)
                    .timeout(20000)
                    .execute();
            int status = response.getStatus();
            log.info("getApp : "+response.getStatus());
            if (status != OK) {
                throw new BizException(CodeEnum.ERROR, response.body());
            }
            String body = response.body();
            log.info("getApp : "+response.body());
            if (JSONUtil.isJsonObj(body)) {
                TokenBean tokenBean = JSONUtil.toBean(body, TokenBean.class);
                APP_TOKEN.put("token",tokenBean.getAccess_token());
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    public static int regIm(String uName,String pwd,String nickName){
        Map map = new HashMap<String, String>();
        map.put("username",uName);
        map.put("password",pwd);
        map.put("nickname",nickName);

        String path = "";
        if (StrUtil.equals(envConfig,ENV_CONFIG)) {
            path = PRODUCT_REG_IM;
        } else {
            path = DEV_REG_IM;
        }

        JSONObject object = JSONUtil.parseObj(map);
        log.info("regIm : "+object.toString());
        String token = (String) APP_TOKEN.get("token");
        log.info("regIm token : "+token);
        HttpResponse response = HttpRequest.post(path)
                .header(Header.CONTENT_TYPE, "application/json")
                .header(Header.AUTHORIZATION, "Bearer "+token)
                .body(object.toString())
                .timeout(20000)
                .execute();
        int status = response.getStatus();
        log.info("regIm : "+status);
        if (status != OK) {
            throw new BizException(CodeEnum.ERROR, response.body());
        }
        return status;
    }

}
