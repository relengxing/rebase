package com.relengxing.rebase.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 *  JacksonUtil
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JSONUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 忽略未知字段  忽略在 json字符串中存在但 Java 对象不存在的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //  序列化始终包含属性，与属性的值无关，默认
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        // 对象属性为空时，默认序列化会失败，关闭
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 设置解析能识别JSON字符串中的注释符号 \
        //objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER,true);
        // Json反序列化可以解析单引号包住的属性名称和字符串值
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,true);
        // 反序列化可以解析Json字符串中属性名没有双引号
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,true);
        // 设置时间格式化
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    @SneakyThrows
    public static<T> String toJSON(T data) {
            return objectMapper.writeValueAsString(data);
    }

    @SneakyThrows
    public static <T> T toBean(String jsonData, Class<T> beanType) {
            return objectMapper.readValue(jsonData, beanType);
    }

    @SneakyThrows
    public static <T> List<T> toList(String jsonData, Class<T> beanType) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, beanType);
        return objectMapper.readValue(jsonData, javaType);
    }

    @SneakyThrows
    public static <K, V> Map<K, V> toMap(String jsonData, Class<K> keyType, Class<V> valueType) {
        JavaType javaType = objectMapper.getTypeFactory().constructMapType(Map.class, keyType, valueType);
        return objectMapper.readValue(jsonData, javaType);
    }
}
