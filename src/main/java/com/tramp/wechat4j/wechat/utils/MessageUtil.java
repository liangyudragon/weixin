package com.tramp.wechat4j.wechat.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.tramp.wechat4j.wechat.client.WechatClient;
import com.tramp.wechat4j.wechat.enums.URLEnum;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * 消息处理类
 *
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月23日 下午2:30:37
 * @version 1.0
 *
 */
public class MessageUtil {
	private static Logger LOG = LoggerFactory.getLogger(MessageUtil.class);

	/**
	 * 根据ID发送文本消息
	 *
	 * @author https://github.com/yaphone
	 * @date 2017年5月6日 上午11:45:51
	 * @param text
	 * @param id
	 */
	public static void sendMsgById(String text, String id,WechatClient wechatClient) {
		sendMsg(text, id, wechatClient);
	}

	/**
	 * 根据UserName发送文本消息
	 *
	 * @author https://github.com/yaphone
	 * @date 2017年5月4日 下午11:17:38
	 * @param toUserName
	 */
	private static void sendMsg(String text, String toUserName,WechatClient wechatClient) {
		LOG.info(String.format("发送消息 %s: %s", toUserName, text));
		webWxSendMsg(1, text, toUserName,wechatClient);
	}

	/**
	 * 消息发送
	 *
	 * @author https://github.com/yaphone
	 * @date 2017年4月23日 下午2:32:02
	 * @param msgType
	 * @param content
	 * @param toUserName
	 */
	public static void webWxSendMsg(int msgType, String content, String toUserName, WechatClient wechatClient) {
		String url = String.format(URLEnum.WEB_WX_SEND_MSG.getUrl(), wechatClient.getLoginInfo().get("url"));
		Map<String, Object> msgMap = Maps.newHashMap();
		msgMap.put("Type", msgType);
		msgMap.put("Content", content);
		msgMap.put("FromUserName", wechatClient.getUserName());
		msgMap.put("ToUserName", toUserName == null ? wechatClient.getUserName() : toUserName);
		msgMap.put("LocalID", new Date().getTime() * 10);
		msgMap.put("ClientMsgId", new Date().getTime() * 10);
		Map<String, Object> paramMap = wechatClient.getParamMap();
		paramMap.put("Msg", msgMap);
		paramMap.put("Scene", 0);
		try {
			String paramStr = JSON.toJSONString(paramMap);
			HttpEntity entity = wechatClient.getHttpClientUtil().doPost(url, paramStr);
			EntityUtils.toString(entity, Consts.UTF_8);
		} catch (Exception e) {
			LOG.error("webWxSendMsg", e);
		}
	}

}
