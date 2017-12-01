package com.roam.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.roam.pojo.User;
import com.roam.service.ILoginService;
import com.roam.util.EncrypUtil;

@Controller
public class LoginController {

	@Autowired
	ILoginService loginService;

	@ResponseBody
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public User register(User user) throws Exception {
		user.setPassword(EncrypUtil.encryptMD5(user.getPassword()));
		User resultUser = loginService.register(user); 
		if (resultUser != null) {
			return resultUser;
		} 
		return null;
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView login(User user, String remeberMe, ModelAndView welcomeView) {
		UsernamePasswordToken token = new UsernamePasswordToken(user.getName(), user.getPassword());
		token.setRememberMe(remeberMe==null ? false : true );
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(token);
		} catch (ExcessiveAttemptsException e) {
			welcomeView.setViewName("login");
			welcomeView.addObject("pwdErrorLoginTips","该用户登陆错误次数超过3次，请联系客服~");
			return welcomeView;
		} 
		//认证失败
		catch (AuthenticationException e) {
			welcomeView.setViewName("login");
			welcomeView.addObject("pwdErrorLoginTips","该用户还有"+ e.getMessage() + "次登录机会~");
			return welcomeView;
		}
		if (subject.isAuthenticated()) {
			Subject currentUser = SecurityUtils.getSubject();
			if (currentUser.isPermitted("adminpermission")) {
				welcomeView.addObject("permission", "给管理员递茶");
			} else if (currentUser.isPermitted("userpermission")) {
				welcomeView.addObject("permission", "用户棒棒哒");
			} else {//other
				welcomeView.addObject("permission", "游客你好");
			}
			welcomeView.setViewName("resource/welcome");
			welcomeView.addObject("welcomeUser", user);
		} 
		return welcomeView;
	}

	// 角色注解
	// @RequiresRoles("admin")
	// 权限注解
	// @RequiresPermissions("adminpermission")
	@RequestMapping(value = "/lookForRes", method = RequestMethod.GET)
	public ModelAndView lookForRes(ModelAndView view) {
		Subject subject = SecurityUtils.getSubject();
		if (subject.hasRole("admin")) {
			view.setViewName("resource/res");
			return view;
		}
		if (subject.hasRole("user")) {
			view.setViewName("resource/userres");
			return view;
		}
		view.setViewName("resource/nopermission");
		return view;
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ModelAndView logout(ModelAndView view) {
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.logout();
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}
		view.setViewName("redirect:content/html/login.html");
		return view;
	}
	
}
