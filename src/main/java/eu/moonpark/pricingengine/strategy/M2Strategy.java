package eu.moonpark.pricingengine.strategy;

import eu.moonpark.pricingengine.model.ParkingRequestDto;

public class M2Strategy implements PricingAgent {

    @Override
    public long calculatePrice(ParkingRequestDto request) {
        // Implement the pricing logic for M2 zone here
        throw new UnsupportedOperationException("M2 pricing strategy is not implemented yet.");
    }
}
