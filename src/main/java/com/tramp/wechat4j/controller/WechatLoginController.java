package com.tramp.wechat4j.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.tramp.wechat4j.wechat.contants.Contants;
import com.tramp.wechat4j.wechat.entity.Friend;
import com.tramp.wechat4j.wechat.entity.WechatInfo;
import com.tramp.wechat4j.wechat.enums.LoginParaEnum;
import com.tramp.wechat4j.wechat.enums.ResultEnum;
import com.tramp.wechat4j.wechat.enums.URLEnum;
import com.tramp.wechat4j.wechat.utils.CommonTools;
import com.tramp.wechat4j.wechat.utils.LoginUtil;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.tramp.wechat4j.wechat.callback.MessageCallback;
import com.tramp.wechat4j.wechat.client.WechatClient;
import com.tramp.wechat4j.wechat.entity.Message;

/**
 * @author chenjm1
 * @since 2017/6/22
 */
@Controller
@RequestMapping("/wechat/*")
public class WechatLoginController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(WechatLoginController.class);

    @RequestMapping("info.do")
    public String info(HttpServletRequest request, ModelMap modelMap) {
        WechatInfo wechatInfo = Contants.WECHAT_MAP.get(Contants.WECHAT_MAP_KEY);
        modelMap.put("groupList",wechatInfo.getWechatClient().getGroupList());
        modelMap.put("friendList",wechatInfo.getWechatClient().getFriendList());
        return "info";
    }

    @RequestMapping(value = "login.do")
    @ResponseBody
    public Boolean login(HttpServletRequest request) {
        boolean isLogin = false;
        WechatInfo wechatInfo = Contants.WECHAT_MAP.get(Contants.WECHAT_MAP_KEY);
        if(wechatInfo!=null){
            WechatClient wechatClient = wechatInfo.getWechatClient();
            // 组装参数和URL
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair(LoginParaEnum.LOGIN_ICON.para(), LoginParaEnum.LOGIN_ICON.value()));
            params.add(new BasicNameValuePair(LoginParaEnum.UUID.para(), wechatInfo.getWechatClient().getUid()));
            params.add(new BasicNameValuePair(LoginParaEnum.TIP.para(), LoginParaEnum.TIP.value()));
            while (!isLogin) {
                long millis = System.currentTimeMillis();
                params.add(new BasicNameValuePair(LoginParaEnum.R.para(), String.valueOf(millis / 1579L)));
                params.add(new BasicNameValuePair(LoginParaEnum._.para(), String.valueOf(millis)));
                HttpEntity entity = wechatClient.getHttpClientUtil().doGet(URLEnum.LOGIN_URL.getUrl(), params, true, null);

                try {
                    String result = EntityUtils.toString(entity);
                    String status = wechatClient.checklogin(result);

                    if (ResultEnum.SUCCESS.getCode().equals(status)) {
                        wechatClient.processLoginInfo(result); // 处理结果
                        isLogin = true;
                        wechatClient.setAlive(isLogin);
                        LOG.info("5. 登陆成功，微信初始化");
                        if (!LoginUtil.webWxInit(wechatClient)) {
                            LOG.info("6. 微信初始化异常");
                            //System.exit(0);
                        }

                        LOG.info("6.开启微信状态检测线程");
                        //new Thread(new CheckLoginStatusThread()).start();

                        LOG.info("7. 开启微信状态通知");
                        LoginUtil.wxStatusNotify(wechatClient);

                        LOG.info("8. 清除。。。。");
                        CommonTools.clearScreen();
                        LOG.info(String.format("欢迎回来， %s", wechatClient.getNickName()));

                        LOG.info("9. 开始接收消息");
                        LoginUtil.startReceiving(wechatClient);

                        LOG.info("10. 获取联系人信息");
                        LoginUtil.webWxGetContact(wechatClient);

                        LOG.info("11. 缓存本次登陆好友相关消息");
                        for (Friend friend : wechatClient.getFriendList()) {
                            wechatClient.getUserInfoMap().put(friend.getNickName(), friend);
                            wechatClient.getUserInfoMap().put(friend.getUserName(), friend);
                        } // 登陆成功后缓存本次登陆好友相关消息（NickName, UserName）

                        return true;
                    }
                    if (ResultEnum.WAIT_CONFIRM.getCode().equals(status)) {
                        LOG.info("请点击微信确认按钮，进行登陆");
                    }

                } catch (Exception e) {
                    LOG.error("微信登陆异常！", e);
                }
            }
        }

        return false;
    }

}
