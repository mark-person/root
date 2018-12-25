package com.ppx.cloud.common.exception;


/**
 * 非法URL异常
 * uri长度>64 uri带有. 
 * @author mark
 * @date 2018年12月24日
 */
@SuppressWarnings("serial")
public class CustomException extends RuntimeException {
	
	// 0:成功 -1:系统忙(500错误) 4000:存在 400x其它业务逻辑；403?:权限；404?: 4040 no found 参数 uri长度、不合法等
	
	// 403?:URI或参数没权限
	public final static int PERMISSION_URI = 4030;
	public final static int PERMISSION_RESUBMIT = 4031;
	public final static int PERMISSION_PARAMS = 4033;
	
	// 404?,URI找不到或不合法
	public final static int URI_NOT_FOUND = 4040;
	public final static int URI_OUT_OF_LENGTH = 4041;
	public final static int URI_EXISTS_DOT = 4042;
	public final static int PARAM_OUT_OF_PAGE_SIZE = 4043;
	public final static int PARAM_OUT_OF_ORDER = 4044;
	
	// 错误
	public final static int ERROR_INIT = 5000;
	public final static int ERROR_UPDATE_NO_PK = 5001;
	public final static int ERROR_LOGIN = 5002;
	
	
	
	private int errcode;
	
	private String errmsg;
	
	public CustomException(int errcode, String errmsg) {
		super(errmsg);
		this.errcode = errcode;
		this.errmsg = errmsg;
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
}


