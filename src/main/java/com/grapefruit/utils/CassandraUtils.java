/*
 *Copyright @2021 Grapefruit. All rights reserved.
 */

package com.grapefruit.utils;

import com.alibaba.fastjson.JSON;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Row;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 结果转换工具
 *
 * @author zhihuangzhang
 * @version 1.0
 * @date 2021-07-10 12:54 下午
 */
public class CassandraUtils {

    /**
     * Row-->java bean
     *
     * @param row    单条结果
     * @param list   结果集
     * @param tClass java对象的类型
     * @param <T>    java对象
     */
    public static <T> void rowToList(Row row, List<T> list, Class<T> tClass) {
        // 获取列的定义
        List<ColumnDefinitions.Definition> definitions = row.getColumnDefinitions().asList();

        Map<String, Object> map = new HashMap<>();
        for (ColumnDefinitions.Definition definition : definitions) {
            // 获取该列的名字
            String name = definition.getName();
            // 获取该列的类型
            DataType type = definition.getType();
            if (type.getName().isCompatibleWith(DataType.Name.INT)) {
                int i = row.getInt(name);
                // 判断类型并获取值
                map.put(name, i);
                continue;
            }
            if (type.getName().isCompatibleWith(DataType.Name.VARCHAR)) {
                String s = row.getString(name);
                map.put(name, s);
            }
        }
        T t = JSON.parseObject(JSON.toJSONString(map), tClass);
        list.add(t);
    }

    /**
     * 将结果集转化为java对象
     *
     * @param list 元素数据
     * @return list java对象集合
     */
    public static <T> List<T> toJavaBean(List<Map<String, Object>> list, Class<T> tClass) {
        List<T> personList = new ArrayList<>();
        list.forEach(row -> {
            String jsonString = JSON.toJSONString(row);
            personList.add(JSON.parseObject(jsonString, tClass));
        });
        return personList;
    }
}
