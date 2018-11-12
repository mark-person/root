package com.ppx.cloud.auth.common;

/**
 * 登录帐号
 * @author mark
 * @date 2018年7月2日
 */
public class LoginAccount  {
	
    private Integer accountId;

    private String loginAccount;

	private Integer merId;
	
	private String merName;
	
	public boolean isMainAccount() {
		return accountId == merId;
	}
	
	public boolean isAdmin() {
		return accountId == -1 ? true : false;
	}

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

	public Integer getMerId() {
		return merId;
	}

	public void setMerId(Integer merId) {
		this.merId = merId;
	}

	public String getMerName() {
		return merName;
	}

	public void setMerName(String merName) {
		this.merName = merName;
	}

	
	

}