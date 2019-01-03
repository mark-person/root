package com.ppx.cloud.common.page;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.ppx.cloud.common.exception.CustomException;
import com.ppx.cloud.common.exception.ErrorCode;
import com.ppx.cloud.common.exception.custom.IllegalRequestException;


public class Page {

	// 防止传入数量过大
	private final static int MAX_PAGE_SIZE = 50;

	private int pageSize = 5;

	private int pageNumber = 1;

	private int totalRows;

	private String orderName;

	private String orderType;
	
	private String unique;
	
	private Set<String> permitOrderNameSet = new HashSet<String>(4);
	
	
	// 不能使用set开头，防止传参数攻击
	public Page addDefaultOrderName(String orderName) {
		this.orderName = StringUtils.isEmpty(this.orderName) ? orderName : this.orderName;
		permitOrderNameSet.add(orderName);
		return this;
	}
	
	// 不能使用set开头，防止传参数攻
	public Page addDefaultOrderType(String orderType) {
		this.orderType = StringUtils.isEmpty(this.orderType) ? orderType : this.orderType;
		return this;
	}
	
	// 不能使用set开头，防止传参数攻
	public Page addPermitOrderName(String... permitOrderName) {
		permitOrderNameSet.addAll(Arrays.asList(permitOrderName));
		return this;
	}
	
	// 不能使用set开头，防止传参数攻
    public Page addUnique(String unique) {
        this.unique = unique;
        return this;
	}
	 
	public String getUnique() {
        return unique;
    }

	public int getPageSize() {
		return pageSize;
	}

    public void setPageSize(int pageSize) {
		if (pageSize > MAX_PAGE_SIZE) {
			throw new IllegalRequestException(ErrorCode.PARAM_OUT_OF_PAGE_SIZE, "pageSize:" + pageSize);
		}
		this.pageSize = pageSize;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public String getOrderName() {
		if (!StringUtils.isEmpty(orderName) && !permitOrderNameSet.contains(orderName)) {
			throw new IllegalRequestException(ErrorCode.PARAM_OUT_OF_ORDER, "getOrderName:" + orderName);
		}
		return orderName;
	}

	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}

	public String getOrderType() {
		// 默认为desc
		return StringUtils.isEmpty(orderType) ? "desc" : orderType;
	}

	public void setOrderType(String orderType) {
		if (StringUtils.isEmpty(orderType) || "desc".equals(orderType) || "asc".equals(orderType)) {
			this.orderType = orderType;
		}
		else {
			throw new IllegalRequestException(ErrorCode.PARAM_OUT_OF_ORDER, "setOrderType:" + orderType);
		}
	}

}
