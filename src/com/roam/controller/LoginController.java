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

	/**
	 *  register
	 *  if the request URL contains register use this method
	 *  
	 *  ^(*￣(oo)￣)^: the request method must be post
	 *  
	 *  P.S:  note "@ModelAttribute("user")" because can't find why it exist
	 */
	@ResponseBody
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public User register(/*@ModelAttribute("user")*/ User user) throws Exception {
		user.setPassword(EncrypUtil.encryptMD5(user.getPassword()));//encrypt the password by MD5
		User resultUser = loginService.register(user);//register 
		if (resultUser != null) {//if exist user : means register success, then return the register user
			return resultUser;
		} 
		return null;//if insert database error, return null
	}

	/**
	 *  login 
	 *  if the request URL contains login use this method
	 *  ^(*￣(oo)￣)^: the request method must be post
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView login(User user, ModelAndView welcomeView) {
		UsernamePasswordToken token = new UsernamePasswordToken(user.getName(), user.getPassword());//user authentication
		token.setRememberMe(true);//set rememberMe
		Subject subject = SecurityUtils.getSubject();//current user
		try {
			subject.login(token);//judge the user whether authenticated
		} catch (ExcessiveAttemptsException e) {//exception : login count too more
			welcomeView.setViewName("redirect:content/html/register.html");
			return welcomeView;
		} catch (AuthenticationException e) {
			System.out.println("error:   " + e.getMessage());
		}
		if (subject.isAuthenticated()) {//if the user is authenticated
			Subject currentUser = SecurityUtils.getSubject();
			if (currentUser.isPermitted("adminpermission")) {//judge the user permitted whether administrator
				welcomeView.addObject("permission", "给管理员递茶");
			} else if (currentUser.isPermitted("userpermission")) {//judge the user permitted whether user
				welcomeView.addObject("permission", "用户棒棒哒");
			} else {//other
				welcomeView.addObject("permission", "游客你好");
			}
			welcomeView.setViewName("resource/welcome");//set jump view name
			welcomeView.addObject("welcomeUser", user);//set available object
		} 
		return welcomeView;
	}

	// 角色注解
	// @RequiresRoles("admin")
	// 权限注解
	// @RequiresPermissions("adminpermission")
	/**
	 * lookForRes: check the user role
	 * 
	 * ^(*￣(oo)￣)^: the request method need be get
	 */
	@RequestMapping(value = "/lookForRes", method = RequestMethod.GET)
	public ModelAndView lookForRes(ModelAndView view) {
		Subject subject = SecurityUtils.getSubject();
		if (subject.hasRole("admin")) {//if the user role is admin
			view.setViewName("resource/res");
			return view;
		}
		if (subject.hasRole("user")) {//if the user role is user
			view.setViewName("resource/userres");
			return view;
		}
		view.setViewName("resource/nopermission");//other
		return view;
	}
	
	/**
	 * logout 
	 * 
	 * ^(*￣(oo)￣)^: the request method need be get
	 * 
	 * @param view
	 * @return
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ModelAndView logout(ModelAndView view) {
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.logout();
		} catch (AuthenticationException e) {
			//why not use e.getMessage();??
			e.printStackTrace();//在命令行打印异常信息在程序中出错的位置及原因

		}
		view.setViewName("login");
		return view;
	}
}
