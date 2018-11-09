

# 查看Mysql实时执行的Sql语句

1.进入Mysql
2.启用Log功能(general_log=ON) SHOW VARIABLES LIKE "general_log%"; SET GLOBAL general_log = 'ON';
3.设置Log文件地址(所有Sql语句都会在general_log_file里) SET GLOBAL general_log_file = 'c:\mysql.log';
4.下载BareTail专门查看Log文件的绿色软件(提供免费版本仅220k)
5.执行mysql命令然后在BareTail里查看


' or '1' = 'test




