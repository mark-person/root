package com.ppx.cloud.monitor.output;

import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ppx.cloud.common.exception.ErrorBean;
import com.ppx.cloud.common.exception.ErrorCode;
import com.ppx.cloud.common.jdbc.nosql.LogTemplate;
import com.ppx.cloud.common.jdbc.nosql.Update;
import com.ppx.cloud.common.util.ApplicationUtils;
import com.ppx.cloud.common.util.DateUtils;
import com.ppx.cloud.monitor.cache.MonitorCache;
import com.ppx.cloud.monitor.cache.SqlPojo;
import com.ppx.cloud.monitor.cache.UriPojo;
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
@Service
public class PersistenceImpl {

	private static final String COL_START = "start";

	private static final String COL_CONF = "conf";

	private static final String COL_GATHER = "gather";

	private static final String COL_SERVICE = "service";

	private static final String COL_ACCESS = "access";

	private static final String COL_URI_STAT = "uri_stat";

	private static final String COL_SQL_STAT = "sql_stat";
	
	private static final String COL_DEBUG = "debug";
	
	private static final String COL_WARNING = "warning";
	
	private static final String COL_ERROR = "error";
	
	private static final String COL_ERROR_DETAIL = "error_detail";
	
	

	public static void insertStart(Map<String, Object> serviceInfo, Map<String, Object> config,
			Map<String, Object> startInfo) {
		try (LogTemplate t = new LogTemplate()) {
			t.addOrReplaceOne(COL_SERVICE, ApplicationUtils.getServiceId(), serviceInfo);
			t.addOrReplaceOne(COL_CONF, ApplicationUtils.getServiceId(), config);
			t.addOrReplaceOne(COL_START, ApplicationUtils.getServiceId(), startInfo);
		}
	}

	public static void insertGather(Map<String, Object> gatherMap, Map<String, Object> lastUpdate) {
		try (LogTemplate t = new LogTemplate()) {
			t.addOrReplaceOne(COL_GATHER, ApplicationUtils.getServiceId(), gatherMap);
			t.addOrReplaceOne(COL_SERVICE, ApplicationUtils.getServiceId(), lastUpdate);
		}
	}

	public static void insertAccess(AccessEntity entity) {
		String dateStr = new SimpleDateFormat(DateUtils.DATE_PATTERN).format(entity.getB());
		try (LogTemplate t = new LogTemplate()) {
			t.add(COL_ACCESS + dateStr, entity);
		}
	}

