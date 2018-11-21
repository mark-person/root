/**
 * 
 */
package com.ppx.cloud.demo.test;

import java.util.HashMap;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.ppx.cloud.common.jdbc.MyDaoSupport;

/**
 * @author mark
 * @date 2018年11月14日
 */
@Service
public class Test2ServiceImpl extends MyDaoSupport {

	public void test() {
		
		NamedParameterJdbcTemplate nameTemplate = new NamedParameterJdbcTemplate(getJdbcTemplate());
		
		var para = new HashMap<String, Object>();
		para.put("testId", 1);
		nameTemplate.update("update test set test_name = '112' where test_id = :testId", para);
	
		
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
