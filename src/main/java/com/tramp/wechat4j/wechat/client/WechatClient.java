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

package com.tramp.wechat4j.wechat.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Matcher;

import com.sun.tools.corba.se.idl.som.cff.Messages;
import com.tramp.wechat4j.wechat.entity.Message;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.tramp.wechat4j.wechat.callback.MessageCallback;
import com.tramp.wechat4j.wechat.enums.*;
import com.tramp.wechat4j.wechat.utils.CommonTools;
import com.tramp.wechat4j.wechat.utils.HttpClientUtil;
import com.tramp.wechat4j.wechat.utils.SleepUtils;

/**
 * @author chenjm1
 * @since 2017/6/22
 */
public class WechatClient {
	private static final Logger LOG = LoggerFactory.getLogger(WechatClient.class);
	private HttpClientUtil httpClientUtil = new HttpClientUtil();

	private String qrPath = "C:\\Users\\chenjm1\\Desktop";
	boolean alive = false;
	private int memberCount = 0;
	private String userName;
	private String nickName;
	private List<JSONObject> msgList = new ArrayList<JSONObject>();
	private JSONObject userSelf; // 登陆账号自身信息
	private List<JSONObject> memberList = new ArrayList<JSONObject>(); // 好友+群聊+公众号+特殊账号
	private List<JSONObject> contactList = new ArrayList<JSONObject>();;// 好友
	private List<JSONObject> groupList = new ArrayList<JSONObject>();; // 群
	private List<JSONObject> groupMemeberList = new ArrayList<JSONObject>();; // 群聊成员字典
	private List<JSONObject> publicUsersList = new ArrayList<JSONObject>();;// 公众号／服务号
	private List<JSONObject> specialUsersList = new ArrayList<JSONObject>();;// 特殊账号
	private List<String> groupIdList = new ArrayList<String>();
	private Map<String, JSONObject> userInfoMap = new HashMap<String, JSONObject>();
	Map<String, Object> loginInfo = new HashMap<String, Object>();
	String uuid = null;
	boolean useHotReload = false;
	String hotReloadDir = "itchat.pkl";
	int receivingRetryCount = 5;
	private long lastNormalRetcodeTime; // 最后一次收到正常retcode的时间，秒为单位

