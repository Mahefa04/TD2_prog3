import java.time.Instant;

public class Sale {

    private Integer id;
    private Instant creationDatetime;
    private Order order;

    public Integer getId() {
        return id;
    }

    public Instant getCreationDatetime() {
        return creationDatetime;
    }

    public Order getOrder() {
        return order;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCreationDatetime(Instant creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}