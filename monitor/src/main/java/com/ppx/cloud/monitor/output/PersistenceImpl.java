package com.ppx.cloud.monitor.output;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.thymeleaf.expression.Maps;

import com.mysql.cj.xdevapi.Row;
import com.mysql.cj.xdevapi.SqlResult;
import com.ppx.cloud.common.exception.ErrorBean;
import com.ppx.cloud.common.exception.ErrorCode;
import com.ppx.cloud.common.jdbc.nosql.LogTemplate;
import com.ppx.cloud.common.jdbc.nosql.MyUpdate;
import com.ppx.cloud.common.util.ApplicationUtils;
import com.ppx.cloud.common.util.DateUtils;
import com.ppx.cloud.common.util.MD5Utils;
import com.ppx.cloud.monitor.cache.MonitorCache;
import com.ppx.cloud.monitor.cache.SqlPojo;
import com.ppx.cloud.monitor.cache.UriPojo;
import com.ppx.cloud.monitor.config.MonitorConfig;
import com.ppx.cloud.monitor.persistence.AccessEntity;
import com.ppx.cloud.monitor.pojo.AccessLog;
import com.ppx.cloud.monitor.pojo.DebugEntity;
import com.ppx.cloud.monitor.pojo.ErrorEntity;
import com.ppx.cloud.monitor.util.MonitorUtils;

/**
 * 
 * @author mark
 * @date 2018年11月21日
 */
public class PersistenceImpl extends PersistenceSupport {
	
	private static Logger logger = LoggerFactory.getLogger(PersistenceImpl.class);

	
	private LogTemplate t;

	public static PersistenceImpl getInstance(LogTemplate t) {
		PersistenceImpl impl = new PersistenceImpl();
		impl.t = t;
		return impl;
	}

	public void insertStart(Map<String, Object> serviceInfo,  Map<String, Object> startInfo) {
		MyUpdate updateSql = MyUpdate.getInstance(true, TABLE_SERVICE, "serviceId", ApplicationUtils.getServiceId());
		updateSql.setJson("serviceInfo", serviceInfo);
		updateSql.execute(t);
		
		t.add(COL_START, startInfo);
		
		MyUpdate confUpdate = MyUpdate.getInstance(true, TABLE_CONF, "serviceId", ApplicationUtils.getServiceId());
        if (MonitorConfig.IS_DEV) {
        	confUpdate.set("isDebug", 1);
        	confUpdate.set("isWarning", 1);
        }
        else {
        	confUpdate.set("isDebug", 0);
        	confUpdate.set("isWarning", 0);
        }
        confUpdate.set("gatherInterval", MonitorConfig.GATHER_INTERVAL);
        confUpdate.set("dumpMaxTime", MonitorConfig.DUMP_MAX_TIME);
        confUpdate.set("modified", new Date());
        confUpdate.execute(t);
	}

	public void insertGather(Map<String, Object> gatherMap, Map<String, Object> lastUpdate) {
		t.add(COL_GATHER, gatherMap);
		
		//updateSql
		MyUpdate updateSql = MyUpdate.getInstance(false, TABLE_SERVICE, "serviceId", ApplicationUtils.getServiceId());
		updateSql.setJson("serviceLastInfo", lastUpdate);
		updateSql.execute(t);
	}

	// 返回accessId
	public int insertAccess(AccessLog a) {
		String[] timeStr = new SimpleDateFormat(DateUtils.TIME_PATTERN).format(a.getBeginTime()).split(" ");
		 
		
		long useMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
        
		var info = Map.of("ip", a.getIp(), "q", a.getQueryString(), "mem", useMemory, "uid", -1);
		
		List<Object> bindValue = Arrays.asList(timeStr[0], timeStr[1], ApplicationUtils.getServiceId(), a.getSpendTime(), info);
		String sql = "insert into access(accessDate, accessTime, serviceId, spendTime, info) values(?, ?, ?, ?, ?)";
		t.sql(sql, bindValue);
		
		int accessId = getLastInsertId(t);
		
		return accessId;
	}

