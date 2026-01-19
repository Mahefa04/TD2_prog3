ALTER TABLE ingredients
ADD COLUMN IF NOT EXISTS required_quantity DECIMAL(5,2) NULL;

UPDATE ingredients
SET required_quantity =
    CASE name
        WHEN 'Laitue'   THEN 1
        WHEN 'Tomate'   THEN 2
        WHEN 'Poulet'   THEN 0.5
        WHEN 'Chocolat' THEN NULL
        WHEN 'Beurre'   THEN NULL
        ELSE required_quantity
    END;


