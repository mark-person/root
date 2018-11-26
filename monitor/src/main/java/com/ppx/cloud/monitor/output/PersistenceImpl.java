package com.ppx.cloud.monitor.output;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ppx.cloud.common.jdbc.nosql.LogTemplate;
import com.ppx.cloud.common.jdbc.nosql.Update;
import com.ppx.cloud.common.util.ApplicationUtils;
import com.ppx.cloud.common.util.DateUtils;
import com.ppx.cloud.monitor.cache.MonitorCache;
import com.ppx.cloud.monitor.cache.UriPojo;
import com.ppx.cloud.monitor.persistence.AccessEntity;
import com.ppx.cloud.monitor.pojo.AccessLog;

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
	
	public void insertUriStat(AccessLog a) {
		int spendTime = (int) (a.getSpendNanoTime() / 1e6);
		Update update = new Update(COL_URI_STAT, a.getUri());
		update.inc("times", 1);
		update.inc("totalTime", spendTime);
		update.max("maxTime", spendTime);
		update.setOnInsert("firsted", a.getBeginTime());
		update.set("lasted", a.getBeginTime());
		distribute(update, spendTime);
		
		// maxTime, 缓存uri最大值
		UriPojo uriPojo = MonitorCache.getUri(a.getUri());
		if (uriPojo != null && spendTime > uriPojo.getMaxTime()) {
			Map<String, Object> maxMap = getUriMaxDetail(a);
		}

		try (LogTemplate t = new LogTemplate()) {
			t.sql(update);
		}
		
		// 1.平均值用update搞定(Integer) map.get("totalTime") / (Integer) map.get("times"), 2.缓存uri最大值
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
//    public void insertStartLog(Map<String, Object> map) {       
//        mongoTemplate.insert(map, COL_START);
//    }
//    
//    public void insertAccess(AccessEntity entity) {
//        String dateStr = dateFormat.format(entity.get_id().getDate());
//        mongoTemplate.insert(entity, COL_ACCESS + dateStr);        
//    }
//    
//    public void insertError(ErrorEntity errorEntity, Throwable t, DebugEntity debug) {
//        ErrorBean errorBean = ErrorCode.getErroCode(t);
//        errorEntity.setC(errorBean.getCode());
//        
//        // 类型为IGNORE_ERROR的异常，打印输入，一般不需要修改代码，不打印详情
//        if (errorBean.getCode() == ErrorCode.IGNORE_ERROR) {
//            errorEntity.setP(debug.getP());
//            errorEntity.setIn(debug.getIn());
//            mongoTemplate.insert(errorEntity, COL_ERROR);
//        }
//        else {
//            mongoTemplate.insert(errorEntity, COL_ERROR);
//            Map<String, Object> detailErrorMap = new HashMap<String, Object>();
//            detailErrorMap.put("_id", errorEntity.get_id());
//            detailErrorMap.put("exception", AccessUtils.getExcepiton(t));
//            detailErrorMap.put("debug", debug.toJsonObject());
//            mongoTemplate.insert(detailErrorMap, COL_ERROR_DETAIL);
//        }
//    }
//

//    
//    public void insertSqlStat(AccessLog a) {
//        
//        if (a.getSqlList().size() != a.getSqlBeginTime().size() || a.getSqlList().size() != a.getSqlSpendTime().size()) {
//            return;
//        }
//        
//        // 一个请求对应多个sql
//        for (int i = 0; i < a.getSqlList().size(); i++) {
//            // sql执行异常时，长度不一样
//            Query query = Query.query(Criteria.where("_id").is(a.getSqlList().get(i)));
//            
//            Update update = new Update();
//            update.inc("times", 1);
//            
//            
//            // sql开始执行时间
//            long sqlBeginTime = a.getSqlBeginTime().get(i);
//            update.setOnInsert("firsted", sqlBeginTime);            
//            update.set("lasted", sqlBeginTime);
//            
//            // sql执行时间
//            int spendTime = a.getSqlSpendTime().get(i);
//            update.inc("totalTime", spendTime);
//            update.max("maxTime", spendTime);
//            distribute(update, spendTime);
//            update.max("maxSqlCount", a.getSqlCount().get(i));
//            // uri
//            update.addToSet("uri", a.getUri());
//
//            // 插入或更新数据
//            Map<?, ?> map = mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().upsert(true).returnNew(true),
//                    Map.class, COL_SQL_STAT);
//            
//            // 更新平均值
//            Update newUpdate = Update.update("avgTime", (Integer)map.get("totalTime") / (Integer)map.get("times"));
//            // 最大时详情
//            if ((Integer)map.get("maxTime") >= spendTime) {
//                // 最大值对应对象
//                Map<String, Object> maxMap = new LinkedHashMap<String, Object>();
//                maxMap.put("sid", ApplicationUtils.getServiceId());
//                maxMap.put("uri", a.getUri());
//                if (a.getQueryString() != null) {
//                    maxMap.put("str", a.getQueryString());
//                }
//                maxMap.put("maxed", sqlBeginTime);
//                maxMap.put("sqlc", a.getSqlCount().get(i));
//                if (a.getSqlArgMap().get(i) != null) {
//                    maxMap.put("sqla", a.getSqlArgMap().get(i));
//                }
//                newUpdate.set("maxDetail", maxMap);     
//            }
//            mongoTemplate.updateFirst(query, newUpdate, COL_SQL_STAT);    
//        }
//    }
//    
//    public void insertResponse(AccessLog a, ObjectId objectId) {
//        String hh = dateHhFormat.format(objectId.getDate());
//        
//        // 有异常和静态uri不统计
//        // 机器ID yyyyMMddHH小时 访问量 总时间
//        Criteria criteria = Criteria.where("sid")
//                .is(ApplicationUtils.getServiceId()).and("hh").is(hh);
//        Query query = Query.query(criteria);
//        
//        Update update = new Update();
//        update.inc("times", 1);
//        update.inc("totalTime", a.getSpendNanoTime() / 1000000);      
//        
//        // 插入或更新数据
//        Map<?, ?> map = mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().upsert(true).returnNew(true),
//                Map.class, COL_RESPONSE);
//        
//        // 更新平均值
//        Update newUpdate = new Update();
//        long avgTime = (Long)map.get("totalTime") / (Integer)map.get("times");
//        newUpdate.set("avgTime", avgTime);
//        mongoTemplate.updateFirst(query, newUpdate, COL_RESPONSE);
//        
//        // 更新到service
//        upsertService(ApplicationUtils.getServiceId(), Update.update("lastResponse", avgTime));
//    }
//    
//    public void insertDebug(DebugEntity debugAccess) {
//        mongoTemplate.insert(debugAccess, COL_DEBUG);
//    }
//    
//    public void insertGather(Map<String, Object> map) {    
//        mongoTemplate.insert(map, COL_GATHER);
//    }
//    
//    public void insertWarning(AccessLog a, BitSet content) {
//        Criteria criteria = Criteria.where("sid").is(ApplicationUtils.getServiceId())
//                .and("uri").is(a.getUri());
//        Update update = Update.update("lasted", a.getBeginTime());
//        update.setOnInsert("beginTime", a.getBeginTime());
//        update.bitwise("content").or(content.toLongArray()[0]);
//        mongoTemplate.upsert(Query.query(criteria), update, COL_WARNING);
//    }
//    
//    private static String lastIndexDate = "";
//    // 创建access索引 
//    public void createAccessIndex(ObjectId objectId) {
//        String today = dateFormat.format(objectId.getDate());
//        if (!lastIndexDate.equals(today)) {
//            // access索引
//            IndexOperations accessOp = mongoTemplate.indexOps(COL_ACCESS + today);
//            accessOp.ensureIndex(new Index().on("sid", Direction.DESC));
//            accessOp.ensureIndex(new Index().on("b", Direction.DESC));
//            accessOp.ensureIndex(new Index().on("uri", Direction.DESC));
//            accessOp.ensureIndex(new Index().on("s", Direction.DESC));
//            accessOp.ensureIndex(new Index().on("marker", Direction.DESC));
//            lastIndexDate = today;
//        }
//    }
//    
//    // 创建(不包括access_yyyy-MM-dd)
//    public void createFixedIndex() {     
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
//    }
//    
	private void distribute(Update update, long t) {
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
