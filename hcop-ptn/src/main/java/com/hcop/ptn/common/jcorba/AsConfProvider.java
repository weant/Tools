package com.hcop.ptn.common.jcorba;

public interface AsConfProvider {
	
	/**
	 * 获取AS host内容
	 * @return
	 */
	String getIP();
	/**
	 * 获取AS host内容
	 * @return
	 */
	String getHost();
	/**
	 * 获取As 的端口信息
	 * @return
	 */
	int getNsPort();
	
	/**
	 * 获取AS对应的实例号
	 * @return
	 */
	int getInst();
	
	/**
	 * 获取数字版本信息
	 * @return
	 */
	String getDigitVersion();
	
	/**
	 * 获取As类型
	 * @return
	 */
	ASType getType();
	
	/**
	 * 获取登录名
	 * @return
	 */
	String getUserName();
	
	/**
	 * 获取登录密码
	 * @return
	 */
	String getPassword();

}
