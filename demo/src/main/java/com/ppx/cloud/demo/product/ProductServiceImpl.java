package com.ppx.cloud.demo.product;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ppx.cloud.common.context.MyContext;
import com.ppx.cloud.common.jdbc.MyDaoSupport;
import com.ppx.cloud.common.page.Page;
import com.ppx.cloud.demo.test.Test;

@Service
public class ProductServiceImpl extends MyDaoSupport {
	
	public List<Test> list(Page page, Product pojo) {

		// 默认排序，后面加上需要从页面传过来的排序的，防止SQL注入
		// page.addDefaultOrderName("test_id").addPermitOrderName("test_price").addUnique("test_id");

		// 分开两条sql，mysql在count还会执行子查询, 总数返回0将不执行下一句
		var c = createCriteria("where").addAnd("t.prod_title like ?", "%", pojo.getProdTitle(), "%");
		 
		page.addDefaultOrderName("t.prod_id").addUnique("t.prod_id");
		
		
		var cSql = new StringBuilder("select count(*) from product t").append(c);
		var qSql = new StringBuilder("select * from product t").append(c);
		
		List<Test> list = queryPage(Test.class, page, cSql, qSql, c.getParaList());
		return list;
	}
	
}
