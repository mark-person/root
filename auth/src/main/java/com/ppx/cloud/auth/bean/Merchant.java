package com.ppx.cloud.auth.bean;

import java.util.Date;

import com.ppx.cloud.common.jdbc.annotation.Column;
import com.ppx.cloud.common.jdbc.annotation.Id;

/**
 * 商户
 * @author mark
 * @date 2018年7月2日
 */
public class Merchant {
	
	@Id
	private Integer merId;
	
	private String merName;
	
	@Column(readonly=true)
	private Date created;
	
	@Column(readonly=true)
	private String loginAccount;
	
	@Column(readonly=true)
	private String loginPassword;
	
	@Column(readonly=true)
	private Integer accountStatus;

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
