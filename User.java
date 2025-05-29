package login;

public class User {
    private String sdt;
    private String username;
    private String password;
    private String hoten;
    private String email;
    private String diachi;
    private String role;

    public User() {}

    public User(String sdt, String username, String password, String hoten, String email, String diachi, String role) {
        this.sdt = sdt;
        this.username = username;
        this.password = password;
        this.hoten = hoten;
        this.email = email;
        this.diachi = diachi;
        this.role = role;
    }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getHoten() { return hoten; }
    public void setHoten(String hoten) { this.hoten = hoten; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDiachi() { return diachi; }
    public void setDiachi(String diachi) { this.diachi = diachi; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "User{" +
                "sdt='" + sdt + '\'' +
                ", username='" + username + '\'' +
                ", hoten='" + hoten + '\'' +
                ", email='" + email + '\'' +
                ", diachi='" + diachi + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}