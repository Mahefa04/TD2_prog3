import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataRetrieverTest {

    private DataRetriever dataRetriever;

    @BeforeEach
    void setUp() {
        dataRetriever = new DataRetriever();
    }

    // a) Dish findDishById id=1
    @Test
    void testFindDishById_ok() throws Exception {
        Dish dish = dataRetriever.findDishById(1);
        assertEquals("Salade fraîche", dish.getName());
        assertEquals(2, dish.getIngredients

                ().size());
    }

    // b) Dish findDishById id=999
    @Test
    void testFindDishById_notFound() {
        assertThrows(RuntimeException.class, () -> {
            dataRetriever.findDishById(999);
        });
    }

    // c) findIngredients page=2 size=2 → Poulet, Chocolat
    @Test
    void testFindIngredients_page2_size2() throws Exception {
        List<Ingredient> list = dataRetriever.findIngredients(2, 2);
        assertEquals(2, list.size());
        assertEquals("Poulet", list.get(0).getName());
        assertEquals("Chocolat", list.get(1).getName());
    }

    // d) findIngredients page=3 size=5 → vide
    @Test
    void testFindIngredients_emptyPage() throws Exception {
        List<Ingredient> list = dataRetriever.findIngredients(3, 5);
        assertTrue(list.isEmpty());
    }

    // e) findDishsByIngredientName "eur" → Gâteau au chocolat
    @Test
    void testFindDishsByIngredientName() throws Exception {
        List<Dish> dishes = dataRetriever.findDishsByIngredientName("eur");
        assertEquals(1, dishes.size());
        assertEquals("Gateau au chocolat", dishes.get(0).getName());
    }

    // f) findIngredientsByCriteria category=VEGETABLE → Laitue, Tomate
    @Test
    void testFindIngredientsByCriteria_category() throws Exception {
        List<Ingredient> list = dataRetriever.findIngredientsByCriteria(
                null,
                CategoryEnum.VEGETABLE,
                null,
                1,
                10
        );

        assertEquals(2, list.size());
        assertEquals("Laitue", list.get(0).getName());
        assertEquals("Tomate", list.get(1).getName());
    }

    // g) ingredientName="cho", dishName="Sal" → vide
    @Test
    void testFindIngredientsByCriteria_empty() throws Exception {
        List<Ingredient> list = dataRetriever.findIngredientsByCriteria(
                "cho",
                null,
                "Sal",
                1,
                10
        );

        assertTrue(list.isEmpty());
    }

    // h) ingredientName="cho", dishName="gâteau" → Chocolat
    @Test
    void testFindIngredientsByCriteria_chocolat() throws Exception {
        List<Ingredient> list = dataRetriever.findIngredientsByCriteria(
                "cho",
                null,
                "gateau",
                1,
                10
        );

        assertEquals(1, list.size());
        assertEquals("Chocolat", list.get(0).getName());
    }

    // i) createIngredients OK
    @Test
    void testCreateIngredients_ok() throws Exception {
        Ingredient fromage = new Ingredient(0, "Fromage", 2000, CategoryEnum.DAIRY, null);
        Ingredient oignon = new Ingredient(0, "Oignon", 500, CategoryEnum.VEGETABLE, null);

        List<Ingredient> result = dataRetriever.createIngredients(List.of(fromage, oignon));

        assertEquals(2, result.size());
    }

    // j) createIngredients erreur (Carotte existe déjà)
    @Test
    void testCreateIngredients_error() {
        Ingredient carotte = new Ingredient(0, "Carotte", 2000, CategoryEnum.VEGETABLE, null);
        Ingredient ail = new Ingredient(0, "Ail", 2000, CategoryEnum.VEGETABLE, null);

        assertThrows(RuntimeException.class, () -> {
            dataRetriever.createIngredients(List.of(carotte, ail));
        });
    }

    // k) saveDish création
    @Test
    void testSaveDish_create() throws Exception {
        Ingredient oignon = new Ingredient(0, "Oignon", 500, CategoryEnum.VEGETABLE, null);

        Dish dish = new Dish(
                0,
                "Soupe de légumes",
                DishTypeEnum.START,
                List.of(oignon)
        );

        Dish saved = dataRetriever.saveDish(dish);
        assertEquals("Soupe de légumes", saved.getName());
    }

    // l) saveDish ajout ingrédients
    @Test
    void testSaveDish_addIngredients() throws Exception {
        Ingredient oignon = new Ingredient(0, "Oignon", 500, CategoryEnum.VEGETABLE, null);
        Ingredient laitue = new Ingredient(0, "Laitue", 800, CategoryEnum.VEGETABLE, null);
        Ingredient tomate = new Ingredient(0, "Tomate", 600, CategoryEnum.VEGETABLE, null);
        Ingredient fromage = new Ingredient(0, "Fromage", 2000, CategoryEnum.DAIRY, null);

        Dish dish = new Dish(
                1,
                "Salade fraîche",
                DishTypeEnum.START,
                List.of(oignon, laitue, tomate, fromage)
        );

        Dish saved = dataRetriever.saveDish(dish);
        assertEquals("Salade fraîche", saved.getName());
    }

    // m) saveDish suppression ingrédients
    @Test
    void testSaveDish_removeIngredients() throws Exception {
        Ingredient fromage = new Ingredient(0, "Fromage", 2000, CategoryEnum.DAIRY, null);

        Dish dish = new Dish(
                1,
                "Salade de fromage",
                DishTypeEnum.START,
                List.of(fromage)
        );

        Dish saved = dataRetriever.saveDish(dish);
        assertEquals("Salade de fromage", saved.getName());
    }
}
