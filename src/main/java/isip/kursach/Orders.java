package isip.kursach;

public class Orders {
    private int id;
    private String userLogin;
    private String productName;
    private String orderTime;

    public Orders(int id, String userLogin, String productName, String orderTime) {
        this.id = id;
        this.userLogin = userLogin;
        this.productName = productName;
        this.orderTime = orderTime;
    }

    public int getId() {
        return id;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public String getProductName() {
        return productName;
    }

    public String getOrderTime() {
        return orderTime;
    }
}
