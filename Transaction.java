package Users.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String phoneNumber;
    private String packageName;
    private double price;
    private LocalDateTime timestamp;

    public Transaction(String phoneNumber, String packageName, double price) {
        this.phoneNumber = phoneNumber;
        this.packageName = packageName;
        this.price = price;
        this.timestamp = LocalDateTime.now();
    }

    public String toDisplayString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return "SĐT: " + phoneNumber + " | Gói: " + packageName + " | Giá: " + String.format("%,.0f VND", price) +
                " | Lúc: " + timestamp.format(formatter);
    }
}
