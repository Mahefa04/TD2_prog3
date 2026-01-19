import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    private final Connection connection;

    public DataRetriever() {
        this.connection = DBConnection.getConnection();
    }

    /* =====================================================
       a & b) findDishById
       ===================================================== */
    public Dish findDishById(Integer id) throws SQLException {

        String sql = "SELECT * FROM dish WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            throw new RuntimeException("Dish not found");
        }

        Dish dish = new Dish(
                rs.getInt("id"),
                rs.getString("name"),
                DishTypeEnum.valueOf(rs.getString("dish_type")),
                findIngredientsByDishId(id)
        );

        return dish;
    }

    private List<Ingredient> findIngredientsByDishId(int dishId) throws SQLException {
        String sql = "SELECT * FROM ingredient WHERE id_dish = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, dishId);

        ResultSet rs = ps.executeQuery();
        List<Ingredient> ingredients = new ArrayList<>();

        while (rs.next()) {
            ingredients.add(new Ingredient(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    CategoryEnum.valueOf(rs.getString("category")),
                    rs.getObject("required_quantity", Double.class),
                    null
            ));
        }
        return ingredients;
    }

    /* =====================================================
       c & d) findIngredients (pagination)
       ===================================================== */
    public List<Ingredient> findIngredients(int page, int size) throws SQLException {

        String sql = "SELECT * FROM ingredient LIMIT ? OFFSET ?";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setInt(1, size);
        ps.setInt(2, (page - 1) * size);

        ResultSet rs = ps.executeQuery();
        List<Ingredient> list = new ArrayList<>();

        while (rs.next()) {
            list.add(new Ingredient(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    CategoryEnum.valueOf(rs.getString("category")),
                    rs.getObject("required_quantity", Double.class),
                    null
            ));
        }
        return list;
    }

    /* =====================================================
       i & j) createIngredients (atomicité)
       ===================================================== */
    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) throws SQLException {

        connection.setAutoCommit(false);

        try {
            for (Ingredient ing : newIngredients) {

                // vérifier si existe
                String checkSql = "SELECT COUNT(*) FROM ingredient WHERE name = ?";
                PreparedStatement checkPs = connection.prepareStatement(checkSql);
                checkPs.setString(1, ing.getName());

                ResultSet rs = checkPs.executeQuery();
                rs.next();

                if (rs.getInt(1) > 0) {
                    throw new RuntimeException("Ingredient already exists");
                }
            }
                // insertion si tous est ok
            for (Ingredient ing : newIngredients) {
                String insertSql = """
                        INSERT INTO ingredient(name, price, category, required_quantity)
                        VALUES (?, ?, ?::ingredient_category, ?)
                        """;
                PreparedStatement insertPs = connection.prepareStatement(insertSql);
                insertPs.setString(1, ing.getName());
                insertPs.setDouble(2, ing.getPrice());
                insertPs.setString(3, ing.getCategory().name());
                insertPs.executeUpdate();
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

    /* =====================================================
       k, l, m) saveDish
       ===================================================== */
    public Dish saveDish(Dish dish) throws SQLException {

        connection.setAutoCommit(false);

        try {
            int dishId = dish.getId();

            // vérifier existence
            String checkSql = "SELECT COUNT(*) FROM dish WHERE id = ?";
            PreparedStatement checkPs = connection.prepareStatement(checkSql);
            checkPs.setInt(1, dishId);
            ResultSet rs = checkPs.executeQuery();
            rs.next();

            if (rs.getInt(1) == 0) {
                // INSERT
                String insertSql =
                        "INSERT INTO dish(name, dish_type) VALUES (?, ?::dish_type) RETURNING id";
                PreparedStatement ps = connection.prepareStatement(insertSql);
                ps.setString(1, dish.getName());
                ps.setString(2, dish.getDishType().name());

                ResultSet r = ps.executeQuery();
                r.next();
                dishId = r.getInt(1);
            } else {
                // UPDATE
                String updateSql =
                        "UPDATE dish SET name=?, dish_type=?::dish_type WHERE id=?";
                PreparedStatement ps = connection.prepareStatement(updateSql);
                ps.setString(1, dish.getName());
                ps.setString(2, dish.getDishType().name());
                ps.setInt(3, dishId);
                ps.executeUpdate();
            }

            // désassocier anciens ingrédients
            String clearSql =
                    "UPDATE ingredient SET id_dish=NULL WHERE id_dish=?";
            PreparedStatement clearPs = connection.prepareStatement(clearSql);
            clearPs.setInt(1, dishId);
            clearPs.executeUpdate();

            // associer nouveaux ingrédients
            for (Ingredient ing : dish.getIngredients()) {
                String linkSql =
                        "UPDATE ingredient SET id_dish=? WHERE name=?";
                PreparedStatement linkPs = connection.prepareStatement(linkSql);
                linkPs.setInt(1, dishId);
                linkPs.setString(2, ing.getName());
                linkPs.executeUpdate();
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

    /* =====================================================
       e) findDishsByIngredientName
       ===================================================== */
    public List<Dish> findDishsByIngredientName(String ingredientName) throws SQLException {

        String sql = """
                SELECT DISTINCT d.*
                FROM dish d
                JOIN ingredient i ON d.id = i.id_dish
                WHERE LOWER(i.name) LIKE LOWER(?)
                """;

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, "%" + ingredientName + "%");

        ResultSet rs = ps.executeQuery();
        List<Dish> dishes = new ArrayList<>();

        while (rs.next()) {
            dishes.add(new Dish(
                    rs.getInt("id"),
                    rs.getString("name"),
                    DishTypeEnum.valueOf(rs.getString("dish_type")),
                    new ArrayList<>()
            ));
        }
        return dishes;
    }

    /* =====================================================
       f, g, h) findIngredientsByCriteria
       ===================================================== */
    public List<Ingredient> findIngredientsByCriteria(
            String ingredientName,
            CategoryEnum category,
            String dishName,
            int page,
            int size) throws SQLException {

        StringBuilder sql = new StringBuilder("""
        SELECT i.*
        FROM ingredient i
        LEFT JOIN dish d ON i.id_dish = d.id
        WHERE 1=1
    """);

        if (ingredientName != null) {
            sql.append(" AND i.name ILIKE ?");
        }
        if (category != null) {
            sql.append(" AND i.category = ?::ingredient_category");
        }
        if (dishName != null) {
            sql.append(" AND d.name ILIKE ?");
        }

        sql.append(" LIMIT ? OFFSET ?");

        PreparedStatement ps = connection.prepareStatement(sql.toString());

        int index = 1;
        if (ingredientName != null) {
            ps.setString(index++, "%" + ingredientName + "%");
        }
        if (category != null) {
            ps.setString(index++, category.name());
        }
        if (dishName != null) {
            ps.setString(index++, "%" + dishName + "%");
        }

        ps.setInt(index++, size);
        ps.setInt(index, (page - 1) * size);

        ResultSet rs = ps.executeQuery();
        List<Ingredient> list = new ArrayList<>();

        while (rs.next()) {
            list.add(new Ingredient(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    CategoryEnum.valueOf(rs.getString("category")),
                    rs.getObject("required_quantity", Double.class),
                    null
            ));
        }
        return list;
    }
}
