package com.ppx.cloud.common.context;

/**
 * 公共类上下文
 * 
 * @author mark
 * @date 2018年6月20日
 */
public class MyContext {

	public static ThreadLocal<User> threadLocalUser = new ThreadLocal<User>();

	public static void setUser(User user) {
		threadLocalUser.set(user);
	}

	public static User getUser() {
		return threadLocalUser.get();
	}

}
