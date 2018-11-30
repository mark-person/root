create table service (
  service_id varchar(32) NOT NULL,
  service_info json,
  service_last_info json,
  PRIMARY KEY (service_id)
);

create table conf (
	service_id 			 varchar(32) NOT NULL,
    is_debug 			 tinyint NOT NULL,
    is_warning 			 tinyint NOT NULL,
    gather_interval 	 int NOT NULL, 
    dump_max_time 	     int NOT NULL,
    created 			 timestamp not null default current_timestamp,
    modified  			 timestamp not null default current_timestamp,
    PRIMARY KEY (service_id)
);






































create table map_uri_seq (
  uri_seq int(11) NOT NULL AUTO_INCREMENT,
  uri_text varchar(250) NOT NULL
  PRIMARY KEY (uri_seq)
);

ALTER TABLE  `map_uri_seq` 
ADD UNIQUE INDEX `idx_map_uri_text` (`uri_text` ASC) VISIBLE;

create table map_sql_md5 (
  sql_md5 varchar(32) NOT NULL,
  sql_text varchar(2048) DEFAULT NULL,
  PRIMARY KEY (sql_md5)
);

create table stat_uri (
	uri_seq int not null primary key,
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
	sql_md5 varchar(32) not null primary key,
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
    /** TODO 改成uri_seq*/
	uri varchar(250) not null,
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
)




