import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        DataRetriever dr = new DataRetriever();

        // 1️⃣ Test findDishById
        Dish dish = dr.findDishById(4); // Gâteau au chocolat

        try {
            System.out.println("Coût du plat : " + dish.getDishCost());
        } catch (RuntimeException e) {
            System.out.println("ERREUR : " + e.getMessage());
        }

        // 2️⃣ Test saveDish
        Dish newDish = new Dish(
                0,
                "Soupe de légumes",
                DishTypeEnum.START,
                List.of(
                        new Ingredient(0, "Carotte", 500, CategoryEnum.VEGETABLE, 2.0, null)
                )
        );

        Dish saved = dr.saveDish(newDish);
        System.out.println("Plat sauvegardé : " + saved.getName());
    }
}
