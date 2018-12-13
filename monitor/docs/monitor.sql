

create table access (
	accessId 	int not null auto_increment,
	accessDate 	date not null,
	accessTime	time not null,
	serviceId 	varchar(32) not null,
	uriSeq		int not null,
	spendTime 	int not null,
	accessInfo 		json,
	primary key (accessId, accessDate)
)
partition by hash (dayofmonth(accessDate)) partitions 10;

create table access_log (
	accessId int not null,
    marker varchar(16),
    log varchar(1024),
    primary key(accessId, marker)
);

create table startup (
	startupId		int not null auto_increment,
	startupTime		datetime not null,
	serviceId		varchar(32) not null,
	startupInfo			json,
	primary key (startupId)
);

create table service (
	serviceId		varchar(32) not NULL,
	serviceInfo 	json,
	serviceLastInfo json,
	servicePrio 	tinyint not null default -1,
	serviceDisplay 	tinyint not null default 1,
	primary key (serviceId)
);

create table conf (
	serviceId 			 varchar(32) not null,
    isDebug 			 tinyint not null,
    isWarning 			 tinyint not null,
    gatherInterval 	 	 int not null, 
    dumpMaxTime 	     int not null,
    created 			 timestamp not null default current_timestamp,
    modified  			 timestamp not null default current_timestamp,
    primary key (serviceId)
);

create table gather (
	serviceId 			varchar(32) not null,
	gatherTime 			datetime not null,
	isOver				tinyint not null default 0,
	maxProcessingTime 	int not null default 0,
	concurrentN			int not null default 0,
	gatherInfo				json,
	primary key (serviceId, gatherTime)
);

create table error (
	accessId 	int not null,
	serviceId 	varchar(32) not null,
	errorTime 	datetime not null,
	errorCode 	int not null default -1,
	errorMsg	varchar(1024),
	primary key (accessId)
);

create table error_detail (
	accessId 	int not null,
	errorDetail json,
	debugDetail json,
	primary key (accessId)
);

create table debug (
	accessId 	int not null,
	serviceId 	varchar(32) not null,
	debugTime	datetime not null,
	debugInfo		json,
	primary key (accessId)
);


create table map_uri_seq (
  uriSeq int(11) not null AUTO_INCREMENT,
  uriText varchar(250) not null,
  primary key (uriSeq)
);

alter table  map_uri_seq 
add unique index idx_map_uri_text (uriText ASC) VISIBLE;

create table map_sql_md5 (
  sqlMd5 varchar(32) not NULL,
  sqlText varchar(2048) default NULL,
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
	serviceId 	 varchar(32) not NULL,
	hh 			 varchar(10) not NULL,
	times int not null default 1,
    totalTime int not null,
    maxTime int not null,
    avgTime int not null,
    primary key(serviceId, hh)
);

create table stat_warning (
	uriSeq int not null primary key,
	firsted timestamp not null default current_timestamp,
    lasted timestamp not null default current_timestamp,
    content int,
    primary key(uriSeq)
);








