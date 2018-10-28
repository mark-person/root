package com.ppx.cloud.common.exception.custom;

/**
 * 非法URL异常
 * @author dengxz
 * @date 2018年6月14日
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


