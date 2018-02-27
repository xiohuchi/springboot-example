-- auto Generated on 2018-02-27 17:56:43 
-- DROP TABLE IF EXISTS `u_role_permission`; 
CREATE TABLE u_role_permission(
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    `rid` BIGINT NOT NULL DEFAULT -1 COMMENT '½ÇÉ«ID',
    `pid` BIGINT NOT NULL DEFAULT -1 COMMENT 'È¨ÏÞID',
    PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'u_role_permission';
