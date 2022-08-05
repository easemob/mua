package com.community.mua.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Pattern;


public class GsonUtils {

    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(String.class, new TypeAdapter<String>() {
                private final Pattern p = Pattern.compile("[0-9]+\\.0*"); // 匹配0.0或1.0等

                @Override
                public void write(JsonWriter out, String value) throws IOException {
                    out.value(value);
                }

                @Override
                public String read(JsonReader in) throws IOException {
                    if (!in.hasNext()) return null;
                    String s = in.nextString();
                    return p.matcher(s).matches() ? s.substring(0, s.indexOf('.')) : s;
                }
            })
            .create();

    public static Gson getGson() {
        return gson;
    }

    private GsonUtils() {
    }

    public static <T> T toBean(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static <T> T toBean(String json, Type type) {
        return gson.fromJson(json, type);
    }


    public static <T> List<T> toList(String json, Class<T> clazz) {
        //泛型转换
        Type type = new ParameterizedTypeListImpl(clazz);
        return gson.fromJson(json, type);
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    private static class ParameterizedTypeListImpl implements ParameterizedType {
        Class clazz;

        public ParameterizedTypeListImpl(Class clz) {
            clazz = clz;
        }

        @Override
        public Type[] getActualTypeArguments() {
            //返回实际类型组成的数据
            return new Type[]{clazz};
        }

        @Override
        public Type getRawType() {
            //返回原生类型
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            //返回Type对象
            return null;
        }
    }

}
