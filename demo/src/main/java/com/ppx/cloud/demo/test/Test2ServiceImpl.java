/**
 * 
 */
package com.ppx.cloud.demo.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mysql.cj.xdevapi.DbDoc;
import com.ppx.cloud.common.jdbc.nosql.MyNosqlSupport;

/**
 * @author mark
 * @date 2018年11月14日
 */
@Service
public class Test2ServiceImpl extends MyNosqlSupport {

	public void test() {
		
		var map = Map.of("value", 88);

		super.testSql("test", "100", map);
		
		
		
		
		
	}
}
