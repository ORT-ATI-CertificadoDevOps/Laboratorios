package uy.edu.ort.devops.paymentsserviceexample.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uy.edu.ort.devops.paymentsserviceexample.domain.PaymentStatus;
import uy.edu.ort.devops.paymentsserviceexample.logic.PaymentsLogic;

@RestController
@RequestMapping("/payments")
public class PaymentsEndpoint {
    @Autowired
    private PaymentsLogic logic;

    @PostMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public PaymentStatus pay(@PathVariable("id") String orderId) {
        return logic.pay(orderId);
    }
}