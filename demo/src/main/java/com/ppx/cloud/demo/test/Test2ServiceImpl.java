/**
 * 
 */
package com.ppx.cloud.demo.test;

import java.util.HashMap;

import org.slf4j.MarkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.ppx.cloud.common.jdbc.MyDaoSupport;

/**
 * @author mark
 * @date 2018年11月14日
 */
@Service
public class Test2ServiceImpl extends MyDaoSupport {
	
	// private Logger logger = LoggerFactory.getLogger(Test2ServiceImpl.class);
	// logger.debug(MarkerFactory.getMarker("123"), "xxxxx");

	public void test() {
		
		
		NamedParameterJdbcTemplate nameTemplate = new NamedParameterJdbcTemplate(getJdbcTemplate());
		
		
		var para = new HashMap<String, Object>();
		para.put("testId", "(select '11')");
		para.put("xxxx", 2222);
		
		int ccc = getJdbcTemplate().queryForObject("/*NamedParameter*/select 123", Integer.class);
		
		
		
		String c = nameTemplate.queryForObject("/*NamedParameter*/select :testId", para, String.class);
		
		System.out.println("ccccccccc:" + c);
		// nameTemplate.update("update test set test_name = '112' where test_id = :testId", para);
	
		
		
//		List<Object[]> list = new ArrayList<Object[]>();
//		Object[] o1 = new Object[] {"1"};
//		Object[] o2 = new Object[] {"2"};
//		Object[] o3 = new Object[] {"3"};
//		list.add(o1);
//		list.add(o2);
//		list.add(o3);
//		
//		getJdbcTemplate().batchUpdate("update test set test_name = '112' where test_id = ?", list);
//		
//		getJdbcTemplate().batchUpdate("update test set test_name = '112' where test_id = ?", list);
		
		//getJdbcTemplate().queryForList("select * from test limit ?", 3);
		
		//getJdbcTemplate().queryForList("select * from test limit ?", 3);
	
		
	}
}