	public WechatClient(final MessageCallback callback) {
		System.setProperty("jsse.enableSNIExtension", "false"); // 防止SSL错误
		login();
		if (callback != null) {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
                        if(alive){
                            //callback.onMessage("11111");
                        }
					}
				}
			}).start();
		}
	}

	/**
	 * 登录
	 */
	private void login() {
		for (int count = 0; count < 10; count++) {
			LOG.info("获取UUID");
			while (StringUtils.isEmpty(uuid)) {
				LOG.info("1. 获取微信UUID");
				while (StringUtils.isEmpty(getUuid())) {
					LOG.warn("1.1. 获取微信UUID失败，两秒后重新获取");
					SleepUtils.sleep(2000);
				}
			}
			LOG.info("2. 获取登陆二维码图片");
			if (this.getQR()) {
				LOG.info("3. 请扫描二维码图片，并在手机上确认");
				if (!this.alive) {
					wLogin();
					alive = true;
					LOG.info(("登陆成功"));
					break;
				}
				LOG.info("4. 登陆超时，请重新扫描二维码图片");
				break;
			}
			else if (count == 10) {
				LOG.error("2.2. 获取登陆二维码图片失败");
				break;
			}
		}

	}

	/**
	 * 第一步：uuid
	 * @return
	 */
	private String getUuid() {
		// 组装参数和URL
		List<BasicNameValuePair> params = Lists.newArrayList();
		params.add(new BasicNameValuePair(UUIDParaEnum.APP_ID.para(), UUIDParaEnum.APP_ID.value()));
		params.add(new BasicNameValuePair(UUIDParaEnum.FUN.para(), UUIDParaEnum.FUN.value()));
		params.add(new BasicNameValuePair(UUIDParaEnum.LANG.para(), UUIDParaEnum.LANG.value()));
		params.add(new BasicNameValuePair(UUIDParaEnum._.para(), String.valueOf(System.currentTimeMillis())));

		HttpEntity entity = httpClientUtil.doGet(URLEnum.UUID_URL.getUrl(), params, true, null);

		try {
			String result = EntityUtils.toString(entity);
			String regEx = "window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\";";
			Matcher matcher = CommonTools.getMatcher(regEx, result);
			if (matcher.find()) {
				if ((ResultEnum.SUCCESS.getCode().equals(matcher.group(1)))) {
					this.uuid = matcher.group(2);
				}
			}
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return this.uuid;
	}

	/**
	 * 第二部:qr
	 * @return
	 */
	public boolean getQR() {
		qrPath = qrPath + File.separator + "QR.jpg";
		String qrUrl = URLEnum.QRCODE_URL.getUrl() + this.uuid;
		HttpEntity entity = httpClientUtil.doGet(qrUrl, null, true, null);
		try {
			OutputStream out = new FileOutputStream(qrPath);
			byte[] bytes = EntityUtils.toByteArray(entity);
			out.write(bytes);
			out.flush();
			out.close();
			try {
				CommonTools.printQr(qrPath); // 打开登陆二维码图片
			}
			catch (Exception e) {
				LOG.info(e.getMessage());
			}

		}
		catch (Exception e) {
			LOG.info(e.getMessage());
			return false;
		}

		return true;
	}

	/**
	 * 登录
	 * @return
	 */
	public boolean wLogin() {

		boolean isLogin = false;
		// 组装参数和URL
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(LoginParaEnum.LOGIN_ICON.para(), LoginParaEnum.LOGIN_ICON.value()));
		params.add(new BasicNameValuePair(LoginParaEnum.UUID.para(), this.uuid));
		params.add(new BasicNameValuePair(LoginParaEnum.TIP.para(), LoginParaEnum.TIP.value()));

		// long time = 4000;
		while (!isLogin) {
			// SleepUtils.sleep(time += 1000);
			long millis = System.currentTimeMillis();
			params.add(new BasicNameValuePair(LoginParaEnum.R.para(), String.valueOf(millis / 1579L)));
			params.add(new BasicNameValuePair(LoginParaEnum._.para(), String.valueOf(millis)));
			HttpEntity entity = httpClientUtil.doGet(URLEnum.LOGIN_URL.getUrl(), params, true, null);

			try {
				String result = EntityUtils.toString(entity);
				String status = checklogin(result);

				if (ResultEnum.SUCCESS.getCode().equals(status)) {
					processLoginInfo(result); // 处理结果
					isLogin = true;
					this.alive = isLogin;
					break;
				}
				if (ResultEnum.WAIT_CONFIRM.getCode().equals(status)) {
					LOG.info("请点击微信确认按钮，进行登陆");
				}

			}
			catch (Exception e) {
				LOG.error("微信登陆异常！", e);
			}
		}
		return isLogin;
	}

	/**
	 * 处理登陆信息
	 *
	 * @author https://github.com/yaphone
	 * @date 2017年4月9日 下午12:16:26
	 */
	private void processLoginInfo(String loginContent) {
		String regEx = "window.redirect_uri=\"(\\S+)\";";
		Matcher matcher = CommonTools.getMatcher(regEx, loginContent);
		if (matcher.find()) {
			String originalUrl = matcher.group(1);
			String url = originalUrl.substring(0, originalUrl.lastIndexOf('/')); // https://wx2.qq.com/cgi-bin/mmwebwx-bin
			this.loginInfo.put("url", url);
			Map<String, List<String>> possibleUrlMap = this.getPossibleUrlMap();
			Iterator<Map.Entry<String, List<String>>> iterator = possibleUrlMap.entrySet().iterator();
			Map.Entry<String, List<String>> entry;
			String fileUrl;
			String syncUrl;
			while (iterator.hasNext()) {
				entry = iterator.next();
				String indexUrl = entry.getKey();
				fileUrl = "https://" + entry.getValue().get(0) + "/cgi-bin/mmwebwx-bin";
				syncUrl = "https://" + entry.getValue().get(1) + "/cgi-bin/mmwebwx-bin";
				if (this.loginInfo.get("url").toString().contains(indexUrl)) {
					this.loginInfo.put("fileUrl", fileUrl);
					this.loginInfo.put("syncUrl", syncUrl);
					break;
				}
			}
			if (this.loginInfo.get("fileUrl") == null && this.loginInfo.get("syncUrl") == null) {
				this.loginInfo.put("fileUrl", url);
				this.loginInfo.put("syncUrl", url);
			}
			this.loginInfo.put("deviceid", "e" + String.valueOf(new Random().nextLong()).substring(1, 16)); // 生成15位随机数
			this.loginInfo.put("BaseRequest", new ArrayList<String>());
			String text = "";

			try {
				HttpEntity entity = httpClientUtil.doGet(originalUrl, null, false, null);
				text = EntityUtils.toString(entity);
			}
			catch (Exception e) {
				LOG.info(e.getMessage());
				return;
			}
			Document doc = CommonTools.xmlParser(text);
			if (doc != null) {
				this.loginInfo.put(StorageLoginInfoEnum.skey.getKey(),
						doc.getElementsByTagName(StorageLoginInfoEnum.skey.getKey()).item(0).getFirstChild().getNodeValue());
				this.loginInfo.put(StorageLoginInfoEnum.wxsid.getKey(),
						doc.getElementsByTagName(StorageLoginInfoEnum.wxsid.getKey()).item(0).getFirstChild().getNodeValue());
				this.loginInfo.put(StorageLoginInfoEnum.wxuin.getKey(),
						doc.getElementsByTagName(StorageLoginInfoEnum.wxuin.getKey()).item(0).getFirstChild().getNodeValue());
				this.loginInfo.put(StorageLoginInfoEnum.pass_ticket.getKey(),
						doc.getElementsByTagName(StorageLoginInfoEnum.pass_ticket.getKey()).item(0).getFirstChild().getNodeValue());
			}

		}
	}

	/**
	 * 检查登陆状态
	 *
	 * @param result
	 * @return
	 */
	public String checklogin(String result) {
		String regEx = "window.code=(\\d+)";
		Matcher matcher = CommonTools.getMatcher(regEx, result);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	private Map<String, List<String>> getPossibleUrlMap() {
		Map<String, List<String>> possibleUrlMap = new HashMap<String, List<String>>();
		possibleUrlMap.put("wx.qq.com", new ArrayList<String>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			{
				add("file.wx.qq.com");
				add("webpush.wx.qq.com");
			}
		});

		possibleUrlMap.put("wx2.qq.com", new ArrayList<String>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			{
				add("file.wx2.qq.com");
				add("webpush.wx2.qq.com");
			}
		});
		possibleUrlMap.put("wx8.qq.com", new ArrayList<String>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			{
				add("file.wx8.qq.com");
				add("webpush.wx8.qq.com");
			}
		});

		possibleUrlMap.put("web2.wechat.com", new ArrayList<String>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			{
				add("file.web2.wechat.com");
				add("webpush.web2.wechat.com");
			}
		});
		possibleUrlMap.put("wechat.com", new ArrayList<String>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			{
				add("file.web.wechat.com");
				add("webpush.web.wechat.com");
			}
		});
		return possibleUrlMap;
	}

    public String getQrPath() {
        return qrPath;
    }

    public void setQrPath(String qrPath) {
        this.qrPath = qrPath;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public JSONObject getUserSelf() {
        return userSelf;
    }

    public void setUserSelf(JSONObject userSelf) {
        this.userSelf = userSelf;
    }

    public List<JSONObject> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<JSONObject> memberList) {
        this.memberList = memberList;
    }

    public List<JSONObject> getContactList() {
        return contactList;
    }

    public void setContactList(List<JSONObject> contactList) {
        this.contactList = contactList;
    }

    public List<JSONObject> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<JSONObject> groupList) {
        this.groupList = groupList;
    }

    public List<JSONObject> getGroupMemeberList() {
        return groupMemeberList;
    }

    public void setGroupMemeberList(List<JSONObject> groupMemeberList) {
        this.groupMemeberList = groupMemeberList;
    }

    public List<JSONObject> getPublicUsersList() {
        return publicUsersList;
    }

    public void setPublicUsersList(List<JSONObject> publicUsersList) {
        this.publicUsersList = publicUsersList;
    }

    public List<JSONObject> getSpecialUsersList() {
        return specialUsersList;
    }

    public void setSpecialUsersList(List<JSONObject> specialUsersList) {
        this.specialUsersList = specialUsersList;
    }

    public List<String> getGroupIdList() {
        return groupIdList;
    }

    public void setGroupIdList(List<String> groupIdList) {
        this.groupIdList = groupIdList;
    }

    public Map<String, JSONObject> getUserInfoMap() {
        return userInfoMap;
    }

    public void setUserInfoMap(Map<String, JSONObject> userInfoMap) {
        this.userInfoMap = userInfoMap;
    }

    public Map<String, Object> getLoginInfo() {
        return loginInfo;
    }

    public void setLoginInfo(Map<String, Object> loginInfo) {
        this.loginInfo = loginInfo;
    }

    public static void main(String[] args) {
        WechatClient client = new WechatClient(new MessageCallback() {
            public void onMessage(String message) {
                System.out.println(message);
            }
        });
    }
}
