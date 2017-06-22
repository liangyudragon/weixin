package com.tramp.wechat4j.wechat.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tramp.wechat4j.wechat.Config;
import com.tramp.wechat4j.wechat.client.WechatClient;
import com.tramp.wechat4j.wechat.entity.*;
import com.tramp.wechat4j.wechat.enums.*;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;

/**
 * @author chenjm1
 * @since 2017/6/22
 */
public class LoginUtil {
    private static final Logger LOG = LoggerFactory.getLogger(LoginUtil.class);

    public static boolean webWxInit(WechatClient wechatClient) {
        wechatClient.setAlive(true);
        wechatClient.setLastNormalRetcodeTime(System.currentTimeMillis());
        // 组装请求URL和参数
        String url = String.format(URLEnum.INIT_URL.getUrl(),
                wechatClient.getLoginInfo().get(StorageLoginInfoEnum.url.getKey()),
                String.valueOf(System.currentTimeMillis() / 3158L),
                wechatClient.getLoginInfo().get(StorageLoginInfoEnum.pass_ticket.getKey()));

        Map<String, Object> paramMap = wechatClient.getParamMap();

        // 请求初始化接口
        HttpEntity entity = wechatClient.getHttpClientUtil().doPost(url, JSON.toJSONString(paramMap));
        try {
            String result = EntityUtils.toString(entity, Consts.UTF_8);
            JSONObject obj = JSON.parseObject(result);

            JSONObject user = obj.getJSONObject(StorageLoginInfoEnum.User.getKey());
            JSONObject syncKey = obj.getJSONObject(StorageLoginInfoEnum.SyncKey.getKey());

            wechatClient.getLoginInfo().put(StorageLoginInfoEnum.InviteStartCount.getKey(),
                    obj.getInteger(StorageLoginInfoEnum.InviteStartCount.getKey()));
            wechatClient.getLoginInfo().put(StorageLoginInfoEnum.SyncKey.getKey(), syncKey);

            JSONArray syncArray = syncKey.getJSONArray("List");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < syncArray.size(); i++) {
                sb.append(syncArray.getJSONObject(i).getString("Key") + "_"
                        + syncArray.getJSONObject(i).getString("Val") + "|");
            }
            // 1_661706053|2_661706420|3_661706415|1000_1494151022|
            String synckey = sb.toString();

            // 1_661706053|2_661706420|3_661706415|1000_1494151022
            wechatClient.getLoginInfo().put(StorageLoginInfoEnum.synckey.getKey(), synckey.substring(0, synckey.length() - 1));// 1_656161336|2_656161626|3_656161313|11_656159955|13_656120033|201_1492273724|1000_1492265953|1001_1492250432|1004_1491805192
            wechatClient.setUserName(user.getString("UserName"));
            wechatClient.setNickName(user.getString("NickName"));
            wechatClient.setUserSelf(obj.getJSONObject("User"));

            JSONArray contactListArray = obj.getJSONArray("ContactList");
            for (int i = 0; i < contactListArray.size(); i++) {
                JSONObject o = contactListArray.getJSONObject(i);
                if (o.getString("UserName").indexOf("@@") != -1) {
                    wechatClient.getGroupIdList().add(o.getString("UserName")); // 更新GroupIdList
                    wechatClient.getGroupList().add(JSON.parseObject(JSONObject.toJSONString(o), Group.class)); // 更新GroupList
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void wxStatusNotify(WechatClient wechatClient) {
        // 组装请求URL和参数
        String url = String.format(URLEnum.STATUS_NOTIFY_URL.getUrl(),
                wechatClient.getLoginInfo().get(StorageLoginInfoEnum.pass_ticket.getKey()));

        Map<String, Object> paramMap = wechatClient.getParamMap();
        paramMap.put(StatusNotifyParaEnum.CODE.para(), StatusNotifyParaEnum.CODE.value());
        paramMap.put(StatusNotifyParaEnum.FROM_USERNAME.para(), wechatClient.getUserName());
        paramMap.put(StatusNotifyParaEnum.TO_USERNAME.para(), wechatClient.getUserName());
        paramMap.put(StatusNotifyParaEnum.CLIENT_MSG_ID.para(), System.currentTimeMillis());
        String paramStr = JSON.toJSONString(paramMap);

        try {
            HttpEntity entity = wechatClient.getHttpClientUtil().doPost(url, paramStr);
            EntityUtils.toString(entity, Consts.UTF_8);
        } catch (Exception e) {
            LOG.error("微信状态通知接口失败！", e);
        }

    }

    public static void startReceiving(final WechatClient wechatClient) {
        wechatClient.setAlive(true);
        new Thread(new Runnable() {
            int retryCount = 0;

            public void run() {
                while (wechatClient.isAlive()) {
                    try {
                        SleepUtils.sleep(500);
                        Map<String, String> resultMap = syncCheck(wechatClient);
                        LOG.info(JSONObject.toJSONString(resultMap));
                        String retcode = resultMap.get("retcode");
                        String selector = resultMap.get("selector");
                        if (retcode.equals(RetCodeEnum.UNKOWN.getCode())) {
                            LOG.info(RetCodeEnum.UNKOWN.getType());
                            continue;
                        } else if (retcode.equals(RetCodeEnum.LOGIN_OUT.getCode())) { // 退出
                            LOG.info(RetCodeEnum.LOGIN_OUT.getType());
                            break;
                        } else if (retcode.equals(RetCodeEnum.LOGIN_OTHERWHERE.getCode())) { // 其它地方登陆
                            LOG.info(RetCodeEnum.LOGIN_OTHERWHERE.getType());
                            break;
                        } else if (retcode.equals(RetCodeEnum.MOBILE_LOGIN_OUT.getCode())) { // 移动端退出
                            LOG.info(RetCodeEnum.MOBILE_LOGIN_OUT.getType());
                            break;
                        } else if (retcode.equals(RetCodeEnum.NORMAL.getCode())) {
                            wechatClient.setLastNormalRetcodeTime(System.currentTimeMillis()); // 最后收到正常报文时间
                            JSONObject msgObj = webWxSync(wechatClient);
                            if (selector.equals("2")) {
                                if (msgObj != null) {
                                    try {
                                        JSONArray msgList = msgObj.getJSONArray("AddMsgList");
                                        for (Object o : msgList) {
                                            Message message = JSON.parseObject(JSONObject.toJSONString(o), Message.class);
                                            msgTypeDone(wechatClient, message);
                                        }
                                    } catch (Exception e) {
                                        LOG.info(e.getMessage());
                                    }
                                }
                            } else if (selector.equals("7")) {
                                webWxSync(wechatClient);
                            } else if (selector.equals("4")) {
                                continue;
                            } else if (selector.equals("3") || selector.equals("6")) {
                                continue;
                            }
                        } else {
                            JSONObject obj = webWxSync(wechatClient);
                        }
                    } catch (Exception e) {
                        LOG.info(e.getMessage());
                        retryCount += 1;
                        if (wechatClient.getReceivingRetryCount() < retryCount) {
                            wechatClient.setAlive(false);
                        } else {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e1) {
                                LOG.info(e.getMessage());
                            }
                        }
                    }

                }
            }
        }).start();

    }

    /**
     * 消息分类处理
     *
     * @param wechatClient
     * @param message
     */
    private static void msgTypeDone(WechatClient wechatClient, Message message) {

        message.setGroupMsg(false);
        if (message.getFromUserName().contains("@@") || message.getToUserName().contains("@@")) { // 群聊消息
            if (message.getFromUserName().contains("@@")
                    && !wechatClient.getGroupIdList().contains(message.getFromUserName())) {
                wechatClient.getGroupIdList().add((message.getFromUserName()));
            } else if (message.getToUserName().contains("@@")
                    && !wechatClient.getGroupIdList().contains(message.getToUserName())) {
                wechatClient.getGroupIdList().add((message.getToUserName()));
            }
            // 群消息与普通消息不同的是在其消息体（Content）中会包含发送者id及":<br/>"消息，这里需要处理一下，去掉多余信息，只保留消息内容
            if (message.getContent().contains("<br/>")) {
                String content = message.getContent().substring(message.getContent().indexOf("<br/>") + 5);
                message.setContent(content);
                message.setGroupMsg(true);
            }
        } else {
            // CommonTools.msgFormatter(m, "Content");
        }
        if (message.getMsgType().equals(MsgCodeEnum.MSGTYPE_TEXT.getCode())) { // words
            message.setType(MsgCodeEnum.MSGTYPE_TEXT.getType());
            if (message.getUrl().length() != 0) {
                String regEx = "(.+?\\(.+?\\))";
                Matcher matcher = CommonTools.getMatcher(regEx, message.getContent());
                String data = "Map";
                if (matcher.find()) {
                    data = matcher.group(1);
                }
            } else {
                message.setContent(message.getContent());
            }
        } else if (message.getMsgType().equals(MsgCodeEnum.MSGTYPE_IMAGE.getCode())
                || message.getMsgType().equals(MsgCodeEnum.MSGTYPE_EMOTICON.getCode())) { // 图片消息
            message.setType(MsgCodeEnum.MSGTYPE_IMAGE.getType());
        } else if (message.getMsgType().equals(MsgCodeEnum.MSGTYPE_VOICE.getCode())) { // 语音消息
            message.setType(MsgCodeEnum.MSGTYPE_IMAGE.getType());

        } else if (message.getMsgType().equals(MsgCodeEnum.MSGTYPE_VERIFYMSG.getCode())) {// friends
            // 好友确认消息

        } else if (message.getMsgType().equals(MsgCodeEnum.MSGTYPE_SHARECARD.getCode())) { // 共享名片
            message.setType(MsgCodeEnum.MSGTYPE_SHARECARD.getType());
        } else if (message.getMsgType().equals(MsgCodeEnum.MSGTYPE_VIDEO.getCode())
                || message.getMsgType().equals(MsgCodeEnum.MSGTYPE_MICROVIDEO.getCode())) {// viedo
            message.setType(MsgCodeEnum.MSGTYPE_VIDEO.getType());
        } else if (message.getMsgType().equals(MsgCodeEnum.MSGTYPE_APP.getCode())) { // sharing
            // 分享链接

        } else if (message.getMsgType().equals(MsgCodeEnum.MSGTYPE_STATUSNOTIFY.getCode())) {// phone
            // init
            // 微信初始化消息

        } else if (message.getMsgType().equals(MsgCodeEnum.MSGTYPE_SYS.getCode())) {// 系统消息

        } else if (message.getMsgType().equals(MsgCodeEnum.MSGTYPE_RECALLED.getCode())) { // 撤回消息

        } else {
            LOG.info("Useless msg");
        }
        if (message.getGroupMsg()) {
            wechatClient.getMessageCallback().onGroupMessage(message);
        } else {
            wechatClient.getMessageCallback().onFriendMessage(message);

        }
    }

    /**
     * 检查是否有新消息 check whether there's a message
     *
     * @return
     * @author https://github.com/yaphone
     * @date 2017年4月16日 上午11:11:34
     */
    private static Map<String, String> syncCheck(WechatClient wechatClient) {
        Map<String, String> resultMap = new HashMap<String, String>();
        // 组装请求URL和参数
        String url = wechatClient.getLoginInfo().get(StorageLoginInfoEnum.syncUrl.getKey()) + URLEnum.SYNC_CHECK_URL.getUrl();
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        for (BaseParaEnum baseRequest : BaseParaEnum.values()) {
            params.add(new BasicNameValuePair(baseRequest.para().toLowerCase(),
                    wechatClient.getLoginInfo().get(baseRequest.value()).toString()));
        }
        params.add(new BasicNameValuePair("r", String.valueOf(new Date().getTime())));
        params.add(new BasicNameValuePair("synckey", (String) wechatClient.getLoginInfo().get("synckey")));
        params.add(new BasicNameValuePair("_", String.valueOf(new Date().getTime())));
        SleepUtils.sleep(7);
        try {
            HttpEntity entity = wechatClient.getHttpClientUtil().doGet(url, params, true, null);
            if (entity == null) {
                resultMap.put("retcode", "9999");
                resultMap.put("selector", "9999");
                return resultMap;
            }
            String text = EntityUtils.toString(entity);
            String regEx = "window.synccheck=\\{retcode:\"(\\d+)\",selector:\"(\\d+)\"\\}";
            Matcher matcher = CommonTools.getMatcher(regEx, text);
            if (!matcher.find() || matcher.group(1).equals("2")) {
                LOG.info(String.format("Unexpected sync check result: %s", text));
            } else {
                resultMap.put("retcode", matcher.group(1));
                resultMap.put("selector", matcher.group(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    /**
     * 同步消息 sync the messages
     *
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月12日 上午12:24:55
     */
    private static JSONObject webWxSync(WechatClient wechatClient) {
        JSONObject result = null;
        String url = String.format(URLEnum.WEB_WX_SYNC_URL.getUrl(),
                wechatClient.getLoginInfo().get(StorageLoginInfoEnum.url.getKey()),
                wechatClient.getLoginInfo().get(StorageLoginInfoEnum.wxsid.getKey()),
                wechatClient.getLoginInfo().get(StorageLoginInfoEnum.skey.getKey()),
                wechatClient.getLoginInfo().get(StorageLoginInfoEnum.pass_ticket.getKey()));
        Map<String, Object> paramMap = wechatClient.getParamMap();
        paramMap.put(StorageLoginInfoEnum.SyncKey.getKey(),
                wechatClient.getLoginInfo().get(StorageLoginInfoEnum.SyncKey.getKey()));
        paramMap.put("rr", -new Date().getTime() / 1000);
        String paramStr = JSON.toJSONString(paramMap);
        try {
            HttpEntity entity = wechatClient.getHttpClientUtil().doPost(url, paramStr);
            String text = EntityUtils.toString(entity, Consts.UTF_8);
            JSONObject obj = JSON.parseObject(text);
            if (obj.getJSONObject("BaseResponse").getInteger("Ret") != 0) {
                result = null;
            } else {
                result = obj;
                wechatClient.getLoginInfo().put(StorageLoginInfoEnum.SyncKey.getKey(), obj.getJSONObject("SyncCheckKey"));
                JSONArray syncArray = obj.getJSONObject(StorageLoginInfoEnum.SyncKey.getKey()).getJSONArray("List");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < syncArray.size(); i++) {
                    sb.append(syncArray.getJSONObject(i).getString("Key") + "_"
                            + syncArray.getJSONObject(i).getString("Val") + "|");
                }
                String synckey = sb.toString();
                wechatClient.getLoginInfo().put(StorageLoginInfoEnum.synckey.getKey(),
                        synckey.substring(0, synckey.length() - 1));// 1_656161336|2_656161626|3_656161313|11_656159955|13_656120033|201_1492273724|1000_1492265953|1001_1492250432|1004_1491805192
            }
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }
        return result;

    }

    public static void webWxGetContact(WechatClient wechatClient) {
        String url = String.format(URLEnum.WEB_WX_GET_CONTACT.getUrl(),
                wechatClient.getLoginInfo().get(StorageLoginInfoEnum.url.getKey()));
        Map<String, Object> paramMap = wechatClient.getParamMap();
        HttpEntity entity = wechatClient.getHttpClientUtil().doPost(url, JSON.toJSONString(paramMap));

        try {
            String result = EntityUtils.toString(entity, Consts.UTF_8);
            JSONObject fullFriendsJsonList = JSON.parseObject(result);
            // 查看seq是否为0，0表示好友列表已全部获取完毕，若大于0，则表示好友列表未获取完毕，当前的字节数（断点续传）
            long seq = 0;
            long currentTime = 0L;
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            if (fullFriendsJsonList.get("Seq") != null) {
                seq = fullFriendsJsonList.getLong("Seq");
                currentTime = new Date().getTime();
            }
            wechatClient.setMemberCount(fullFriendsJsonList.getInteger(StorageLoginInfoEnum.MemberCount.getKey()));
            JSONArray member = fullFriendsJsonList.getJSONArray(StorageLoginInfoEnum.MemberList.getKey());

            // 循环获取seq直到为0，即获取全部好友列表 ==0：好友获取完毕 >0：好友未获取完毕，此时seq为已获取的字节数
            while (seq > 0) {
                // 设置seq传参
                params.add(new BasicNameValuePair("r", String.valueOf(currentTime)));
                params.add(new BasicNameValuePair("seq", String.valueOf(seq)));
                entity = wechatClient.getHttpClientUtil().doGet(url, params, false, null);

                params.remove(new BasicNameValuePair("r", String.valueOf(currentTime)));
                params.remove(new BasicNameValuePair("seq", String.valueOf(seq)));

                result = EntityUtils.toString(entity, Consts.UTF_8);
                fullFriendsJsonList = JSON.parseObject(result);

                if (fullFriendsJsonList.get("Seq") != null) {
                    seq = fullFriendsJsonList.getLong("Seq");
                    currentTime = new Date().getTime();
                }

                // 累加好友列表
                member.addAll(fullFriendsJsonList.getJSONArray(StorageLoginInfoEnum.MemberList.getKey()));
            }
            wechatClient.setMemberCount(member.size());
            for (Iterator<?> iterator = member.iterator(); iterator.hasNext(); ) {
                JSONObject o = (JSONObject) iterator.next();
                if ((o.getInteger("VerifyFlag") & 8) != 0) { // 公众号/服务号
                    wechatClient.getPublicUsersList().add(JSON.parseObject(JSONObject.toJSONString(o), PublicUser.class));
                } else if (Config.API_SPECIAL_USER.contains(o.getString("UserName"))) { // 特殊账号
                    wechatClient.getSpecialUsersList().add(JSON.parseObject(JSONObject.toJSONString(o), SpecialUser.class));
                } else if (o.getString("UserName").indexOf("@@") != -1) { // 群聊
                    if (!wechatClient.getGroupIdList().contains(o.getString("UserName"))) {
                        wechatClient.getGroupIdList().add(o.getString("UserName"));
                        wechatClient.getGroupList().add(JSON.parseObject(JSONObject.toJSONString(o), Group.class));
                    }
                } else if (o.getString("UserName").equals(wechatClient.getUserSelf().getString("UserName"))) { // 自己
                    wechatClient.getFriendList().remove(JSON.parseObject(JSONObject.toJSONString(o), Friend.class));
                } else { // 普通联系人
                    wechatClient.getFriendList().add(JSON.parseObject(JSONObject.toJSONString(o), Friend.class));
                }
            }
            return;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return;
    }
}
