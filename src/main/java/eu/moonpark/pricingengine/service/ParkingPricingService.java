package eu.moonpark.pricingengine.service;

import eu.moonpark.pricingengine.model.ParkingRequestDto;
import eu.moonpark.pricingengine.strategy.PricingAgent;

import java.util.List;

public class ParkingPricingService {
    private final List<PricingAgent> pricingAgents;

    public ParkingPricingService(List<PricingAgent> pricingAgents) {
        this.pricingAgents = pricingAgents;
    }

    public long calculatePrice(ParkingRequestDto request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