	private Map<String, Object> getUriMaxDetail(AccessLog a) {
		// uri最大请求时间对应对象
		var maxMap = new LinkedHashMap<String, Object>(8);
		maxMap.put("sid", ApplicationUtils.getServiceId());
		if (a.getQueryString() != null) {
			maxMap.put("str", a.getQueryString());
		}
		maxMap.put("maxed", a.getBeginTime());
		if (!a.getSqlList().isEmpty()) {
			maxMap.put("sql", a.getSqlList());
			maxMap.put("sqls", StringUtils.collectionToCommaDelimitedString(a.getSqlSpendTime()));
			maxMap.put("sqlc", StringUtils.collectionToCommaDelimitedString(a.getSqlCount()));

			StringBuilder sqla = new StringBuilder();
			if (!a.getSqlArgMap().isEmpty()) {
				a.getSqlArgMap().forEach((k, v) -> {
					sqla.append(v.toString());
				});
				maxMap.put("sqla", sqla.toString());
			}
		}
		return maxMap;
	}

	public void insertStatUri(AccessLog a) {

		MyUpdate update = MyUpdate.getInstanceSql(true, TABLE_STAT_URI, "uriSeq", "(select uriSeq from map_uri_seq where uriText = '" + a.getUri() + "')");
		int spendTime = (int) a.getSpendTime();
		update.inc("times", 1);
		update.inc("totalTime", a.getSpendTime());
		update.setSql("avgTime", "totalTime/times");
		update.max("maxTime", a.getSpendTime());
		update.setOnInsert("firsted", a.getBeginTime());
		update.set("lasted",a.getBeginTime());
		distribute(update, a.getSpendTime());

		// maxTime, 缓存uri最大的maxTime值
		UriPojo uriPojo = MonitorCache.getUri(a.getUri());
		if (uriPojo == null || spendTime > uriPojo.getMaxTime()) {
			Map<String, Object> maxMap = getUriMaxDetail(a);
			update.max("maxTime", spendTime, "maxDetail", maxMap);
			if (uriPojo != null) {
				uriPojo.setMaxTime(spendTime);
			}
		}

		t.sql("insert into map_uri_seq(uriText) select '" + a.getUri()
				+ "' from dual where not exists(select 1 from map_uri_seq where uriText = '" + a.getUri() + "')");
		update.execute(t);

	}

	private Map<String, Object> getSqlMaxDetail(AccessLog a, int i) {
		// 最大值对应对象
		Map<String, Object> maxMap = new LinkedHashMap<String, Object>();
		maxMap.put("sid", ApplicationUtils.getServiceId());
		maxMap.put("uri", a.getUri());
		if (a.getQueryString() != null) {
			maxMap.put("str", a.getQueryString());
		}
		maxMap.put("maxed", a.getSqlBeginTime().get(i));
		maxMap.put("sqlc", a.getSqlCount().get(i));
		if (a.getSqlArgMap().get(i) != null) {
			maxMap.put("sqla", a.getSqlArgMap().get(i));
		}
		return maxMap;
	}

	public void insertStatSql(AccessLog a) {
		if (a.getSqlList().size() != a.getSqlBeginTime().size()
				|| a.getSqlList().size() != a.getSqlSpendTime().size()) {
			logger.error("insertStatSql:{}", a);
			return;
		}

		// 一个请求对应多个sql
		for (int i = 0; i < a.getSqlList().size(); i++) {

			String sqlText = a.getSqlList().get(i);
			String sqlMd5 = sqlText;
			if (sqlText.length() != 32) {
				sqlMd5 = MD5Utils.getMD5(sqlText);
			}
			// sql执行异常时，长度不一样
			MyUpdate update = MyUpdate.getInstance(true, TABLE_STAT_SQL, "sqlMd5", sqlMd5);
			update.inc("times", 1);

			// sql开始执行时间
			update.setOnInsert("firsted", a.getBeginTime());
			update.set("lasted", a.getSqlBeginTime().get(i));

			// sql执行时间
			int spendTime = a.getSqlSpendTime().get(i);
			update.inc("totalTime", spendTime);
			update.max("maxTime", spendTime);
			distribute(update, spendTime);
			update.max("maxSqlCount", a.getSqlCount().get(i));
			// uri
			update.addToSet("uri", a.getUri());
			update.setSql("avgTime", "totalTime/times");

			// maxTime, 缓存uri最大的maxTime值
			SqlPojo sqlPojo = MonitorCache.getSqlPojo(a.getSqlList().get(i));
			if (sqlPojo == null || spendTime > sqlPojo.getMaxTime()) {
				Map<String, Object> maxMap = getSqlMaxDetail(a, i);
				update.max("maxTime", spendTime, "maxDetail", maxMap);

				if (sqlPojo != null) {
					sqlPojo.setMaxTime(spendTime);
				}
			}
			t.sql("insert into map_sql_md5(sqlMd5, sqlText) select ?, ? from dual where not exists(select 1 from map_sql_md5 where sqlMd5 = ?)",
					Arrays.asList(sqlMd5, sqlText, sqlMd5));
			update.execute(t);

		}

	}

