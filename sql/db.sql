Create database mini_dish_db;
\c mini_dish_db

create user mini_dish_db_manager with password '123456';

grant all privileges on database mini_dish_db to mini_dish_db_manager;