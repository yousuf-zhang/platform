-- 新增用户
CREATE USER 'platform'@'localhost' IDENTIFIED BY 'platform';
CREATE USER 'platform'@'%' IDENTIFIED BY 'platform';
-- 新增数据库
CREATE DATABASE  platform_data DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 授权
GRANT SELECT,INSERT,UPDATE,DELETE ON platform_data.* TO 'platform'@'localhost';
GRANT SELECT,INSERT,UPDATE,DELETE ON platform_data.* TO 'platform'@'%';
-- 生效
FLUSH PRIVILEGES;
