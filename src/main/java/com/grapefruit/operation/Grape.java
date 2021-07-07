/*
 *Copyright @2021 Grapefruit. All rights reserved.
 */

package com.grapefruit.operation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实体类
 *
 * @author zhihuangzhang
 * @version 1.0
 * @date 2021-07-07 5:19 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grape {
    int id;
    String address;
    int age;
    String name;
}
