import java.time.LocalDateTime;

public class Transaction {
    private String item;
    private double price;
    private LocalDateTime time;

    public Transaction(String item, double price, LocalDateTime time) {
        this.item = item;
        this.price = price;
        this.time = time;
    }

    public String getItem() {
        return item;
    }

    public double getPrice() {
        return price;
    }

    public LocalDateTime getTime() {
        return time;
    }
}