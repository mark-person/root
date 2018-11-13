/**
 * 
 */
package com.ppx.cloud.common.jdbc.nosql;

import com.mysql.cj.xdevapi.Session;

/**
 * @author mark
 * @date 2018年11月13日
 */
public abstract class MyNosqlSupport {
	
	
	protected NosqlTemplate getNosqlTemplate() {
		Session session = MySessionUtils.getSession();
		
		return new NosqlTemplate(session);
	}
	
}	
