package com.ppx.cloud.common.context;



/**
 * # 公共类上下文，为了可以多auth登录ID，传值给monitor
 * @author mark
 * @date 2018年12月29日
 */
public class CommonContext {

	public static ThreadLocal<Integer> threadLocalAccountId = new ThreadLocal<Integer>();

	public static void setAccountId(Integer accountId) {
	    threadLocalAccountId.set(accountId);
	}
	
	public static Integer getAccountId() {
	   return threadLocalAccountId.get();
	}

}
