
create table config_service (
	service_id 		varchar(32) not null,
	artifact_id		varchar(32) not null,
	service_status	tinyint not null default 1 comment '1:使用 0:禁用',
	service_desc	varchar(32),
	primary key (service_id)
);

create table config_value (
	config_name 		varchar(64) not null comment '每个名称对应一个ConfigExec的实现类',
	artifact_id			varchar(32) not null,
	config_value		json comment '[{"key":"KEY", "value":"VALUE"}]',
	config_desc			varchar(64),
	primary key (config_name)
);

INSERT INTO `config_value` (`config_name`,`config_module`,`config_value`,`config_desc`) 
	VALUES ('AUTH','auth','{\"JWT_PASSWORD\": \"JWTPASSWORD\", \"ADMIN_PASSWORD\": \"ee70e58531bc525d5b818be90357faa4\", \"JWT_VALIDATE_SECOND\": 86400}','明文:admin');
INSERT INTO `config_value` (`config_name`,`config_module`,`config_value`,`config_desc`) 
	VALUES ('MONITOR_SWITCH','monitor','{\"IS_DEBUG\": false, \"IS_WARNING\": false}',NULL);
INSERT INTO `config_value` (`config_name`,`config_module`,`config_value`,`config_desc`) 
	VALUES ('MONITOR_THRESHOLD','monitor','{\"MAX_CPU_DUMP\": 0.9, \"DUMP_MAX_TIME\": 5000, \"GATHER_INTERVAL\": 300000, \"MAX_MEMORY_DUMP\": 0.9}',NULL);


create table config_exec_result (
	config_name		varchar(64) not null,
	service_id 		varchar(32) not null,
	exec_result 	tinyint not null default 1 comment '0:失败,1:成功',
 	exec_desc 	 	varchar(128),
	primary key (config_name, service_id)
);









