package com.ppx.cloud.auth.pojo;

import java.util.Date;

import com.ppx.cloud.common.jdbc.annotation.Column;
import com.ppx.cloud.common.jdbc.annotation.Id;


/**
 * 用户
 * @author mark
 * @date 2018年12月16日
 */
public class AuthUser {
	
	@Id
	private Integer userId;
	
	private String userName;
	
	@Column(readonly=true)
	private Date created;
	
	@Column(readonly=true)
	private String loginAccount;
	
	@Column(readonly=true)
	private String loginPassword;
	
	@Column(readonly=true)
	private Integer accountStatus;

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

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getLoginAccount() {
		return loginAccount;
	}

	public void setLoginAccount(String loginAccount) {
		this.loginAccount = loginAccount;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public Integer getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(Integer accountStatus) {
		this.accountStatus = accountStatus;
	}

}
