

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



