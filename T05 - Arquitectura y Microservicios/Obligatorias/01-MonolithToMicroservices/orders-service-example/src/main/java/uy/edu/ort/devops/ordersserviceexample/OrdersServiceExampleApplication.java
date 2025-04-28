package uy.edu.ort.devops.ordersserviceexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import uy.edu.ort.devops.ordersserviceexample.logic.OrdersLogic;

@SpringBootApplication
public class OrdersServiceExampleApplication {

	public static void main(String[] args) {
		OrdersLogic.setPaymentsServiceUrl(args[0]);
		OrdersLogic.setShippingServiceUrl(args[1]);
		OrdersLogic.setProductsServiceUrl(args[2]);

		SpringApplication.run(OrdersServiceExampleApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

}
