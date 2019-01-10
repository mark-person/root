
create table config_service (
	service_id 		varchar(32) not null,
	service_status	tinyint not null default 1 comment '1:使用 0:禁用',
	service_desc	varchar(32),
	primary key (service_id)
);

create table config_value (
	config_name 	varchar(64) not null comment '每个名称对应一个ConfigExec的实现类',
	config_module	varchar(32) not null,
	config_value	varchar(64),
	config_desc		varchar(64),
	primary key (config_name)
);

create table config_exec_result (
	config_name		varchar(64) not null,
	service_id 		varchar(32) not null,
	exec_result 	tinyint not null default 1 comment '0:失败,1:成功',
 	exec_desc 	 	varchar(128),
	primary key (config_name, service_id)
);









