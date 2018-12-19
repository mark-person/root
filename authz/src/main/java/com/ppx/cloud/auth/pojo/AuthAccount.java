package com.ppx.cloud.auth.pojo;

import java.util.Date;

import com.ppx.cloud.common.jdbc.annotation.Column;
import com.ppx.cloud.common.jdbc.annotation.Id;


/**
 * 帐号
 * @author mark
 * @date 2018年12月16日
 */
public class AuthAccount {

	@Id
	private Integer accountId;

	private Integer userId;

	private String loginAccount;

	private String loginPassword;

	@Column(readonly = true)
	private String userName;

	@Column(readonly = true)
	private Date created;
	
	private Integer accountStatus;
	
	private Date modified;
	
	private Integer userStatus;
	
	private Integer userAccountStatus;
	

	public Integer getUserAccountStatus() {
		return userAccountStatus;
	}

	public void setUserAccountStatus(Integer userAccountStatus) {
		this.userAccountStatus = userAccountStatus;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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

	public Integer getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(Integer accountStatus) {
		this.accountStatus = accountStatus;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Integer getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(Integer userStatus) {
		this.userStatus = userStatus;
	}

}
