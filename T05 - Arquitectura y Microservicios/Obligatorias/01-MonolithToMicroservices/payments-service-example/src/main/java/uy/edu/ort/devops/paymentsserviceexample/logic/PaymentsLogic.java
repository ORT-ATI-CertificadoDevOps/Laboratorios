package uy.edu.ort.devops.paymentsserviceexample.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uy.edu.ort.devops.paymentsserviceexample.domain.PaymentStatus;

@Service
public class PaymentsLogic {

    private static Logger logger = LoggerFactory.getLogger(PaymentsLogic.class);

    public PaymentStatus pay(String orderId) {
        PaymentStatus[] options = {new PaymentStatus(orderId, true, "Done."), new PaymentStatus(orderId, false, "No money.")};
        java.util.Random random = new java.util.Random();
        int randomOption = random.nextInt(options.length);

        logger.info("Paying result: " + options[randomOption].toString());

        return options[randomOption];
    }
}
