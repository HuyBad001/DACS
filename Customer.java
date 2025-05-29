package Users;

public class Customer {
    private int id;
    private String name;
    private String phone;
    private String address;
    private String email;
    private String plan; // Gói cước (nếu có)

    public Customer(int id, String name, String phone, String address, String email, String plan) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.plan = plan;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public String getPlan() { return plan; }
}