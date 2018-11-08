
# 日志管理
### 日志管理解决的问题
* 跟项目解耦
* 为宕机或故障找不原因发愁
> * 不能根据cpu、内存、耗时，dump出相关信息
* 没有预防故障错
* 前面开发快，后面开发慢
* 未运行完的请求，找出最大的线程序，并打印请求和SQL信息
* 最慢的sql及其请求参数(包括返回值)
* 最慢的uri及其请求参数(json及非json)
* 出异常，能否通过ID定位到异常或使用用户和传参
* 日志标记
* 是否解耦(搞成jar包形式)
* 异常分类，部分异常(前端不合法的请求)不需要打印轨迹
* 支持分布式
* 动态控制debug和warnning
* 在线查看日志
* 响应统计，并发数统计
* 各种警告，sql注入，不加条件的sql,不关数据库连接等
* 良好的性能，字符串压缩，关联关系，利用mongodb特性

# 使用说明
1. 使用标记(可在访问日志、异常或debug)，异常会自动产生一个marker以便查询
```java
// SLF4J中的API
private static final Logger logger = LoggerFactory.getLogger(TestController.class);
logger.info(MarkerFactory.getMarker("M001"), "hello log");
```

# 日志collection说明(12个)
```
config:配置
start:启动日志
service:服务器信息
gather:采集日志
>>>>>统计
response:响应统计
sql_stat:sql:sql统计
uri_stat:uri统计
>>>>>关联,只能按日期查
access_yyyy-MM-dd:访问日志
error:错误日志
error_detail:错误日志详情
debug:调试日志
warning:警告日志
```

