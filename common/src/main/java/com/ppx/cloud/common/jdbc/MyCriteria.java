package com.ppx.cloud.common.jdbc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 * sql的where条件工具
 * @author mark
 * @date 2018年11月13日
 */
public class MyCriteria {
	
	private String pre;

	private List<Object> paraList = new ArrayList<Object>();
	private List<Object> preParaList = new ArrayList<Object>();
	private List<String> paraStr = new ArrayList<String>();

	public MyCriteria(String pre) {
		this.pre = pre;
	}

	public MyCriteria addAnd(String sql) {
		paraStr.add(sql);
		return this;
	}

	public MyCriteria addAnd(String sql, Object obj) {
		if (StringUtils.isEmpty(obj)) return this;
		
		if (sql.toLowerCase().contains(" in ")) {
			List<String> question = new ArrayList<String>();
			boolean isArray = obj.getClass().isArray();
			if (isArray) {
				Object[] o = (Object[])obj;
				for (int i = 0; i < o.length; i++) {
					question.add("?");
					paraList.add(o[i]);
				}
			}
			else {
				String[] p = obj.toString().split(",");
				for (int i = 0; i < p.length; i++) {
					question.add("?");
					paraList.add(p[i]);
				}
			}
			
			paraStr.add(sql.replace("?", "(" + StringUtils.collectionToCommaDelimitedString(question) + ")"));
		}
		else {
			paraStr.add(sql);
			paraList.add(obj);
		}
		
		return this;
	}

	public MyCriteria addAnd(String sql, Object obj, String afterStr) {
		if (StringUtils.isEmpty(obj)) return this;
		paraStr.add(sql);
		paraList.add(obj + afterStr);
		return this;
	}
	
	public MyCriteria addAnd(String sql, String preStr, Object obj, String afterStr) {
		if (StringUtils.isEmpty(obj)) return this;
		paraStr.add(sql);
		paraList.add(preStr + obj + afterStr);
		return this;
	}

	public String toString() {
		if (paraStr.size() == 0) return "";
		else return " " + pre + " " + StringUtils.collectionToDelimitedString(paraStr, " and ");
	}

	public List<Object> getParaList() {
		preParaList.addAll(paraList);
		return preParaList;
	}
	
	public MyCriteria addPara(Object object) {
		paraList.add(object);
		return this;
	}
	
	public MyCriteria addPrePara(Object object) {
		preParaList.add(object);
		return this;
	}
}
