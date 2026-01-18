
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataRetrieverTest {

    @Test
    public void testFindDishById_ok() throws Exception {
        DataRetriever dr = new DataRetriever();

        Dish dish = dr.findDishById(1);

        assertNotNull(dish);
        assertEquals("Salade Fraîche", dish.getName());
        assertEquals(2, dish.getIngredients().size());
    }

    @Test
    public void testFindDishById_notFound() throws SQLException {
        DataRetriever dr = new DataRetriever();
        assertThrows(Exception.class, () -> dr.findDishById(999));
    }


    @Test
    public void testFindIngredients_page2() throws Exception {
        DataRetriever dr = new DataRetriever();

        List<Ingredient> list = dr.findIngredients(2, 2);

        assertEquals(2, list.size());
        assertEquals("Poulet", list.get(0).getName());
        assertEquals("Chocolat", list.get(1).getName());
    }

    @Test
    public void testFindIngredients_empty() throws Exception {
        DataRetriever dr = new DataRetriever();

        List<Ingredient> list = dr.findIngredients(3, 5);

        assertTrue(list.isEmpty());
    }

    @Test
    public void testFindDishByIngredientName() throws Exception {
        DataRetriever dr = new DataRetriever();

        List<Dish> dishes = dr.findDishByIngredientName("eur");

        assertEquals(1, dishes.size());
        assertEquals("Gâteau au chocolat", dishes.get(0).getName());
    }

    @Test
    public void testFindIngredientsByCriteria_category() throws Exception {
        DataRetriever dr = new DataRetriever();

        List<Ingredient> list = dr.findIngredientsByCriteria(
                null, CategoryEnum.vegetable, null, 1, 10
        );

        assertEquals(2, list.size());
    }

    @Test
    public void testFindIngredientsByCriteria_empty() throws Exception {
        DataRetriever dr = new DataRetriever();

        List<Ingredient> list = dr.findIngredientsByCriteria(
                "cho", null, "Sal", 1, 10
        );

        assertTrue(list.isEmpty());
    }

    @Test
    public void testFindIngredientsByCriteria_chocolat() throws Exception {
        DataRetriever dr = new DataRetriever();

        List<Ingredient> list = dr.findIngredientsByCriteria(
                "cho", null, "gâteau", 1, 10
        );

        assertEquals(1, list.size());
        assertEquals("Chocolat", list.get(0).getName());
    }

    @Test
    public void testCreateIngredients_ok() throws Exception {
        DataRetriever dr = new DataRetriever();

        List<Ingredient> ingredients = List.of(
                new Ingredient(1, "Fromage", 1200.00, CategoryEnum.dairy, null, null),
                new Ingredient(2, "Oignon", 500.00, CategoryEnum.vegetable, null, null)
        );

        List<Ingredient> result = dr.createIngredients(ingredients);

        assertEquals(2, result.size());
    }

    @Test
    public void testCreateIngredients_exception() throws Exception {
        DataRetriever dr = new DataRetriever();

        List<Ingredient> ingredients = List.of(
                new Ingredient(1, "Carotte", 2000.00, CategoryEnum.vegetable, null, null),
                new Ingredient(2, "Laitue", 200.00, CategoryEnum.vegetable, null, null)
        );

        assertThrows(Exception.class, () -> dr.createIngredients(ingredients));
    }
    }

    @Test
    public void testSaveDish_addIngredient() throws Exception {
        DataRetriever dr = new DataRetriever();

        Dish dish = dr.findDishById(1);
        dish.getIngredients().add(new Ingredient(5, null));

        Dish saved = dr.saveDish(dish);

        assertEquals(4, saved.getIngredients().size());
    }
}

public void main() {
}
