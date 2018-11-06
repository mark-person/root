package com.ppx.cloud.demo.test;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ppx.cloud.common.jdbc.MyDaoSupport;
import com.ppx.cloud.common.page.Page;

@Service
public class TestServiceImpl extends MyDaoSupport {

	public List<TestPojo> list(Page page, TestPojo pojo) {

		// 默认排序，后面加上需要从页面传过来的排序的，防止SQL注入
		// page.addDefaultOrderName("test_id").addPermitOrderName("test_price").addUnique("test_id");

		// 分开两条sql，mysql在count还会执行子查询, 总数返回0将不执行下一句
		var c = createCriteria("where").addAnd("t.test_name like ?", "%", pojo.getTestName(), "%");
		 
		page.addDefaultOrderName("t.test_id").addPermitOrderName("t.test_name").addUnique("t.test_id");
		
		
		var cSql = new StringBuilder("select count(*) from test t").append(c);
		var qSql = new StringBuilder("select * from test t").append(c);
		
		List<TestPojo> list = queryPage(TestPojo.class, page, cSql, qSql, c.getParaList());
		return list;

	}

	public int test() {
		int c = getJdbcTemplate().queryForObject("select count(*) from test", Integer.class);

		return c;
	}
}