	public void insertDebug(DebugEntity debugAccess) {
		t.add(COL_DEBUG, debugAccess);

	}

	public void insertResponse(AccessLog a) {
		// 机器ID yyyyMMddHH小时 访问量 总时间
		String hh = new SimpleDateFormat("yyyyMMddHH").format(a.getBeginTime());

		MyUpdate update = MyUpdate.getInstance(true, TABLE_STAT_RESPONSE, "serviceId,hh", ApplicationUtils.getServiceId(), hh);
		update.inc("times", 1);
		update.inc("totalTime", a.getSpendTime());
		update.setSql("avgTime", "totalTime/times");
		update.max("maxTime", a.getSpendTime());
		update.execute(t);

	}

	public void insertError(ErrorEntity errorEntity, Throwable throwable, DebugEntity debug) {
		ErrorBean errorBean = ErrorCode.getErroCode(throwable);
		errorEntity.setC(errorBean.getCode());

		// 类型为IGNORE_ERROR的异常，打印输入，一般不需要修改代码，不打印详情
		if (errorBean.getCode() == ErrorCode.IGNORE_ERROR) {
			errorEntity.setP(debug.getP());
			errorEntity.setIn(debug.getIn());
			t.add(COL_ERROR, errorEntity);

		} else {
			t.add(COL_ERROR, errorEntity);
			Map<String, Object> detailErrorMap = new HashMap<String, Object>();
			detailErrorMap.put("_id", errorEntity.get_id());
			detailErrorMap.put("exception", MonitorUtils.getExcepiton(throwable));
			detailErrorMap.put("debug", debug.toJsonObject());
			t.add(COL_ERROR_DETAIL, errorEntity);
		}
	}

	public void insertWarning(AccessLog a, BitSet content) {
		MyUpdate update = MyUpdate.getInstance(true, TABLE_STAT_WARNING, "uri", a.getUri());
		update.setJson("lasted", a.getBeginTime());
		update.setSql("content", content.toLongArray()[0] + "", "content|" + content.toLongArray()[0]);
		update.execute(t);
	}

//    public void upsertService(String serviceId, Update update) {
//        Criteria criteria = Criteria.where("_id").is(serviceId);
//        mongoTemplate.upsert(Query.query(criteria), update, COL_SERVICE);
//    }
//    
//    public void upsertConfig(String serviceId, Update update) {
//        Criteria criteria = Criteria.where("_id").is(serviceId);
//        mongoTemplate.upsert(Query.query(criteria), update, COL_CONIFG);
//    }
//    
    public Row getConfig(String serviceId){
    	SqlResult sr = t.sql("select * from conf where serviceId = ?", Arrays.asList(serviceId));
    	return sr.fetchOne();
    }
    
