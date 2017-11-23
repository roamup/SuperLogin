package com.roam.util;

public class StringUtil {

	/**
	 * char transfer string
	 * @param charList
	 * @return
	 */
	public final static String charToString(char [] charList){
		StringBuffer sb = new StringBuffer();
		for (char c : charList) {
			sb.append(c);
		}
		return sb.toString();
	}
	
	/**
	 * judge the str whether empty
	 * @param str
	 * @return
	 */
	public final static boolean isEmpty(String str){
		if(str==null || "".equals(str)){
			return true;
		}
		return false;
	}
}
