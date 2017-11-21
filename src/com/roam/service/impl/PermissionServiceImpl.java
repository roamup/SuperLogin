package com.roam.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roam.mapper.PermissionMapper;
import com.roam.pojo.User;
import com.roam.service.IPermissionService;

@Service
public class PermissionServiceImpl implements IPermissionService{
	
	@Autowired
	PermissionMapper permissionMapper ;

	@Override
	public String getUserPermission(String username) {
		return permissionMapper.getUserPermission(username);
	}

}
