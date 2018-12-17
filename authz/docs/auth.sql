

create table auth_res (
	res_id int not null auto_increment,
    parent_id int not null,
    res_name varchar(32),
    res_prio int not null,
    res_type tinyint not null comment '0:目录1:菜单',
    uri_seq int comment '菜单uri_seq',
    primary key (res_id)
) comment='资源';

create table auth_uri_seq (
	uri_seq int not null auto_increment,
    uri_text varchar(64) not null,
    primary key (uri_seq)
) comment='URI系列';

alter table  auth_uri_seq add unique index idx_auth_uri_text (uri_text asc);


create table auth_res_uri (
	res_id int not null,
    uri_seq int not null,
    primary key (res_id, uri_seq)
) comment='资源对应多个URI';


/** ----------------- auth ----------------- */
/**
 * account_status 判断帐号有效性
 * modified 判断帐号或密码已经被修改，用来检验tocken的有效性
 * last_login 最后登录时间
 */
create table auth_account (
  account_id 		int(11) not null auto_increment,
  user_id 			int(11) not null,
  login_account 	varchar(32) not null,
  login_password 	varchar(32) not null,
  account_status 	tinyint(1) not null default 1 comment '0:删除 1:正常 -1:禁用', 
  created 			timestamp not null default current_timestamp,
  modified 			timestamp not null default current_timestamp,
  last_login		datetime,
  primary key (account_id)
) comment='帐号';

/** user_id继承auth_account.account_id */
create table auth_user (
  user_id 		int(11) not null,
  user_name 	varchar(32) not null,
  user_status 	tinyint(1) not null default 1 comment '0:删除 1:正常',
  created 		timestamp not null default current_timestamp,
  primary key (user_id)
) comment='用户';

/** index */
create index idx_auth_account_user_id on auth_account(user_id);
create unique index idx_auth_account_acc on auth_account(login_account);
create unique index idx_user_name on auth_user(user_name);


create table auth_grant (
	account_id 		int not null,
	res_id			int not null,
    primary key (account_id, res_id)
) comment='分配资源权限';

create table auth_cache (
  cache_type varchar(32) not null,
  all_version int(11) not null comment '所有权限缓存版本',
  grant_version int(11) not null comment '分配权限缓存版本',
  primary key (cache_type)
) comment='权限缓存';


















