package uy.edu.ort.devops.monolithstoreexample.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.edu.ort.devops.monolithstoreexample.domain.OrderStatus;
import uy.edu.ort.devops.monolithstoreexample.domain.PaymentStatus;
import uy.edu.ort.devops.monolithstoreexample.domain.Product;

import java.util.List;

@Service
public class OrdersLogic {

    private static Logger logger = LoggerFactory.getLogger(OrdersLogic.class);

    @Autowired
    private ProductsLogic productsLogic;
    @Autowired
    private PaymentsLogic paymentsLogic;
    @Autowired
    private ShippingLogic shippingLogic;

    public OrderStatus buy(List<String> products) {
        StringBuilder errorBuilder = new StringBuilder();
        logger.info("Creating order.");
        logger.info("Checking products.");

        boolean hasError = false;
        for (String productId : products) {
            if (productsLogic.hasProduct(productId)) {
                Product product = productsLogic.getProduct(productId);
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
            PaymentStatus paymentStatus = paymentsLogic.pay(orderId);
            if (paymentStatus.isSuccess()) {
                logger.info("Payment ok.");
                shippingLogic.addShipping(orderId);
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
}
