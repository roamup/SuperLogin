package com.roam.service;

import com.roam.pojo.User;

public interface ILoginService {

	User checkLoginUser(User user);

	User register(User user);
}
