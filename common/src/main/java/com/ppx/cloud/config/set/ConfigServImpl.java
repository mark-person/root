package com.ppx.cloud.config.set;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ppx.cloud.common.jdbc.MyDaoSupport;
import com.ppx.cloud.common.page.Page;
import com.ppx.cloud.config.Config;

@Service
public class ConfigServImpl extends MyDaoSupport {
	
	public List<Config> list(Page page, Config config) {

		var cSql = new StringBuilder("select count(*) from config_value");
		var qSql = new StringBuilder("select * from config_value");
		
		List<Config> list = queryPage(Config.class, page, cSql, qSql);
		return list;
	}
	
   
    
    
}
