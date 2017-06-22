package com.tramp.wechat4j.controller;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import com.tramp.wechat4j.wechat.callback.MessageCallback;
import com.tramp.wechat4j.wechat.client.WechatClient;
import com.tramp.wechat4j.wechat.entity.Message;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author chenjm1
 * @since 2017/6/22
 */
@Controller
@RequestMapping("/wechat/*")
public class WechatLoginController extends BaseController {

    @RequestMapping(value = "login.do")
    @ResponseBody
    public Map login(HttpServletRequest request) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("code", 201);

        WechatClient wechatClient = new WechatClient(new MessageCallback() {
            public void onMessage(Message message) {
                //消息处理

            }
        });

        return result;
    }

}
