
create table config_service (
	service_id 		varchar(32) not null,
	service_status	tinyint not null default 1 comment '1:使用 0:禁用',
	service_desc	varchar(32),
	primary key (service_id)
);

create table config_value (
	config_param	varchar(64) not null comment '变量或接口,接口以/开头',
	service_id 		varchar(32) not null,
	config_module	varchar(32) not null,
	config_value	varchar(64),
	config_desc 	varchar(64),
	config_status 	tinyint not null default 1 comment '0:待同步,1:完成',
	primary key (config_param, service_id)
);









