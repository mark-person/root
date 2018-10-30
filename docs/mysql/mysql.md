# windows

mysql-8.0.13-winx64.zip

1. 初始化数据库

~~~
mysqld --initialize --console


~~~


执行完成后，会打印 root 用户的初始默认密码

 A temporary password is generated for root@localhost: sOtjoMgeh1;H

2.创建my.ini

~~~

~~~

3.安装服务
 用管理员身份
 
~~~
mysqld --install mysql8
~~~


4. 

MySQL8.0.4以前MySQL的密码认证插件是“mysql_native_password”，而现在使用的是“caching_sha2_password”。
如果想默认使用“mysql_native_password”插件认证，可以在配置文件中配置default_authentication_plugin项。
[mysqld]
default_authentication_plugin=mysql_native_password
~~~
mysql -uroot -p


ALTER USER 'root'@'localhost' IDENTIFIED WITH caching_sha2_password BY 'dengppx123456';


ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'dengppx123456';
~~~


# centos
1、查看是否安装mariadb
~~~
rpm -qa | grep mariadb

rpm -e --nodeps mariadb-libs-5.5.52-1.el7.x86_64
~~~

安装mysql 依赖包

~~~
yum install libaio
~~~

tar -xzvf mysql-8.0.13-linux-glibc2.12-x86_64.tar.xz


https://blog.csdn.net/xintingandzhouyang/article/details/80956348

设置允许远程登陆：
mysql> use mysql
mysql> update user set user.Host='%'where user.User='root';
mysql> flush privileges;
禁用防火墙
systemctl stopfirewalld.service


