package uy.edu.ort.devops.productsserviceexample.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uy.edu.ort.devops.productsserviceexample.domain.Product;
import uy.edu.ort.devops.productsserviceexample.logic.ProductsLogic;

import java.util.Collection;

@RestController
@RequestMapping("/products")
public class ProductsEndpoint {

    @Autowired
    private ProductsLogic logic;

    @GetMapping("")
    public Collection<Product> index() {
        return logic.list();
    }

    @GetMapping("/{id}")
    public ResponseEntity getProduct(@PathVariable("id") String id) {
        if (logic.hasProduct(id)) {
            Product product = logic.getProduct(id);
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}