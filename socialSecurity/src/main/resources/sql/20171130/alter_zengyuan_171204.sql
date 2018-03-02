ALTER TABLE zengyuan add `send_email_date` DATETIM COMMENT '邮件发送时间';

ALTER TABLE jianyuan add `send_email_date` DATETIM Comment '邮件发送时间';

ALTER TABLE zengyuan add `is_export` tinyint default 0 COMMENT '导出状态：0、否1、是';

ALTER TABLE jianyuan add `is_export` tinyint default 0 COMMENT '导出状态：0、否1、是';