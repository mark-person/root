/**
 * 
 */
package com.ppx.cloud.demo.test;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppx.cloud.common.util.DateUtils;

/**
 * @author mark
 *
 * @date 2018年11月5日
 */
public class TestPojo {
	
	private Integer testId;
	
	private String testName;
	
	/**
	 * @JsonFormat(pattern=DateUtils.DATE_PATTERN)
	 * @DateTimeFormat(pattern=DateUtils.DATE_PATTERN)
	 */
	
	
	@JsonFormat(pattern=DateUtils.DATE_PATTERN)
	private Date testDate;
	
	private Date testTime;
	
	private Float testValue;

	public Integer getTestId() {
		return testId;
	}

	public void setTestId(Integer testId) {
		this.testId = testId;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public Date getTestDate() {
		return testDate;
	}

	public void setTestDate(Date testDate) {
		this.testDate = testDate;
	}

	public Date getTestTime() {
		return testTime;
	}

	public void setTestTime(Date testTime) {
		this.testTime = testTime;
	}

	public Float getTestValue() {
		return testValue;
	}

	public void setTestValue(Float testValue) {
		this.testValue = testValue;
	}
	
	
	

}
