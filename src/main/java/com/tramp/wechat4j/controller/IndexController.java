package com.tramp.wechat4j.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.tramp.wechat4j.wechat.client.WechatClient;
import com.tramp.wechat4j.wechat.contants.Contants;
import com.tramp.wechat4j.wechat.entity.Friend;
import com.tramp.wechat4j.wechat.entity.WechatInfo;
import com.tramp.wechat4j.wechat.utils.CommonTools;
import com.tramp.wechat4j.wechat.utils.LoginUtil;
import com.tramp.wechat4j.wechat.utils.SleepUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;

@Controller
@RequestMapping("/index/*")
public class IndexController extends BaseController {
	private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);

	@RequestMapping("loginPage.do")
	public String loginPage(HttpServletRequest request, ModelMap modelMap) {
		return "loginPage";
	}

	/**
	 *
	 * <B>方法名称：</B><BR>
	 * <B>概要说明：登录页</B><BR>
	 * @param
	 * @return
	 */
	@RequestMapping(value = "login.do")
	public @ResponseBody Map login(HttpSession session, HttpServletRequest request, String mobile, String password, String code) {
		Map<String, Object> result = Maps.newHashMap();
		result.put("code", 201);
		if (StringUtils.isBlank(mobile)) {
			result.put("msg", "请输入账号!");
			return result;
		}
		if (StringUtils.isBlank(password)) {
			result.put("msg", "请输入密码!");
			return result;
		}
		//验证码通过
		/*if(!code.toLowerCase().equals(session.getAttribute("check").toString().toLowerCase())){
			result.put("msg", "验证码输入错误!");
			return result;
		}*/

		if ("admin".equals(mobile) && "123456".equals(password)) {
			result.put("code", 200);
		}
		else {
			result.put("msg", "账号或密码错误");

		}
		return result;
	}

	@RequestMapping("index.do")
	public String index(HttpServletRequest request, ModelMap modelMap) {

		return "index";
	}

	@RequestMapping("main.do")
	public String main(HttpServletRequest request, ModelMap modelMap) {
		WechatInfo info = Contants.WECHAT_MAP.get(Contants.WECHAT_MAP_KEY);
		if (info ==null ){
			WechatClient wechatClient = new WechatClient();
			//获取uuid
			LOG.info("获取UUID");
			for (int count = 0; count < 10; count++) {
				while (StringUtils.isEmpty(wechatClient.getUid())) {
					LOG.info("1. 获取微信UUID");
					String uuid = wechatClient.getUuid();
					wechatClient.setUuid(uuid);
					if (StringUtils.isEmpty(uuid)) {
						LOG.warn("1.1. 获取微信UUID失败，两秒后重新获取");
					}else {
						LOG.info("获取登陆二维码图片");
						String qrName = System.currentTimeMillis()+".jpg";
						String path = request.getSession().getServletContext().getRealPath("/");
						if (wechatClient.getQR(path,qrName)) {
							LOG.info("请扫描二维码图片，并在手机上确认:" + path+qrName);
							modelMap.put("qrPath", qrName);
						} else if (count == 10) {
							LOG.error("2.2. 获取登陆二维码图片失败");
							break;
						}
					}
					SleepUtils.sleep(2000);
				}
			}
			WechatInfo wechatInfo = new WechatInfo();
			wechatInfo.setWechatClient(wechatClient);
			Contants.WECHAT_MAP.put(Contants.WECHAT_MAP_KEY, wechatInfo);
		}else {
			modelMap.put("loginFlag", "1");

		}

		return "main";
	}
}
