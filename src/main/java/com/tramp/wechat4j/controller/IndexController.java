package com.tramp.wechat4j.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;

@Controller
@RequestMapping("/index/*")
public class IndexController extends BaseController {

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

		return "main";
	}
}
