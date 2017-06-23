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

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tramp.wechat4j.wechat.bot.TuringQueryService;
import com.tramp.wechat4j.wechat.callback.MessageCallback;
import com.tramp.wechat4j.wechat.contants.Contants;
import com.tramp.wechat4j.wechat.entity.*;
import com.tramp.wechat4j.wechat.enums.*;
import com.tramp.wechat4j.wechat.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @author chenjm1
 * @since 2017/6/22
 */
public class WechatClient {
    private static final Logger LOG = LoggerFactory.getLogger(WechatClient.class);
    private HttpClientUtil httpClientUtil = new HttpClientUtil();
    private MessageCallback messageCallback;
    //private String qrPath = "C:\\Users\\chenjm1\\Desktop";
    boolean alive = false;
    private int memberCount = 0;
    private String userName;
    private String nickName;
    private List<JSONObject> msgList = new ArrayList<JSONObject>();
    private JSONObject userSelf; // 登陆账号自身信息
    private List<JSONObject> memberList = new ArrayList<JSONObject>(); // 好友+群聊+公众号+特殊账号
    private List<Friend> friendList = Lists.newArrayList();//好友
    ;// 好友
    private List<Group> groupList = Lists.newArrayList();
    ; // 群
    private List<JSONObject> groupMemeberList = new ArrayList<JSONObject>();
    ; // 群聊成员字典
    private List<PublicUser> publicUsersList = Lists.newArrayList()
    ;// 公众号／服务号
    private List<SpecialUser> specialUsersList = Lists.newArrayList()
    ;// 特殊账号
    private List<String> groupIdList = new ArrayList<String>();
    private Map<String, Friend> userInfoMap = new HashMap<String, Friend>();
    Map<String, Object> loginInfo = new HashMap<String, Object>();
    String uuid = null;
    boolean useHotReload = false;
    String hotReloadDir = "itchat.pkl";
    int receivingRetryCount = 5;
    private long lastNormalRetcodeTime; // 最后一次收到正常retcode的时间，秒为单位

    public WechatClient() {
        System.setProperty("jsse.enableSNIExtension", "false"); // 防止SSL错误
    }

    public WechatClient(final MessageCallback callback) {
        if (callback != null) {
            System.setProperty("jsse.enableSNIExtension", "false"); // 防止SSL错误
            this.messageCallback = callback;
            login();
            /*new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (alive) {
							callback.onMessage(null);
						}
					}
				}
			}).start();*/
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
            if (this.getQR(Contants.QRPATH,"QR")) {
                LOG.info("3. 请扫描二维码图片，并在手机上确认");
                if (!this.alive) {
                    if (wLogin()) {
                        alive = true;
                        LOG.info("5. 登陆成功，微信初始化");
                        if (!LoginUtil.webWxInit(this)) {
                            LOG.info("6. 微信初始化异常");
                            //System.exit(0);
                        }

                        LOG.info("6.开启微信状态检测线程");
                        //new Thread(new CheckLoginStatusThread()).start();

                        LOG.info("7. 开启微信状态通知");
                        LoginUtil.wxStatusNotify(this);

                        LOG.info("8. 清除。。。。");
                        CommonTools.clearScreen();
                        LOG.info(String.format("欢迎回来， %s", this.getNickName()));

                        LOG.info("9. 开始接收消息");
                        LoginUtil.startReceiving(this);

                        LOG.info("10. 获取联系人信息");
                        LoginUtil.webWxGetContact(this);

                        LOG.info("11. 缓存本次登陆好友相关消息");
                        for (Friend friend : this.getFriendList()) {
                            this.getUserInfoMap().put(friend.getNickName(), friend);
                            this.getUserInfoMap().put(friend.getUserName(), friend);
                        } // 登陆成功后缓存本次登陆好友相关消息（NickName, UserName）

                        break;
                    }
                }
                LOG.info("4. 登陆超时，请重新扫描二维码图片");
                break;
            } else if (count == 10) {
                LOG.error("2.2. 获取登陆二维码图片失败");
                break;
            }
        }

    }

    /**
     * 第一步：uuid
     *
     * @return
     */
    public String getUuid() {
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
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return this.uuid;
    }

