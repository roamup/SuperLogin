package com.roam.util;

import org.junit.Assert;
import org.junit.Test;

public class Md5Test {

	@Test
	public void testEncryptMD5() {
		EncrypUtil encrypUtil = new EncrypUtil();
		Assert.assertEquals("202cb962ac59075b964b07152d234b70", encrypUtil.encryptMD5("123"));
		//断言语法 http://blog.csdn.net/mayh554024289/article/details/38375457
	}

}
