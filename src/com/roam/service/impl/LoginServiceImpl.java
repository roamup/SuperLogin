package com.roam.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roam.mapper.UserMapper;
import com.roam.pojo.User;
import com.roam.service.ILoginService;

@Service
public class LoginServiceImpl implements ILoginService{

	@Autowired
	UserMapper userMapper ;
	
	@Override
	public User checkLoginUser(User user) {
		User resultUser = userMapper.checkLoginUser(user);
		return resultUser;
	}

	@Override
	public User register(User user) {
		userMapper.insert(user);
		return userMapper.checkLoginUser(user);
	}

}
