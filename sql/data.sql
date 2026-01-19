INSERT INTO dish (name, dish_type) VALUES
('Salade fraîche', 'START'),
('Poulet grillé', 'MAIN'),
('Riz aux légumes', 'MAIN'),
('Gateau au chocolat', 'DESSERT'),
('Salade de fruits', 'DESSERT');

INSERT INTO ingredient (name, price, category, id_dish) VALUES
('Laitue', 800, 'VEGETABLE', 1),
('Tomate', 600, 'VEGETABLE', 1),
('Poulet', 4500, 'ANIMAL', 2),
('Chocolat', 3000, 'OTHER', 4),
('Beurre', 2500, 'DAIRY', 4);
