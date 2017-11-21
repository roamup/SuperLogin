package com.roam.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roam.mapper.RoleMapper;
import com.roam.service.IRoleService;

@Service
public class RoleService implements IRoleService{
	
	@Autowired
	RoleMapper roleMapper ;

	@Override
	public String getUserRole(String userName) {
		return roleMapper.getUserRole(userName);
	}

}
