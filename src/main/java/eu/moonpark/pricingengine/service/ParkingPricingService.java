package eu.moonpark.pricingengine.service;

import eu.moonpark.pricingengine.exception.InvalidParkingRequestException;
import eu.moonpark.pricingengine.model.ParkingRequestDto;
import eu.moonpark.pricingengine.strategy.PricingAgent;
import eu.moonpark.pricingengine.validation.ParkingRequestValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingPricingService {
    private final List<PricingAgent> pricingAgents;

    public ParkingPricingService(List<PricingAgent> pricingAgents) {
        this.pricingAgents = pricingAgents;
    }

    public long calculatePrice(ParkingRequestDto request) {
        ParkingRequestValidator.validate(request);

        PricingAgent pricingAgent = pricingAgents.stream()
                .filter(agent -> agent.supports() == request.tariffZone())
                .findFirst()
                .orElseThrow(() -> new InvalidParkingRequestException(
                        "No pricing strategy found for tariff zone: "
                                + request.tariffZone()
                ));

        return pricingAgent.calculatePrice(request);
    }
}