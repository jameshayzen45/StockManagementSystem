create database stock_management_system;

use stock_management_system;

create table users(
id int auto_increment primary key, username varchar(50) not null unique,
password varchar(255) not null,
email varchar(100) not null, 
user_type varchar(20) not null, 
reset_token varchar(255), 
token_expiry datetime);