	public void insertGather(Map<String, Object> map) {
		t.add(COL_GATHER, map);
	}
    

//    
//    private static String lastIndexDate = "";
//    // 创建access索引 
	public void createAccessIndex(AccessEntity accessEntity) {

//	        String today = dateFormat.format(objectId.getDate());
//	        if (!lastIndexDate.equals(today)) {
//	            // access索引
//	            IndexOperations accessOp = mongoTemplate.indexOps(COL_ACCESS + today);
//	            accessOp.ensureIndex(new Index().on("sid", Direction.DESC));
//	            accessOp.ensureIndex(new Index().on("b", Direction.DESC));
//	            accessOp.ensureIndex(new Index().on("uri", Direction.DESC));
//	            accessOp.ensureIndex(new Index().on("s", Direction.DESC));
//	            accessOp.ensureIndex(new Index().on("marker", Direction.DESC));
//	            lastIndexDate = today;
//	        }
	}

//    
//    // 创建(不包括access_yyyy-MM-dd)
	public void createFixedIndex() {
		
//        // error索引
//        IndexOperations errorOp = mongoTemplate.indexOps(COL_ERROR);
//        errorOp.ensureIndex(new Index().on("sId", Direction.DESC));
//        errorOp.ensureIndex(new Index().on("b", Direction.DESC));
//        errorOp.ensureIndex(new Index().on("c", Direction.DESC));
//        errorOp.ensureIndex(new Index().on("uri", Direction.DESC));
//        errorOp.ensureIndex(new Index().on("marker", Direction.DESC));
//        
//        // debug索引
//        IndexOperations debugOp = mongoTemplate.indexOps(COL_DEBUG);
//        debugOp.ensureIndex(new Index().on("sid", Direction.DESC));
//        debugOp.ensureIndex(new Index().on("b", Direction.DESC));
//        debugOp.ensureIndex(new Index().on("uri", Direction.DESC));
//        debugOp.ensureIndex(new Index().on("marker", Direction.DESC));
//    
//        // gather索引
//        IndexOperations gatherOp = mongoTemplate.indexOps(COL_GATHER);
//        gatherOp.ensureIndex(new Index().on("sid", Direction.DESC));
//        gatherOp.ensureIndex(new Index().on("created", Direction.DESC));
//        gatherOp.ensureIndex(new Index().on("isOver", Direction.DESC));
//        gatherOp.ensureIndex(new Index().on("requestInfo.maxProcessingTime", Direction.DESC));
//        
//        // uri_stat索引
//        IndexOperations uriOp = mongoTemplate.indexOps(COL_URI_STAT);
//        uriOp.ensureIndex(new Index().on("avgTime", Direction.DESC));
//        uriOp.ensureIndex(new Index().on("times", Direction.DESC));
//        uriOp.ensureIndex(new Index().on("lasted", Direction.DESC));
//        uriOp.ensureIndex(new Index().on("maxTime", Direction.DESC));
//        
//        // sql_stat索引
//        IndexOperations sqlOp = mongoTemplate.indexOps(COL_SQL_STAT);
//        sqlOp.ensureIndex(new Index().on("avgTime", Direction.DESC));
//        sqlOp.ensureIndex(new Index().on("maxTime", Direction.DESC));
//        sqlOp.ensureIndex(new Index().on("lasted", Direction.DESC));
//        sqlOp.ensureIndex(new Index().on("maxSqlCount", Direction.DESC));
//        sqlOp.ensureIndex(new Index().on("uri", Direction.DESC));
//        
//        // response索引
//        IndexOperations responseOp = mongoTemplate.indexOps(COL_RESPONSE);
//        responseOp.ensureIndex(new Index().on("sid", Direction.DESC));
//        responseOp.ensureIndex(new Index().on("hh", Direction.DESC));
//        responseOp.ensureIndex(new Index().on("avgTime", Direction.DESC));
//                
//        // warning索引
//        IndexOperations warningOp = mongoTemplate.indexOps(COL_WARNING);
//        Index warningCombine = new Index();
//        warningCombine.on("sid", Direction.DESC);
//        warningCombine.on("uri", Direction.DESC);
//        warningOp.ensureIndex(warningCombine);
//        warningOp.ensureIndex(new Index().on("lasted", Direction.DESC));
		
		
	}

	private static void distribute(MyUpdate update, int t) {
		if (t < 10) {
			// update.inc("ms0_10", 1);
			update.setSql("distribute", "'[1,0,0,0,0,0]'",
					"JSON_SET(distribute, '$[0]', JSON_EXTRACT(distribute, '$[0]') + 1)");
		} else if (t < 100) {
			// update.inc("ms10_100", 1);
			update.setSql("distribute", "'[0,1,0,0,0,0]'",
					"JSON_SET(distribute, '$[1]', JSON_EXTRACT(distribute, '$[1]') + 1)");
		} else if (t < 1000) {
			// update.inc("ms100_s1", 1);
			update.setSql("distribute", "'[0,0,1,0,0,0]'",
					"JSON_SET(distribute, '$[2]', JSON_EXTRACT(distribute, '$[2]') + 1)");
		} else if (t < 3000) {
			// update.inc("s1_3", 1);
			update.setSql("distribute", "'[0,0,0,1,0,0]'",
					"JSON_SET(distribute, '$[3]', JSON_EXTRACT(distribute, '$[3]') + 1)");
		} else if (t < 10000) {
			// update.inc("s3_10", 1);
			update.setSql("distribute", "'[0,0,0,0,1,0]'",
					"JSON_SET(distribute, '$[4]', JSON_EXTRACT(distribute, '$[4]') + 1)");
		} else {
			// update.inc("s10_", 1);
			update.setSql("distribute", "'[0,0,0,0,0,1]'",
					"JSON_SET(distribute, '$[5]', JSON_EXTRACT(distribute, '$[5]') + 1)");
		}
	}

}
