package com.roam.service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roam.pojo.User;
import com.roam.util.EncrypUtil;
import com.roam.util.StringUtil;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@Service("monitorRealm")
public class MonitorRealm extends AuthorizingRealm {

	@Autowired
	ILoginService loginService;

	@Autowired
	IPermissionService permissionService;

	@Autowired
	IRoleService roleService;

	private Ehcache passwordRetryCache;
	private AtomicInteger retryCount ;

	public MonitorRealm() {
		super();
		CacheManager cacheManager = CacheManager.create(CacheManager.class.getClassLoader().getResource("ehcache.xml"));
		passwordRetryCache = (Ehcache) cacheManager.getCache("passwordRetryCache");
	}

	// 授权
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		String username = (String) principals.getPrimaryPrincipal();
		// 这里可以查出一个list 用户可以有多个角色
		String roleName = roleService.getUserRole(username);
		// 这里可以查出一个list 角色可以有多个权限 这里也可以根据角色去查 因为我数据库上就是单一的关系 所以直接这么查了
		String permissionname = permissionService.getUserPermission(username);

		Set<String> roleNames = new HashSet<String>();
		Set<String> permissions = new HashSet<String>();
		roleNames.add(roleName);
		if (!StringUtil.isEmpty(permissionname)) {
			permissions.add(permissionname);
		}
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleNames);
		info.setStringPermissions(permissions);
		return info;
	}

	// 认证
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken){
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		String username = (String) token.getPrincipal();
		countLoginErrorCount(username);
		User checkUser = new User();
		checkUser.setName((String) token.getPrincipal());
		checkUser.setPassword(EncrypUtil.encryptMD5(new String((char[]) token.getCredentials())));
		User resultUser = loginService.checkLoginUser(checkUser);
		if (resultUser == null) {
			throw new AuthenticationException(String.valueOf(3-retryCount.intValue()));
		}
		passwordRetryCache.remove(username);
		return new SimpleAuthenticationInfo(resultUser.getName(), new String((char[]) token.getCredentials()), getName())  ;
	}

	public void clearCachedAuthorizationInfo(String principal) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
		clearCachedAuthorizationInfo(principals);
	}
	
	private void countLoginErrorCount(String username){
		// 计算登陆次数
		Element element = passwordRetryCache.get(username);
		if (element == null) {
			element = new Element(username, new AtomicInteger(0));
			passwordRetryCache.put(element);
		}
		retryCount = (AtomicInteger) element.getObjectValue();
		if (retryCount.incrementAndGet() >= 3 ) {
			throw new ExcessiveAttemptsException();
		}
	}

}
