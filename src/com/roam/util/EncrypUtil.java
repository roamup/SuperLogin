package com.roam.util;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.Sha256Hash;

public class EncrypUtil {

	/**
	 *  MD5 加密
	 * @param source
	 * @return
	 */
	public static final String encryptMD5(String source) {
		if (source == null) {
			source = "";
		}
		Md5Hash md5 = new Md5Hash(source);
		return md5.toString();
	}

	/**
	 * SHA256加密
	 * @param source
	 * @return
	 */
	public static final String encryptSHA256(String source) {
		if (source == null) {
			source = "";
		}
		Sha256Hash sha256 = new Sha256Hash(source, "roam");
		return sha256.toString();
	}
}
