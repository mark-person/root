package com.ppx.cloud.auth.common;

/**
 * 登录帐号
 * @author mark
 * @date 2018年7月2日
 */
public class LoginAccount  {
	
    private Integer accountId;

    private String loginAccount;

	private Integer userId;
	
	private String userName;

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public String getLoginAccount() {
		return loginAccount;
	}

	public void setLoginAccount(String loginAccount) {
		this.loginAccount = loginAccount;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
	
	

}