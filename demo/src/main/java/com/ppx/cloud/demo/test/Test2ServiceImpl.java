/**
 * 
 */
package com.ppx.cloud.demo.test;

import org.springframework.stereotype.Service;

import com.ppx.cloud.common.jdbc.MyDaoSupport;

/**
 * @author mark
 * @date 2018年11月14日
 */
@Service
public class Test2ServiceImpl extends MyDaoSupport {

	public void test() {
		

		getJdbcTemplate().queryForList("select * from test limit ?", 3);
		
		getJdbcTemplate().queryForList("select * from test limit ?", 3);
	
		
	}
}
