package uy.edu.ort.devops.ordersserviceexample.domain;

public class OrderStatus {
    private String orderId;
    private boolean success;
    private String description;

    public OrderStatus(String orderId, boolean success, String description) {
        this.orderId = orderId;
        this.success = success;
        this.description = description;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
