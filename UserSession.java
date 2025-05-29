package Users;

public class UserSession {
    private static String phoneNumber;

    public static void setPhoneNumber(String phone) {
        phoneNumber = phone;
    }

    public static String getPhoneNumber() {
        return phoneNumber;
    }
}