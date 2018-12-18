package com.ppx.cloud.auth.bean;

import java.util.Date;

import com.ppx.cloud.common.jdbc.annotation.Column;
import com.ppx.cloud.common.jdbc.annotation.Id;

/**
 * 商户帐号
 * @author mark
 * @date 2018年7月2日
 */
public class MerchantAccount {

	@Id
	private Integer accountId;

	private Integer merId;

	private String loginAccount;

	private String loginPassword;
	

	@Column(readonly = true)
	private String merName;

	@Column(readonly = true)
	private Date created;
	
	private Integer accountStatus;
	
	private Date modified;
	
	private Integer merAccountStatus;

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
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

    public Integer getMerAccountStatus() {
        return merAccountStatus;
    }

    public void setMerAccountStatus(Integer merAccountStatus) {
        this.merAccountStatus = merAccountStatus;
    }
	
	

}
