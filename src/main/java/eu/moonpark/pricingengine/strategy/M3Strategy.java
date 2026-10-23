package eu.moonpark.pricingengine.strategy;

import eu.moonpark.pricingengine.model.ParkingRequestDto;
import eu.moonpark.pricingengine.model.TariffZone;

public class M3Strategy implements PricingAgent {

    @Override
    public TariffZone supports() {
        return TariffZone.M3;
    }

    @Override
    public long calculatePrice(ParkingRequestDto request) {
        // Implement the pricing logic for M3 zone here
        throw new UnsupportedOperationException("M3 pricing strategy not implemented yet.");
    }
}
