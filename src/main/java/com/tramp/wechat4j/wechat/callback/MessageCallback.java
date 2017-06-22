package com.tramp.wechat4j.wechat.callback;

import com.tramp.wechat4j.wechat.entity.Message;

/**
 * 收到消息的回调
 */
public interface MessageCallback {

	/**
	 * 收到私聊消息后的回调
	 * @param message
	 */
	void onMessage(Message message);
}
