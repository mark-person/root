
#
根据refer和error=1 参数错误，来打印日志

#



# 返回值 
返回码说明
40001	invalid credential	不合法的调用凭证 
40002	invalid grant_type	不合法的grant_type
40039	invalid url size	不合法的url长度
45005	url size out of limit	url参数长度超过限制



返回的JSON数据包
errcode number错误码
errmsg string错误信息
-1 系统繁忙，此时请开发者稍候再试
0 请求成功
40029 code无效
45011 频率限制，每个用户每分钟100次




