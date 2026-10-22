package eu.moonpark.pricingengine.strategy;

import eu.moonpark.pricingengine.model.ParkingRequestDto;


public interface PricingAgent {
    long calculatePrice(ParkingRequestDto request);
}
