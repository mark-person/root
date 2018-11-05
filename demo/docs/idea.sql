


CREATE TABLE `test` (
  `test_id` int(11) NOT NULL,
  `test_name` varchar(45) DEFAULT NULL,
  `test_date` date DEFAULT NULL,
  `test_time` timestamp NULL DEFAULT NULL,
  `test_value` decimal(7,2) DEFAULT NULL,
  PRIMARY KEY (`test_id`)
) 


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
  main_img_src	int not null comment '主图src',
  primary key (prod_id)
) comment='商品';

create table product_img
(
  prod_img_id	int not null auto_increment,
  prod_id 		int not null,
  pord_img_src	varchar(255) not null,
  pord_img_prio	smallint not null comment '显示优先级',
  primary key (prod_img_id)
) comment='商品图片(非主图)';




  





