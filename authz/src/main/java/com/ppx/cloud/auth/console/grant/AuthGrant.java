package com.ppx.cloud.auth.console.grant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthGrant {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthGrant.class);
    
    private Integer accountId;
    private String resIds;

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public List<Integer> getResIds() {
        List<Integer> returnList = new ArrayList<>(0);
        if (StringUtils.isEmpty(resIds)) {
            return returnList;
        }
        else {
        	String[] resId = resIds.split(",");
        	for (String id : resId) {
        		returnList.add(Integer.parseInt(id));
			}
        }
        return returnList;
    }

    public void setResIds(String resIds) {
        this.resIds = resIds;
    }

}
