CREATE TYPE movement_type AS ENUM ('IN', 'OUT');

CREATE TABLE stock_movement (
    id SERIAL PRIMARY KEY,
    id_ingredient INT NOT NULL REFERENCES ingredient(id),
    quantity NUMERIC NOT NULL,
    unit unit_type NOT NULL,
    type movement_type NOT NULL,
    creation_datetime TIMESTAMP NOT NULL
);