    /**
     * 第二部:qr
     *
     * @return
     */
    public boolean getQR(String path,String qrName) {
        String qrPath = path+ File.separator + qrName;
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
            } catch (Exception e) {
                LOG.info(e.getMessage());
            }

        } catch (Exception e) {
            LOG.info(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * 登录
     *
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

            } catch (Exception e) {
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
    public void processLoginInfo(String loginContent) {
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
            } catch (Exception e) {
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

    /**
     * 请求参数
     */
    public Map<String, Object> getParamMap() {
        return new HashMap<String, Object>(1) {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            {
                Map<String, String> map = new HashMap<String, String>();
                for (BaseParaEnum baseRequest : BaseParaEnum.values()) {
                    map.put(baseRequest.para(), getLoginInfo().get(baseRequest.value()).toString());
                }
                put("BaseRequest", map);
            }
        };
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

    public List<JSONObject> getGroupMemeberList() {
        return groupMemeberList;
    }

    public void setGroupMemeberList(List<JSONObject> groupMemeberList) {
        this.groupMemeberList = groupMemeberList;
    }

    public List<PublicUser> getPublicUsersList() {
        return publicUsersList;
    }

    public void setPublicUsersList(List<PublicUser> publicUsersList) {
        this.publicUsersList = publicUsersList;
    }


    public List<String> getGroupIdList() {
        return groupIdList;
    }

    public void setGroupIdList(List<String> groupIdList) {
        this.groupIdList = groupIdList;
    }

    public Map<String, Friend> getUserInfoMap() {
        return userInfoMap;
    }

    public void setUserInfoMap(Map<String, Friend> userInfoMap) {
        this.userInfoMap = userInfoMap;
    }

    public Map<String, Object> getLoginInfo() {
        return loginInfo;
    }

    public void setLoginInfo(Map<String, Object> loginInfo) {
        this.loginInfo = loginInfo;
    }

    public List<JSONObject> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<JSONObject> msgList) {
        this.msgList = msgList;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isUseHotReload() {
        return useHotReload;
    }

    public void setUseHotReload(boolean useHotReload) {
        this.useHotReload = useHotReload;
    }

    public String getHotReloadDir() {
        return hotReloadDir;
    }

    public void setHotReloadDir(String hotReloadDir) {
        this.hotReloadDir = hotReloadDir;
    }

    public int getReceivingRetryCount() {
        return receivingRetryCount;
    }

    public void setReceivingRetryCount(int receivingRetryCount) {
        this.receivingRetryCount = receivingRetryCount;
    }

    public long getLastNormalRetcodeTime() {
        return lastNormalRetcodeTime;
    }

    public void setLastNormalRetcodeTime(long lastNormalRetcodeTime) {
        this.lastNormalRetcodeTime = lastNormalRetcodeTime;
    }

    public List<Friend> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<Friend> friendList) {
        this.friendList = friendList;
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    public List<SpecialUser> getSpecialUsersList() {
        return specialUsersList;
    }

    public void setSpecialUsersList(List<SpecialUser> specialUsersList) {
        this.specialUsersList = specialUsersList;
    }

    public HttpClientUtil getHttpClientUtil() {
        return httpClientUtil;
    }

    public void setHttpClientUtil(HttpClientUtil httpClientUtil) {
        this.httpClientUtil = httpClientUtil;
    }

    public MessageCallback getMessageCallback() {
        return messageCallback;
    }

    public void setMessageCallback(MessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    public String getUid(){
        return this.uuid;
    }
    public static void main(String[] args) {
       final Map<String,WechatClient> clientMap = Maps.newHashMap();

        WechatClient client = new WechatClient(new MessageCallback() {
            //好友消息回调
            public void onFriendMessage(Message message) {
                LOG.info("收到一条好友消息，来自: " + message.getFromUserName());
                System.out.println(message.getContent());
                TuringQueryService turingQueryService = new TuringQueryService();
                MessageUtil.sendMsgById(turingQueryService.chat("350784", message.getContent()),message.getFromUserName(), clientMap.get("wechatClient"));
            }
            //群消息回调
            public void onGroupMessage(Message message) {
                LOG.info("收到一条群消息，来自: " + message.getFromUserName());
                System.out.println(message.getContent());
                TuringQueryService turingQueryService = new TuringQueryService();
                String text=message.getContent();
                if(StringUtils.isNotBlank(text) && text.contains("小明")){
                    if (message.getContent().startsWith("@小明" + " ")) {
                        text = text.replace("@小明" + " ", "");
                    }
                    if (text.startsWith("@小明" + "，")) {
                        text = text.replace("@小明" + "，", "");
                    }
                    if (text.startsWith("@小明" + ",")) {
                        text = text.replace("@小明" + ",", "");
                    }
                    if (text.startsWith("@小明")) {
                        text = text.replace("@小明", "");
                    }
                    MessageUtil.sendMsgById(turingQueryService.chat("350784", text),message.getFromUserName(), clientMap.get("wechatClient"));
                }
            }
        });
        clientMap.put("wechatClient", client);
        List<Friend> friendList = client.getFriendList();
        LOG.info("-----------好友--------------");
        for (Friend friend : friendList) {
            LOG.info(friend.getNickName());
        }
        LOG.info("-----------群--------------");

        List<Group> groupList = client.getGroupList();
        for (Group group : groupList) {
            LOG.info(group.getNickName());
        }
        LOG.info("-----------公众号--------------");
        List<PublicUser> publicUsersList = client.getPublicUsersList();
        for (PublicUser publicUser : publicUsersList) {
            LOG.info(publicUser.getNickName());

        }


    }
}
