# 新增供应商名称字段
alter table fulitao add supplier_name varchar(50) default null Comment '供应商名称' after supplier_id;