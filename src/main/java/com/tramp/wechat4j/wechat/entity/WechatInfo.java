/**
 *  
 *  CustomerLogQueryController.java 2016-12-22
 * <p/>
 * Copyright 2000-2016 by ChinanetCenter Corporation.
 * <p/>
 * All rights reserved.
 * <p/>
 * This software is the confidential and proprietary information of
 * ChinanetCenter Corporation ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with ChinanetCenter.
 */

package com.tramp.wechat4j.wechat.entity;

import com.tramp.wechat4j.wechat.client.WechatClient;

/**
 * @author chenjm1
 * @since 2017/6/23
 */
public class WechatInfo {
    private WechatClient wechatClient;

    public WechatClient getWechatClient() {
        return wechatClient;
    }

    public void setWechatClient(WechatClient wechatClient) {
        this.wechatClient = wechatClient;
    }
}
