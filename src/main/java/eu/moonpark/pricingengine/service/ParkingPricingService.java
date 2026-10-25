package eu.moonpark.pricingengine.service;

import eu.moonpark.pricingengine.config.PricingProperties;
import eu.moonpark.pricingengine.exception.InvalidParkingRequestException;
import eu.moonpark.pricingengine.model.ParkingRequestDto;
import eu.moonpark.pricingengine.model.PricingResult;
import eu.moonpark.pricingengine.strategy.PricingAgent;
import eu.moonpark.pricingengine.validation.ParkingRequestValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ParkingPricingService {
    private final List<PricingAgent> pricingAgents;
    private final PricingProperties properties;

    public ParkingPricingService(List<PricingAgent> pricingAgents, PricingProperties properties) {
        this.pricingAgents = pricingAgents;
        this.properties = properties;
    }

    public PricingResult calculatePrice(ParkingRequestDto request) {
        ParkingRequestValidator.validate(request);

        log.debug("Calculating price for request for tariff zone: {} from {} to {}", request.tariffZone(), request.startTime(), request.endTime());

        PricingAgent pricingAgent = pricingAgents.stream()
                .filter(agent -> agent.supports() == request.tariffZone())
                .findFirst()
                .orElseThrow(() -> new InvalidParkingRequestException(
                        "No pricing strategy found for tariff zone: "
                                + request.tariffZone()
                ));

        long price = pricingAgent.calculatePrice(request);
        log.info("Calculated price for tariff zone {} from {} to {} is: {}",
                request.tariffZone(), request.startTime(), request.endTime(), price);
        return new PricingResult(price, properties.currency());
    }
}