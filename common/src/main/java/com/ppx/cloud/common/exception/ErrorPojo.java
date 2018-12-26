package com.ppx.cloud.common.exception;

/**
 * 错误代码POJO
 * @author mark
 * @date 2018年12月26日
 */
public class ErrorPojo {
	// 0为成功，其他为失败
	private int errcode;
	// 失败原因，非0时errmsg为必填
	private String errmsg;
	// 错误级别
	private int errlevel;
	

	public ErrorPojo(int errcode, String errmsg, int errlevel) {
		this.errcode = errcode;
		this.errmsg = errmsg;
		this.errlevel = errlevel;
	}

	public int getErrcode() {
		return errcode;
	}

	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public int getErrlevel() {
		return errlevel;
	}

	public void setErrlevel(int errlevel) {
		this.errlevel = errlevel;
	}

}
