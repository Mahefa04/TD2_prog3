-- 1. Création de la base
CREATE DATABASE mini_dish_db;

-- 2. Création de l'utilisateur
CREATE USER mini_dish_db_manager WITH PASSWORD 'password';

-- 3. Droits
GRANT ALL PRIVILEGES ON DATABASE mini_dish_db TO mini_dish_db_manager;
