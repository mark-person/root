package com.ppx.cloud.demo.product;

import org.springframework.stereotype.Service;

import com.ppx.cloud.common.context.MyContext;
import com.ppx.cloud.common.jdbc.MyDaoSupport;

@Service
public class ProductServiceImpl extends MyDaoSupport {
	
	public void test() {
		int c = getJdbcTemplate().queryForObject("select count(*) from test", Integer.class);
		System.out.println("ccc:" + MyContext.getUser().getUserId());
	}
}
