-- auto Generated on 2018-02-27 17:56:25 
-- DROP TABLE IF EXISTS `u_permission`; 
CREATE TABLE u_permission(
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    `url` VARCHAR(50) NOT NULL DEFAULT '' COMMENT 'urlµÿ÷∑',
    `name` VARCHAR(50) NOT NULL DEFAULT '' COMMENT 'url√Ë ˆ',
    PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT 'u_permission';
