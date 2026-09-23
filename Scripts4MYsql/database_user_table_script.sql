create database stock_management_system;

use  stock_management_system;

create table users(
id int auto_increment primary key, 
username varchar(50) not null unique, 
password varchar(255) not null, 
email varchar(100) not null, 
user_type varchar(20) not null, 
reset_token varchar(255), 
token_expiry datetime);

insert into users (username, password, email, user_type)
values('admin', 'admin123', 'admin@example.com', 'admin');

create table if not exists products (
id int auto_increment primary key,
name varchar(100) not null, 
brand varchar(50), 
cost_price decimal(10, 2),
selling_price decimal(10, 2),
stock_quantity int, 
product_type varchar(20) not null);