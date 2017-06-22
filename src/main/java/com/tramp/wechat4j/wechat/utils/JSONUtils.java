/* 
 * JSONutils.java  2016-10-12
 *
 * Copyright 2000-2016 by ChinanetCenter Corporation.
 *
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ChinanetCenter Corporation ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with ChinanetCenter.
 *
*/

package com.tramp.wechat4j.wechat.utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JavaType;

/**
 *  类的描述信息 
 *
 * @author tangwz
 * @since 2016-10-12
 */
public final class JSONUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSONUtils.class);

    private static final JsonMapper JSON_MAPPER = JsonMapper.nonEmptyMapper();

    private JSONUtils() {
    }

    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        return JSON_MAPPER.fromJson(jsonString, clazz);
    }

    public static <T> List<T> fromJsonList(String jsonString, Class<T> clazz) {
        JavaType javaType = JsonMapper.nonEmptyMapper().contructCollectionType(List.class, clazz);
        return JSON_MAPPER.fromJson(jsonString, javaType);
    }

    public static String toJson(Object object) {
        if (null == object) {
            return null;
        }
        return JSON_MAPPER.toJson(object);
    }

    public static Map fromJson(String jsonString, Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        JavaType javaType = JSON_MAPPER.contructMapType(mapClass, keyClass, valueClass);
        return JSON_MAPPER.fromJson(jsonString, javaType);
    }

    /**
     * 根据JSON创建带范型的对象
     *
     * @param jsonString
     * @param parametrized
     * @param parameterClasses
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String jsonString, Class<?> parametrized, Class... parameterClasses) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }

        try {
            JavaType javaType = JSON_MAPPER.getMapper().getTypeFactory().constructParametricType(parametrized, parameterClasses);
            return JSON_MAPPER.getMapper().readValue(jsonString, javaType);
        } catch (IOException e) {
            LOGGER.warn("parse json string error, param:{}", jsonString, e);
            return null;
        }
    }
}
