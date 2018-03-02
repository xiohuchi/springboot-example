drop table if exists qingkuan;
create table if not exists qingkuan(
	id int(11) not null auto_increment primary key,
	supplier_id int(11) Comment '供应商',
	supplier_name varchar(50) default null comment '供应商名称',
	user_id int Comment '操作人',
	type TINYINT(4) DEFAULT '0' COMMENT '默认0:请款,1:垫付',
	state TINYINT(4) DEFAULT '0' COMMENT '0:待处理,1:已申请垫付,2:已垫付, 3:已请款',
	reporting_period VARCHAR(20) default null comment '月份',
	customer_name VARCHAR(32) NOT NULL COMMENT '客户名称',
	customer_id int(11) not null comment '客户ID',
	mount_total DOUBLE(8,2) default 0 COMMENT '合计',
	gerenshebao DOUBLE(8,2) default 0 Comment '个人社保',
	gongsishebao DOUBLE(8,2) default 0 Comment '公司社保',
	shebao_total DOUBLE(8,2) default 0 Comment '社保合计',
	gongsicanbaojin DOUBLE(8,2) default 0 Comment '公司残保金',
	gerengongjijin DOUBLE(8,2) default 0 Comment '个人公积金',
	gongsigongjijin DOUBLE(8,2) default 0 Comment '公司公积金',
	gongjijin_total DOUBLE(8,2) default 0 COMMENT '公积金',
	fuwufei_total DOUBLE(8,2) default 0 COMMENT '服务费',
	remark varchar(50) default null comment '备注',
	create_date date default null Comment '创建时间',
	payment_date  date default null Comment '款项回收时间'
);

truncate qingkuan;
insert into qingkuan values
(1, 64, '测试供应商', 4, 0, 0, '2017-12', '测试客户', 12, 500.00, 12.15, 18.10, 30.25, 30.05, 50.00, 50.00, 100.00, 20.00, '测试', now(), now()),
(2, 64, '测试供应商', 4, 1, 0, '2017-12', '测试客户', 12, 500.00, 12.15, 18.10, 30.25, 30.05, 50.00, 50.00, 100.00, 20.00, '测试', now(), now()),
(3, 64, '测试供应商', 4, 0, 0, '2017-11', '测试客户', 12, 500.00, 12.15, 18.10, 30.25, 30.05, 50.00, 50.00, 100.00, 20.00, '测试', now(), now());




-- 查询客户账单明细
SELECT q.customer_name customerName,
q.customer_id customerId,
q.reporting_period reportingPeriod,
SUM(q.mount_total) mountTotal,
SUM(q.gerenshebao) gerenshebao,
SUM(q.gongsishebao) gongsishebao,
SUM(q.shebao_total) shebao_total,
SUM(q.gongsicanbaojin) gongsicanbaojin,
SUM(q.gerengongjijin) gerengongjijin,
SUM(q.gongsigongjijin) gongsigongjijin,
SUM(q.gongjijin_total) gongjijinTotal,
SUM(q.fuwufei_total) fuwufeiTotal
FROM qingkuan q where q.type=1 and q.state=0 and q.reporting_period='2017-12'
GROUP BY q.customer_name, q.customer_id, q.reporting_period;



-- 查询供应商账单明细
select
q.type,
q.supplier_id,
q.supplier_name,
q.reporting_period,
sum(q.mount_total),
sum(q.gerenshebao),
sum(q.gongsishebao),
sum(q.gongsicanbaojin),
sum(q.gerengongjijin),
sum(q.gongsigongjijin),
sum(q.gongjijin_total),
sum(q.fuwufei_total)
from qingkuan q 
where q.state=0
group by 
q.type,
q.supplier_id,
q.supplier_name,
q.reporting_period




