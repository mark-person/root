package com.ppx.cloud.monitor.output;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.xdevapi.Column;
import com.mysql.cj.xdevapi.DbDoc;
import com.mysql.cj.xdevapi.DbDocImpl;
import com.mysql.cj.xdevapi.JsonNumber;
import com.mysql.cj.xdevapi.JsonString;
import com.mysql.cj.xdevapi.Row;
import com.mysql.cj.xdevapi.SqlResult;
import com.mysql.cj.xdevapi.Type;
import com.ppx.cloud.common.jdbc.nosql.LogTemplate;
import com.ppx.cloud.common.page.Page;

public class PersistenceSupport {

	
	protected int getLastInsertId(LogTemplate t) {
		return t.sql("select LAST_INSERT_ID()").fetchOne().getInt(0);
    }
	
	protected List<Map<String, Object>> queryTablePage(LogTemplate t, Page page, StringBuilder cSql, StringBuilder qSql,
			List<Object> paraList) {
		paraList = paraList == null ? new ArrayList<Object>() : paraList;
		int totalRows = t.sql(cSql.toString(), paraList).fetchOne().getInt(0);
		
		page.setTotalRows(totalRows);
		if (totalRows == 0) {
			return new ArrayList<Map<String, Object>>();
		}

		// order by
		if (!StringUtils.isEmpty(page.getOrderName())) {
			qSql.append(" order by ").append(page.getOrderName()).append(" ").append(page.getOrderType());
			// 数据库使用快速排序，防止分页的数据出现相同情况
			if (!Objects.equals(page.getOrderName(), page.getUnique()) && !StringUtils.isEmpty(page.getUnique())) {
				qSql.append(",").append(page.getUnique());
			}
		}

		qSql.append(" limit ?, ?");
		paraList.add((page.getPageNumber() - 1) * page.getPageSize());
		paraList.add(page.getPageSize());
		
		SqlResult sr = t.sql(qSql.toString(), paraList);
		
		List<Row> list = sr.fetchAll();
		
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		List<Column> colList = sr.getColumns();
		
	
		
		for (Row row : list) {
			returnList.add(getRowMap(row, colList));
		}
		return returnList;
	}
	
	private Map<String, Object> getRowMap(Row row, List<Column> colList) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (Column col : colList) {
			
			if (col.getType() == Type.INT) {
				map.put(col.getColumnName(), row.getInt(col.getColumnName()));
			}
			else if (col.getType() == Type.TIMESTAMP) {
				map.put(col.getColumnName(), row.getTimestamp(col.getColumnName()));
			}
			else if (col.getType() == Type.JSON) {
				var r = row.getString(col.getColumnName());
				// DbDoc不支持[]的json
				if (!StringUtils.isEmpty(r) && r.startsWith("[")) {
					map.put(col.getColumnName(), r);
				}
				else {
					DbDoc doc = row.getDbDoc(col.getColumnName());
					Map<String, Object> m = new LinkedHashMap<String, Object>();
					if (doc != null) {
						doc.forEach((k, v) -> {
							if (v instanceof JsonString) {
								m.put(k, ((JsonString)v).getString());
							}
							else if (v instanceof JsonNumber) {
								m.put(k, ((JsonNumber)v).getBigDecimal());
							}
							else {
								m.put(k, v.toString());
							}
						});
					}
					map.put(col.getColumnName(), m);
				}
			}
			else {
				map.put(col.getColumnName(), row.getString(col.getColumnName()));
			}
		}
		return map;
	}
	
	protected Map<String, Object> fetchOne(LogTemplate t, String sql, Object... obj) {
		SqlResult sr = t.sql(sql, obj);
		Row row = sr.fetchOne();
		List<Column> colList = sr.getColumns();
		return getRowMap(row, colList);
	}
	
	
}
