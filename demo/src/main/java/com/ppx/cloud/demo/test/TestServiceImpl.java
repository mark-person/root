package com.ppx.cloud.demo.test;

import org.springframework.stereotype.Service;

import com.ppx.cloud.common.jdbc.MyDaoSupport;

@Service
public class TestServiceImpl extends MyDaoSupport {
	
	public int test() {
		int c = getJdbcTemplate().queryForObject("select count(*) from test", Integer.class);
		return c;
	}
}