	private static Map<String, Object> getUriMaxDetail(AccessLog a) {
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

	public static void insertUriStat(AccessLog a) {
		int spendTime = (int) (a.getSpendNanoTime() / 1e6);
		Update update = new Update(COL_URI_STAT, a.getUri());
		update.inc("times", 1);
		update.inc("totalTime", spendTime);
		update.max("maxTime", spendTime);
		update.setOnInsert("firsted", a.getBeginTime());
		update.set("lasted", a.getBeginTime());
		distribute(update, spendTime);

		// maxTime, 缓存uri最大的maxTime值
		UriPojo uriPojo = MonitorCache.getUri(a.getUri());
		if (uriPojo != null && spendTime > uriPojo.getMaxTime()) {
			Map<String, Object> maxMap = getUriMaxDetail(a);
			update.max("maxTime", spendTime, "maxDetail", maxMap);
		}

		update.set("avgTime", "JSON_EXTRACT(doc,'$.totalTime') / JSON_EXTRACT(doc,'$.times')");

		try (LogTemplate t = new LogTemplate()) {
			t.sql(update);
		}
	}

	private static Map<String, Object> getSqlMaxDetail(AccessLog a, int i) {
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

	public static void insertSqlStat(AccessLog a) {

		if (a.getSqlList().size() != a.getSqlBeginTime().size()
				|| a.getSqlList().size() != a.getSqlSpendTime().size()) {
			return;
		}

		// 一个请求对应多个sql
		for (int i = 0; i < a.getSqlList().size(); i++) {
			// sql执行异常时，长度不一样
			Update update = new Update(COL_SQL_STAT, a.getSqlList().get(i));
			update.inc("times", 1);

			// sql开始执行时间
			long sqlBeginTime = a.getSqlBeginTime().get(i);
			update.setOnInsert("firsted", sqlBeginTime);
			update.set("lasted", sqlBeginTime);

			// sql执行时间
			int spendTime = a.getSqlSpendTime().get(i);
			update.inc("totalTime", spendTime);
			update.max("maxTime", spendTime);
			distribute(update, spendTime);
			update.max("maxSqlCount", a.getSqlCount().get(i));
			// uri
			update.addToSet("uri", a.getUri());
			update.set("avgTime", "JSON_EXTRACT(doc,'$.totalTime') / JSON_EXTRACT(doc,'$.times')");

			// maxTime, 缓存uri最大的maxTime值
			SqlPojo sqlPojo = MonitorCache.getSqlPojo(a.getSqlList().get(i));
			if (sqlPojo != null && spendTime > sqlPojo.getMaxTime()) {
				Map<String, Object> maxMap = getSqlMaxDetail(a, i);
				update.max("maxTime", spendTime, "maxDetail", maxMap);
			}
			try (LogTemplate t = new LogTemplate()) {
				t.sql(update);
			}
		}
		
	}
	
	public void insertDebug(DebugEntity debugAccess) {
		try (LogTemplate t = new LogTemplate()) {
			t.add(COL_DEBUG, debugAccess);
		}
	}
	
	  public void insertResponse(AccessLog a, String _id) {
//	        String hh = dateHhFormat.format(objectId.getDate());
//	        
//	        // 有异常和静态uri不统计
//	        // 机器ID yyyyMMddHH小时 访问量 总时间
//	        Criteria criteria = Criteria.where("sid")
//	                .is(ApplicationUtils.getServiceId()).and("hh").is(hh);
//	        Query query = Query.query(criteria);
//	        
//	        Update update = new Update();
//	        update.inc("times", 1);
//	        update.inc("totalTime", a.getSpendNanoTime() / 1000000);      
//	        
//	        // 插入或更新数据
//	        Map<?, ?> map = mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().upsert(true).returnNew(true),
//	                Map.class, COL_RESPONSE);
//	        
//	        // 更新平均值
//	        Update newUpdate = new Update();
//	        long avgTime = (Long)map.get("totalTime") / (Integer)map.get("times");
//	        newUpdate.set("avgTime", avgTime);
//	        mongoTemplate.updateFirst(query, newUpdate, COL_RESPONSE);
//	        
//	        // 更新到service
//	        upsertService(ApplicationUtils.getServiceId(), Update.update("lastResponse", avgTime));
	    }
	  
	  public void insertError(ErrorEntity errorEntity, Throwable throwable, DebugEntity debug) {
	        ErrorBean errorBean = ErrorCode.getErroCode(throwable);
	        errorEntity.setC(errorBean.getCode());
	        
	        // 类型为IGNORE_ERROR的异常，打印输入，一般不需要修改代码，不打印详情
	        if (errorBean.getCode() == ErrorCode.IGNORE_ERROR) {
	            errorEntity.setP(debug.getP());
	            errorEntity.setIn(debug.getIn());
	            try (LogTemplate t = new LogTemplate()) {
	    			t.add(COL_ERROR, errorEntity);
	    		}
	        }
	        else {
	        	try (LogTemplate t = new LogTemplate()) {
	    			t.add(COL_ERROR, errorEntity);
	    		}
	            
	            Map<String, Object> detailErrorMap = new HashMap<String, Object>();
	            detailErrorMap.put("_id", errorEntity.get_id());
	            detailErrorMap.put("exception", MonitorUtils.getExcepiton(throwable));
	            detailErrorMap.put("debug", debug.toJsonObject());
	            try (LogTemplate t = new LogTemplate()) {
	    			t.add(COL_ERROR_DETAIL, errorEntity);
	    		}
	        }
	    }
	  
	public void insertWarning(AccessLog a, BitSet content) {
//		Criteria criteria = Criteria.where("sid").is(ApplicationUtils.getServiceId()).and("uri").is(a.getUri());
//		Update update = Update.update("lasted", a.getBeginTime());
//		
//		
//		update.setOnInsert("beginTime", a.getBeginTime());
//		update.bitwise("content").or(content.toLongArray()[0]);
//		mongoTemplate.upsert(Query.query(criteria), update, COL_WARNING);
//		
//		try (LogTemplate t = new LogTemplate()) {
//			
//		}
		
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
//    public Map<?, ?> getConfig(String serviceId){
//        Criteria criteria = Criteria.where("_id").is(serviceId);
//        return mongoTemplate.findOne(Query.query(criteria), Map.class, COL_CONIFG);
//    }
//    


//    
//    public void insertGather(Map<String, Object> map) {    
//        mongoTemplate.insert(map, COL_GATHER);
//    }
//    

//    
//    private static String lastIndexDate = "";
//    // 创建access索引 
	public static void createAccessIndex(AccessEntity accessEntity) {

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
	public static void createFixedIndex() {
		try (LogTemplate t = new LogTemplate()) {
			t.createCollection(COL_URI_STAT);
		}
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

	private static void distribute(Update update, long t) {
		if (t < 10) {
			update.inc("ms0_10", 1);
		} else if (t < 100) {
			update.inc("ms10_100", 1);
		} else if (t < 1000) {
			update.inc("ms100_s1", 1);
		} else if (t < 3000) {
			update.inc("s1_3", 1);
		} else if (t < 10000) {
			update.inc("s3_10", 1);
		} else {
			update.inc("s10_", 1);
		}
	}

}
