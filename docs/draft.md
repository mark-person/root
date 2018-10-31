
~~~
1、查看是否安装mariadb
rpm -qa | grep mariadb
rpm -e --nodeps mariadb-libs-5.5.56-2.el7.x86_64


error:./mysqld: error while loading shared libraries: libnuma.so.1: cannot open shared object file: No such file or directory
yum -y install numactl

Default options are read from the following files in the given order:
/etc/my.cnf /etc/mysql/my.cnf /usr/local/mysql/etc/my.cnf ~/.my.cnf 

# 去掉目录
tar -xvf /home/ppx/ftp/mysql-8.0.13-linux-glibc2.12-x86_64.tar.xz --strip-components 1 -C /home/ppx/mysql


/home/ppx/mysql/bin/mysqld -I --user=root
#;Y3:e1:tbV=


/home/ppx/mysql/bin/mysqld -u root -p --basedir=/home/ppx/mysql/ --datadir=/home/ppx/mysql/data/


Can not perform keyring migration
/home/ppx/mysql/bin/mysqld --initialize --user=root --console --basedir=/home/ppx/mysql/ --datadir=/home/ppx/mysql/data/
 
[ERROR] [MY-011084] [Server] Keyring migration failed.






mysqld: Can not perform keyring migration : Invalid --keyring-migration-source option.
2018-10-31T03:56:08.825630Z 0 [System] [MY-010116] [Server] /home/ppx/mysql/bin/mysqld (mysqld 8.0.13) starting as process 1974
2018-10-31T03:56:08.829065Z 0 [ERROR] [MY-011084] [Server] Keyring migration failed.
2018-10-31T03:56:08.829092Z 0 [ERROR] [MY-010119] [Server] Aborting
2018-10-31T03:56:08.829330Z 0 [System] [MY-010910] [Server] /home/ppx/mysql/bin/mysqld: Shutdown complete (mysqld 8.0.13)  MySQL Community Server - GPL.









CentOS7使用yum安装MySQL8.0
https://www.cnblogs.com/hujiapeng/p/9124298.html

grep 'temporary password' /var/log/mysqld.log

AdcaBhf)=0Fw



~~~
