-- auto Generated on 2018-02-27 17:56:50 
-- DROP TABLE IF EXISTS `u_user`; 
CREATE TABLE u_user(
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    `nickname` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '�û��ǳ�',
    `account` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '����|��¼�ʺ�',
    `pswd` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '����',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'createTime',
    `last_login_time` DATETIME NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '����¼ʱ��',
    `enable` INTEGER(12) NOT NULL DEFAULT -1 COMMENT '0:��Ч��1:��ֹ��¼',
    PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'u_user';
