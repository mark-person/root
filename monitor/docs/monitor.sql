
create table access (
	accessId 	int not null auto_increment,
	accessDate 	date not null,
	accessTime	time not null,
	serviceId 	varchar(32) not null,
	uri 		varchar(128) not null,
	spendTime 	int not null,
	info 		json,
	primary key (accessId, accessDate)
)
partition by hash (dayofmonth(accessDate)) partitions 10

create table startup (
	startupId		int not null auto_increment,
	startupTime		datetime not null,
	serviceId		varchar(32) not null,
	info			json,
	primary key (startupId)
)


create table service (
	serviceId		varchar(32) NOT NULL,
	serviceInfo 	json,
	serviceLastInfo json,
	servicePrio 	tinyint NOT NULL default -1,
	serviceDisplay 	tinyint NOT NULL default 1,
	PRIMARY KEY (serviceId)
);

create table conf (
	serviceId 			 varchar(32) NOT NULL,
    isDebug 			 tinyint NOT NULL,
    isWarning 			 tinyint NOT NULL,
    gatherInterval 	 	 int NOT NULL, 
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




ip,uri,marker(另创建一张表)

























