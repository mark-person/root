

~~~
1.解压
tar -xvf mysql-8.0.13-linux-glibc2.12-x86_64.tar.xz

2.初始化数据库
./mysqld --initialize --console



/usr/local/mysql/bin/mysqld --initialize --user=root --basedir=/usr/local/mysql/ --datadir=/usr/local/mysql/data/

error:./mysqld: error while loading shared libraries: libnuma.so.1: cannot open shared object file: No such file or directory
yum -y install numactl

A temporary password is generated for root@localhost: ylAaiS,lc7fa

手动启动
./mysqld -u root -p

3.

10.开机自启 
# cp mysql.server /etc/init.d/mysql 
# chmod +x /etc/init.d/mysql 
11.注册服务 
# chkconfig --add mysql 
12.查看是否添加成功 
# chkconfig --list mysql 
--------------------- 



~~~