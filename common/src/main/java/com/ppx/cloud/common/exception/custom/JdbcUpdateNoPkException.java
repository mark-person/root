package com.ppx.cloud.common.exception.custom;

/**
 * 更新时没有找到主键annotation或主键为空值异常
 * @author dengxz
 * @date 2018年6月14日
 */
@SuppressWarnings("serial")
public class JdbcUpdateNoPkException extends RuntimeException {
	
	public JdbcUpdateNoPkException() {
		super("JdbcUpdateNoPk");
	}
}


