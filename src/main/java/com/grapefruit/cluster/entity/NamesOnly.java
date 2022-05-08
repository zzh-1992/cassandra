/*
 *Copyright @2021 Grapefruit. All rights reserved.
 */

package com.grapefruit.cluster.entity;

/**
 * Interface-based Projections(投影 want to retrieve the person’s address and name attributes only)
 *
 * @author zhihuangzhang
 * @version 1.0
 * @date 2022-05-08 12:39
 */
public interface NamesOnly {

    /**
     * 获取address
     *
     * @return address
     */
    String getAddress();

    /**
     * 获取name
     *
     * @return name
     */
    String getName();

    /**
     * 获取address + name
     *
     * @return str
     */
    default String getFullName() {
        return getAddress().concat(" ").concat(getName());
    }
}
