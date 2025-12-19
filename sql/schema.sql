create type category as enum ('vegetable', 'animal', 'marine', 'dairy', 'other');

create type dish_type as enum ('start', 'main', 'dessert');

create table Dish (
    id serial primary key,
    name varchar(250) not null,
    dish_type dishType not null
);


create table Ingredient (
    id serial primary key,
    name varchar(250) not null,
    price numeric(10,2) not null,
    category category not null,
    id_dish int
);