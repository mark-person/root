package com.ppx.cloud.common.exception.custom;


/**
 * Nosql异常
 * @author mark
 * @date 2018年11月14日
 */
@SuppressWarnings("serial")
public class NosqlException extends RuntimeException {
	
	public NosqlException(Exception e) {
		super("Nosql", e);
	}
	
}


