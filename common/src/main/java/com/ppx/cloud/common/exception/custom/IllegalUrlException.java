package com.ppx.cloud.common.exception.custom;


/**
 * 非法URL异常
 * @author mark
 * @date 2018年11月8日
 */
@SuppressWarnings("serial")
public class IllegalUrlException extends RuntimeException {
	
	public IllegalUrlException() {
		super("url illegal");
	}
	
	public IllegalUrlException(String msg) {
		super(msg);
	}
}


