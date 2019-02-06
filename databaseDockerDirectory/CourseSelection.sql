CREATE DATABASE CourseSelectionDb CHARACTER SET utf8 COLLATE utf8_general_ci;
CREATE USER 'courseSelectionUser'@'%' IDENTIFIED BY '12345';
GRANT ALL PRIVILEGES ON CourseSelectionDb.* TO 'courseSelectionUser'@'%';
