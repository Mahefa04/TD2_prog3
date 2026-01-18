import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    private Connection connection;

    public  DataRetriever() throws SQLException {
        this.connection = DBConnection.getDBConnection();
    }

    // a)
    public Dish findDishById(Integer id) throws SQLException {
        String sql = """
        SELECT d.id, d.name,
               i.id AS ingredient_id, i.name AS ingredient_name
        FROM Dish d
        LEFT JOIN Dish_Ingredient di ON d.id = di.dish_id
        LEFT JOIN Ingredient i ON i.id = di.ingredient_id
        WHERE d.id = ?
    """;

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        Dish dish = null;
        List<Ingredient> ingredients = new ArrayList<>();

        while (rs.next()) {
            if (dish == null) {
                dish = new Dish(
                        rs.getInt("id"),
                        rs.getString("name")
                );
            }

            int ingId = rs.getInt("ingredient_id");
            if (!rs.wasNull()) {
                ingredients.add(new Ingredient(
                        ingId,
                        rs.getString("ingredient_name")
                ));
            }
        }

        if (dish != null) {
            dish.setIngredients(ingredients);
        }

        return dish;
    }

    // b)
    public List<Ingredient> findIngredients(int page, int size) throws SQLException {
        String sql = "SELECT * FROM Ingredient LIMIT ? OFFSET ?";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setInt(1, size);
        ps.setInt(2, page * size);

        ResultSet rs = ps.executeQuery();
        List<Ingredient> ingredients = new ArrayList<>();

        while (rs.next()) {
            ingredients.add(new Ingredient(
                    rs.getInt("id"),
                    rs.getString("name")
            ));
        }
        return ingredients;
    }


    // c)
    public List<Ingredient> createIngredients(List<Ingredient> newIngredients)
            throws SQLException {

        String checkSql = "SELECT id FROM Ingredient WHERE name = ?";
        String insertSql = "INSERT INTO Ingredient(name) VALUES (?)";

        connection.setAutoCommit(false);

        try {
            for (Ingredient ing : newIngredients) {
                PreparedStatement check = connection.prepareStatement(checkSql);
                check.setString(1, ing.getName());
                ResultSet rs = check.executeQuery();

                if (rs.next()) {
                    throw new RuntimeException(
                            "Ingredient déjà existant : " + ing.getName()
                    );
                }

                PreparedStatement insert = connection.prepareStatement(
                        insertSql, Statement.RETURN_GENERATED_KEYS
                );
                insert.setString(1, ing.getName());
                insert.executeUpdate();

                ResultSet keys = insert.getGeneratedKeys();
                if (keys.next()) {
                    ing.setId(keys.getInt(1));
                }
            }

            connection.commit();
            return newIngredients;

        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // d)
    public Dish saveDish(Dish dish) throws SQLException {

        connection.setAutoCommit(false);

        try {
            if (dish.getId() == 0) {
                String insert = "INSERT INTO Dish(name) VALUES (?)";
                PreparedStatement ps = connection.prepareStatement(
                        insert, Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, dish.getName());
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    dish.setId(rs.getInt(1));
                }
            } else {
                String update = "UPDATE Dish SET name=? WHERE id=?";
                PreparedStatement ps = connection.prepareStatement(update);
                ps.setString(1, dish.getName());
                ps.setInt(2, dish.getId());
                ps.executeUpdate();

                PreparedStatement delete = connection.prepareStatement(
                        "DELETE FROM Dish_Ingredient WHERE dish_id=?"
                );
                delete.setInt(1, dish.getId());
                delete.executeUpdate();
            }

            String link = "INSERT INTO Dish_Ingredient(dish_id, ingredient_id) VALUES (?, ?)";
            for (Ingredient ing : dish.getIngredients()) {
                PreparedStatement ps = connection.prepareStatement(link);
                ps.setInt(1, dish.getId());
                ps.setInt(2, ing.getId());
                ps.executeUpdate();
            }

            connection.commit();
            return dish;

        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // e)
    public List<Dish> findDishByIngredientName(String ingredientName)
            throws SQLException {

        String sql = """
        SELECT DISTINCT d.id, d.name
        FROM Dish d
        JOIN Dish_Ingredient di ON d.id = di.dish_id
        JOIN Ingredient i ON i.id = di.ingredient_id
        WHERE i.name LIKE ?
    """;

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, "%" + ingredientName + "%");

        ResultSet rs = ps.executeQuery();
        List<Dish> dishes = new ArrayList<>();

        while (rs.next()) {
            dishes.add(new Dish(
                    rs.getInt("id"),
                    rs.getString("name")
            ));
        }
        return dishes;
    }

    // f)
    public List<Ingredient> findIngredientsByCriteria(
            String ingredientName,
            CategoryEnum category,
            String dishName,
            int page,
            int size
    ) throws SQLException {

        StringBuilder sql = new StringBuilder("""
        SELECT DISTINCT i.*
        FROM Ingredient i
        LEFT JOIN Dish_Ingredient di ON i.id = di.ingredient_id
        LEFT JOIN Dish d ON d.id = di.dish_id
        WHERE 1=1
    """);

        List<Object> params = new ArrayList<>();

        if (ingredientName != null) {
            sql.append(" AND i.name LIKE ?");
            params.add("%" + ingredientName + "%");
        }

        if (category != null) {
            sql.append(" AND i.category = ?");
            params.add(category.name());
        }

        if (dishName != null) {
            sql.append(" AND d.name LIKE ?");
            params.add("%" + dishName + "%");
        }

        sql.append(" LIMIT ? OFFSET ?");
        params.add(size);
        params.add(page * size);

        PreparedStatement ps = connection.prepareStatement(sql.toString());

        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        ResultSet rs = ps.executeQuery();
        List<Ingredient> ingredients = new ArrayList<>();

        while (rs.next()) {
            ingredients.add(new Ingredient(
                    rs.getInt("id"),
                    rs.getString("name")
            ));
        }
        return ingredients;
    }

}
