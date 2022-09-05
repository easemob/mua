package com.easemob.mua.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * json操作工具
 * 
 * @author easemob_developer
 * @date 2020/05/06 17:41:55
 */
public class JsonUtil {
    /**
     * 将指定对象转换为json字符串
     * 
     * @param obj
     *            指定对象
     * @return String json字符串
     * @throws JsonProcessingException
     *             抛出json处理异常
     * @author easemob_developer
     * @date 2020/05/06 17:42:01
     */
    public static String toJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

    /**
     * 将json字符串转换为指定对象
     * 
     * @param jsonStr
     *            json字符串
     * @param c
     *            指定对象的class
     * @return T 指定对象
     * @author easemob_developer
     * @throws IOException
     * @date 2020/05/06 17:42:07
     */
    public static <T> T toBean(String jsonStr, Class<T> c) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonStr, c);
    }

    /**
     * 解析部分属性,json中存在实体类不包含的key/val解析不报错，
     * @param <T>
     * @param jsonStr
     * @param c
     * @return
     * @throws IOException
     * @author maojinfeng
     * @date 2020/06/15 19:40:48
     */
    public static <T> T toSectionBean(String jsonStr, Class<T> c) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        //ObjectMapper mapper = new ObjectMapper().setVisibility(JsonMethod.FIELD, Visibility.ANY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, false);
//        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonStr, c);
    }

    public static void main(String[] args) {
        Map map=new HashMap<String,Object>(16);
        map.put("tt",123);
        map.put("ww","qwer");
        try {
            String str=toJsonString(map);
            System.out.println(str);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
             e.printStackTrace();
        }
    }
}
