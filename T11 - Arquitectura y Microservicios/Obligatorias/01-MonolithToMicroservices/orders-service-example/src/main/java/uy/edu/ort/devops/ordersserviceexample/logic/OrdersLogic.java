package uy.edu.ort.devops.ordersserviceexample.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uy.edu.ort.devops.ordersserviceexample.domain.OrderStatus;
import uy.edu.ort.devops.ordersserviceexample.dtos.PaymentStatus;
import uy.edu.ort.devops.ordersserviceexample.dtos.Product;

import java.util.List;

@Service
public class OrdersLogic {

    private static Logger logger = LoggerFactory.getLogger(OrdersLogic.class);

    private static String PAYMENTS_SERVICE_URL;
    private static String SHIPPING_SERVICE_URL;
    private static String PRODUCTS_SERVICE_URL;

    @Autowired
    private RestTemplate restTemplate;

    public static void setPaymentsServiceUrl(String paymentsServiceUrl) {
        PAYMENTS_SERVICE_URL = paymentsServiceUrl;
    }

    public static void setShippingServiceUrl(String shippingServiceUrl) {
        SHIPPING_SERVICE_URL = shippingServiceUrl;
    }

    public static void setProductsServiceUrl(String productsServiceUrl) {
        PRODUCTS_SERVICE_URL = productsServiceUrl;
    }

    public OrderStatus buy(List<String> products) {
        StringBuilder errorBuilder = new StringBuilder();
        logger.info("Creating order.");
        logger.info("Checking products.");

        boolean hasError = false;
        for (String productId : products) {
            Product product = getProduct(productId);
            if (product != null) {
                if (product.getStock() == 0) {
                    if (hasError) {
                        errorBuilder.append(" ");
                    }
                    hasError = true;
                    errorBuilder.append("No stock: ").append(productId).append(".");
                }
            } else {
                if (hasError) {
                    errorBuilder.append(" ");
                }
                hasError = true;
                errorBuilder.append("Missing: ").append(productId).append(".");
            }

        }

        String orderId = java.util.UUID.randomUUID().toString();
        if (!hasError) {
            logger.info("Products ok.");
            PaymentStatus paymentStatus = pay(orderId);
            if (paymentStatus.isSuccess()) {
                logger.info("Payment ok.");
                addShipping(orderId);
                return new OrderStatus(orderId, true, "Ok.");
            } else {
                logger.info("Error in payment: " + paymentStatus.getDescription());
                return new OrderStatus(orderId, false, paymentStatus.getDescription());
            }
        } else {
            String productErrors = errorBuilder.toString();
            logger.info("Error in products: " + productErrors);
            return new OrderStatus(orderId, false, productErrors);
        }
    }

    private Product getProduct(String id) {
        try {
            logger.info("Invoking products service.");
            return restTemplate.getForObject(PRODUCTS_SERVICE_URL + "/products/" + id, Product.class);
        } catch (HttpClientErrorException ex)   {
            return null;
        }
    }

    private PaymentStatus pay(String orderId) {
        try {
            logger.info("Invoking payments service.");
            return restTemplate.postForObject(PAYMENTS_SERVICE_URL + "/payments/" + orderId, null, PaymentStatus.class);
        } catch (HttpClientErrorException ex)   {
            return null;
        }
    }

    private void addShipping(String orderId) {
        logger.info("Invoking shipping service.");
        restTemplate.postForEntity(SHIPPING_SERVICE_URL + "/shipping/" + orderId, null, String.class);
    }
}