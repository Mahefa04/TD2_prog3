insert into Dish (id, name, dish_type) values
(1, 'Salade fraiche', 'start'),
(2, 'poulet grillé', 'main'),
(3, 'riz aux légumes', 'main'),
(4, 'gateau au chocolat', 'dessert'),
(5, 'saladede fruits', 'dessert');

insert into Ingredient (id, name, price, category, id_dish) values
(1, 'laitue', 800.00, 'vegetable', 1),
(2, 'tomate', 600.00, 'vegetable', 1),
(3, 'poulet', 4500.00, 'animal', 2),
(4, 'chocolat', 3000.00, 'other', 4),
(5, 'beurre', 2500.00, 'dairy', 4);
