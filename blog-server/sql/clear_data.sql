-- 清空所有数据和表结构（执行后可重新运行 init.sql 获得最新表结构）
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS t_article_tag;
DROP TABLE IF EXISTS t_article_like;
DROP TABLE IF EXISTS t_comment;
DROP TABLE IF EXISTS t_article;
DROP TABLE IF EXISTS t_moment;
DROP TABLE IF EXISTS t_category;
DROP TABLE IF EXISTS t_tag;
DROP TABLE IF EXISTS t_user;
DROP TABLE IF EXISTS t_operation_log;
DROP TABLE IF EXISTS t_about;
SET FOREIGN_KEY_CHECKS = 1;
