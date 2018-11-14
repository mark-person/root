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
		System.out.println("-------------------001");
		DbDoc doc = super.fetchOne("test", "name = ?", "abcSabc");
		System.out.println("...........doc:" + doc);
		
		System.out.println("-------------------002");
		
		
//		var map = new HashMap<String, Object>();
//		map.put("id", "haha");
//		super.add("test", map);
		
		
		
		
		
		
	}
}
