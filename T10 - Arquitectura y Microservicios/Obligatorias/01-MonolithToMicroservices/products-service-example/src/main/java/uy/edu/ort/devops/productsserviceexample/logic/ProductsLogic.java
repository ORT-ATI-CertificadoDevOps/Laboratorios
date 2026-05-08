package uy.edu.ort.devops.productsserviceexample.logic;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uy.edu.ort.devops.productsserviceexample.domain.Product;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProductsLogic {

    private static Logger logger = LoggerFactory.getLogger(ProductsLogic.class);

    private Map<String, Product> testProducts;

    public ProductsLogic() {
        testProducts = new HashMap<>();
        testProducts.put("123", new Product("123", "Producto 123", 10, "Este es un producto"));
        testProducts.put("321", new Product("321", "Producto 321", 10, "Este es un producto"));
        testProducts.put("111", new Product("111", "Producto 111", 10, "Este es un producto"));
    }

    public Collection<Product> list() {
        logger.info("Listing products");
        return testProducts.values();
    }

    public Product getProduct(String id) {
        return testProducts.get(id);
    }

    public boolean hasProduct(String id) {
        logger.info("Checking if has product: " + id);
        return testProducts.containsKey(id);
    }

}
