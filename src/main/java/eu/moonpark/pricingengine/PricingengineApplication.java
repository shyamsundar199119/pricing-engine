package eu.moonpark.pricingengine;

import eu.moonpark.pricingengine.config.PricingProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PricingProperties.class)
public class PricingengineApplication {

	public static void main(String[] args) {
		SpringApplication.run(PricingengineApplication.class, args);
	}

}
