package com.otn.tool.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.util.Assert;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class JsonUtils {

    private final static Logger LOGGER = LogManager.getLogger();

    private final static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 允许注释
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        // 允许使用无引号属性名
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许单引号包住属性名和值
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 允许非引号控制字符
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        // null值不序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // 没有映射到的属性不抛出异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 日期类型属性处理
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(dateFormat);

    }

    private JsonUtils(){}

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static String toJson(Object obj){
        try {
            Assert.notNull(obj, "object cannot be null.");
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            LOGGER.error("fail to encode:[" + obj + "]", e);
        }
        return null;
    }

    public static <T> T toPojo(String jsonStr, Class<T> clazz) {
        try {
            Assert.hasText(jsonStr, "json string cannot be empty.");
            Assert.notNull(clazz, "clazz cannot be null.");
            return objectMapper.readValue(jsonStr, clazz);
        } catch (Exception e) {
            LOGGER.error("fail to decode json:" + jsonStr, e);
        }
        return null;
    }

    public static <T> T toPojo(String jsonStr, TypeReference<T> typeReference) {
        try {
            Assert.hasText(jsonStr, "json string cannot be empty.");
            Assert.notNull(typeReference, "typeReference cannot be null.");
            return objectMapper.readValue(jsonStr, typeReference);
        } catch (Exception e) {
            LOGGER.error("fail to decode json:" + jsonStr, e);
        }
        return null;
    }

    public static <T> T toPojo(String jsonStr, JavaType javaType) {
        try {
            Assert.hasText(jsonStr, "json string cannot be empty.");
            Assert.notNull(javaType, "javaType cannot be null.");
            return objectMapper.readValue(jsonStr, javaType);
        } catch (Exception e) {
            LOGGER.error("fail to decode json:" + jsonStr, e);
        }
        return null;
    }

    public static <T>List<T> toList(String jsonStr, Class<T> clazz) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, clazz);
        return toPojo(jsonStr, javaType);
    }

    public static Map<String, Object> toMap(String jsonStr) {
        return toPojo(jsonStr, Map.class);
    }

    public static <T> T map2pojo(Map map, Class<T> clazz) {
        try {
            Assert.notEmpty(map, "map  cannot be empty.");
            Assert.notNull(clazz, "clazz cannot be null.");
            return objectMapper.convertValue(map, clazz);
        }catch (Exception e) {
            LOGGER.error("fail to decode map:[" + map + "]", e);
        }
        return null;
    }

    public static <T> T readFile(File file, Class<T> clazz) {
        try {
            Assert.notNull(file, "file  cannot be NULL.");
            Assert.notNull(clazz, "clazz cannot be null.");
            return objectMapper.readValue(file, clazz);
        }catch (Exception e) {
            LOGGER.error("fail to read file :[" + file + "]", e);
        }
        return null;
    }

    public static <T> T readFile(InputStream inputStream, TypeReference<T> typeReference) {
        try {
            Assert.notNull(inputStream, "inputStream  cannot be NULL.");
            Assert.notNull(typeReference, "typeReference cannot be null.");
            return objectMapper.readValue(inputStream, typeReference);
        }catch (Exception e) {
            LOGGER.error("fail to read inputStream :[" + inputStream + "]", e);
        }
        return null;
    }

    public static <T> T readFile(InputStream inputStream, Class<T> clazz) {
        try {
            Assert.notNull(inputStream, "inputStream  cannot be NULL.");
            Assert.notNull(clazz, "clazz cannot be null.");
            return objectMapper.readValue(inputStream, clazz);
        }catch (Exception e) {
            LOGGER.error("fail to read inputStream :[" + inputStream + "]", e);
        }
        return null;
    }

    public static <T>List<T> readFileToList(InputStream inputStream, Class<T> clazz) {
        try {
            Assert.notNull(inputStream, "inputStream  cannot be NULL.");
            Assert.notNull(clazz, "clazz cannot be null.");
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, clazz);
            return objectMapper.readValue(inputStream, javaType);
        }catch (Exception e) {
            LOGGER.error("fail to read inputStream :[" + inputStream + "]", e);
        }
        return null;
    }

}
