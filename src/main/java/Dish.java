import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Dish {
    private int id;
    private String name;
    private DishTypeEnum dishType;
    private List<Ingredient> ingredients;

    public Dish(int id, String name, DishTypeEnum dishType, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.ingredients = ingredients;
    }

    public Double getDishCost() {

        double total = 0;

        for (Ingredient ing : ingredients) {

            if (ing.getRequiredQuantity() == null) {
                throw new RuntimeException(
                        "Quantité inconnue pour l’ingrédient : " + ing.getName()
                );
            }

            total += ing.getPrice() * ing.getRequiredQuantity();
        }

        return total;
    }


    public String getName() {
        return this.name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public int getId() {
        return id;
    }

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    // getters & setters
}
