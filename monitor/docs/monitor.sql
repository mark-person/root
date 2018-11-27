

create table map_uri_seq (
  uri_seq int(11) NOT NULL AUTO_INCREMENT,
  uri_text varchar(250) NOT NULL,
  uri_times int(11) not null default 1,
  PRIMARY KEY (uri_seq)
)

create table map_sql_md5 (
  sql_md5 varchar(32) NOT NULL,
  sql_text varchar(2048) DEFAULT NULL,
  sql_times int(11) not null default 1,
  PRIMARY KEY (sql_md5)
) 

create table stat_uri (
	uri_seq int not null primary key,
	times int not null default 1,
    totalTime int not null default 1,
    maxTime int not null,
    firsted timestamp not null default current_timestamp,
    lasted timestamp not null default current_timestamp,
    maxDetail json,
    sql_set json
)


