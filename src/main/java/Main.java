public class Main {

    public static void main(String[] args) {

        DataRetriever dataRetriever = new DataRetriever();

        System.out.println("========= TEST 1 : Création d'une commande UNPAID =========");

        Order order = new Order();
        order.setReference("ORD0001");
        order.setPaymentStatus(PaymentStatusEnum.UNPAID);

        // 🔹 Exemple : 1 plat commandé
        Dish dish = dataRetriever.findDishById(1); // plat existant
        DishOrder dishOrder = new DishOrder();
        dishOrder.setDish(dish);
        dishOrder.setQuantity(1);

        order.setDishOrderList(List.of(dishOrder));

        try {
            dataRetriever.saveOrder(order);
            System.out.println("✅ Commande créée : " + order.getReference());
        } catch (Exception e) {
            System.out.println("❌ Erreur création commande : " + e.getMessage());
        }

        // =========================================================

        System.out.println("\n========= TEST 2 : Vente sur commande UNPAID (doit échouer) =========");

        try {
            dataRetriever.createSaleFrom(order);
            System.out.println("❌ ERREUR : la vente n'aurait pas dû être créée");
        } catch (Exception e) {
            System.out.println("✅ Exception attendue : " + e.getMessage());
        }

        // =========================================================

        System.out.println("\n========= TEST 3 : Vente sur commande PAYÉE =========");

        // 🔹 Passage manuel en PAYÉE
        order.setPaymentStatus(PaymentStatusEnum.PAID);

        try {
            Sale sale = dataRetriever.createSaleFrom(order);
            System.out.println("✅ Vente créée avec ID : " + sale.getId());
        } catch (Exception e) {
            System.out.println("❌ Erreur création vente : " + e.getMessage());
        }

        // =========================================================

        System.out.println("\n========= TEST 4 : Deuxième vente sur la même commande (doit échouer) =========");

        try {
            dataRetriever.createSaleFrom(order);
            System.out.println("❌ ERREUR : une deuxième vente a été créée");
        } catch (Exception e) {
            System.out.println("✅ Exception attendue : " + e.getMessage());
        }

        System.out.println("\n========= FIN DES TESTS =========");
    }
}