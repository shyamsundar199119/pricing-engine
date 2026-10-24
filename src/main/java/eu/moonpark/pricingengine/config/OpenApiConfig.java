
package eu.moonpark.pricingengine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI moonParkOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MoonPark Pricing Engine API")
                        .description("""
                            REST API for calculating parking fees
                            across the M1, M2, and M3 tariff zones.
                            Returns prices in Norwegian kroner (NOK).
                            """)
                        .version("1.0.0"));
    }
}

