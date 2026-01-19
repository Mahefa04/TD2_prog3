-- ENUMS
CREATE TYPE ingredient_category AS ENUM (
    'VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER'
);

CREATE TYPE dish_type AS ENUM (
    'START', 'MAIN', 'DESSERT'
);

-- TABLE Dish
CREATE TABLE dish (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    dish_type dish_type NOT NULL
);

-- TABLE Ingredient
CREATE TABLE ingredient (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    price NUMERIC NOT NULL,
    category ingredient_category NOT NULL,
    id_dish INT,
    FOREIGN KEY (id_dish) REFERENCES dish(id) ON DELETE SET NULL
);
