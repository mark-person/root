create table service (
  serviceId varchar(32) NOT NULL,
  serviceInfo json,
  serviceLastInfo json,
  servicePrio tinyint NOT NULL default -1,
  serviceDisplay tinyint NOT NULL default 1,
  PRIMARY KEY (serviceId)
);

create table conf (
	serviceId 			 varchar(32) NOT NULL,
    isDebug 			 tinyint NOT NULL,
    isWarning 			 tinyint NOT NULL,
    gatherInterval 	 int NOT NULL, 
    dumpMaxTime 	     int NOT NULL,
    created 			 timestamp not null default current_timestamp,
    modified  			 timestamp not null default current_timestamp,
    PRIMARY KEY (service_id)
);



create table map_uri_seq (
  uriSeq int(11) NOT NULL AUTO_INCREMENT,
  uriText varchar(250) NOT NULL,
  PRIMARY KEY (uri_seq)
);

ALTER TABLE  `map_uri_seq` 
ADD UNIQUE INDEX `idx_map_uri_text` (`uriText` ASC) VISIBLE;

create table map_sql_md5 (
  sqlMd5 varchar(32) NOT NULL,
  sqlText varchar(2048) DEFAULT NULL,
  PRIMARY KEY (sqlMd5)
);

create table stat_uri (
	uriSeq int not null primary key,
	times int not null default 1,
    totalTime int not null default 1,
    maxTime int not null,
    avgTime int not null,
    firsted timestamp not null default current_timestamp,
    lasted timestamp not null default current_timestamp,
    distribute json,
    maxDetail json
);

create table stat_sql (
	sqlMd5 varchar(32) not null primary key,
	times int not null default 1,
    totalTime int not null default 1,
    maxTime int not null,
    avgTime int not null,
    maxSqlCount int,
    firsted timestamp not null default current_timestamp,
    lasted timestamp not null default current_timestamp,
    distribute json,
    maxDetail json,
    uri json
);

create table stat_response (
	serviceId 	 varchar(32) NOT NULL,
	hh 			 varchar(10) NOT NULL,
	times int not null default 1,
    totalTime int not null default 1,
    maxTime int not null,
    avgTime int not null,
    primary key(service_id, hh)
);

create table stat_warning (
	uri varchar(250) not null primary key,
	firsted timestamp not null default current_timestamp,
    lasted timestamp not null default current_timestamp,
    content int
);




CREATE TABLE part_tab ( c1 int default NULL, c2 varchar(30) default NULL, c3 date default NULL) 
PARTITION BY RANGE (year(c3)) (PARTITION p0 VALUES LESS THAN (1995),  
PARTITION p1 VALUES LESS THAN (1996) , PARTITION p2 VALUES LESS THAN (1997) ,  
PARTITION p3 VALUES LESS THAN (1998) , PARTITION p4 VALUES LESS THAN (1999) ,  
PARTITION p5 VALUES LESS THAN (2000) , PARTITION p6 VALUES LESS THAN (2001) ,  
PARTITION p7 VALUES LESS THAN (2002) , PARTITION p8 VALUES LESS THAN (2003) ,  
PARTITION p9 VALUES LESS THAN (2004) , PARTITION p10 VALUES LESS THAN (2010),  
PARTITION p11 VALUES LESS THAN MAXVALUE );


CREATE TABLE tblist (
    id INT NOT NULL,
    store_id INT
)
PARTITION BY LIST(store_id) (
    PARTITION a VALUES IN (1,5,6),
    PARTITION b VALUES IN (2,7,8),
    PARTITION c VALUES IN (3,9,10),
    PARTITION d VALUES IN (4,11,12)
);
 CREATE TABLE `access5` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accessDate` date NOT NULL,
  `accessTime` time NOT NULL,
  `val` int not null,
  `title` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`,`accessDate`)
) ENGINE=InnoDB AUTO_INCREMENT=2 
 PARTITION BY HASH (dayofmonth(`accessDate`))
PARTITIONS 4

create table access (
	accessId int(10) not null auto_increment,
	accessDate date not null,
	accessTime time not null,
	
)



# 索引条件
服务
日期-时间
URI
标记

ip,uri,marker(另创建一张表)























