create table login_user
(
  user_id		int not null auto_increment,
  user_name 	varchar(32) not null,
  primary key (user_id)
) comment='用户';

create table category
(
  cat_id			int not null auto_increment,
  cat_name	 		varchar(32) not null,
  cat_prio			smallint not null comment '显示优先级',
  cat_status		tinyint not null default 1 comment '1:正常', 
  primary key (cat_id)
) comment='分类';

create table product
(
  prod_id		int not null auto_increment,
  prod_title	varchar(32) not null,
  cat_id		int not null,
  recommend 	tinyint not null comment '推荐星级',
  main_img_id	int not null comment '主图ID',
  primary key (prod_id)
) comment='商品';

create table product_img
(
  prod_img_id	int not null auto_increment,
  prod_id 		int not null,
  pord_img_src	varchar(255) not null,
  pord_img_prio	smallint not null comment '显示优先级',
  primary key (prod_img_id)
) comment='商品图片';




  





