package eu.moonpark.pricingengine.strategy;

import eu.moonpark.pricingengine.model.ParkingRequestDto;

public class M1Strategy implements PricingAgent {

    @Override
    public long calculatePrice(ParkingRequestDto request) {
        // Implement the pricing logic for M1 tariff zone here
        throw new UnsupportedOperationException("Pricing logic for M1 tariff zone is not implemented yet.");
    }
}
