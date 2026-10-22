package eu.moonpark.pricingengine.strategy;

import eu.moonpark.pricingengine.model.ParkingRequestDto;
import eu.moonpark.pricingengine.model.TariffZone;

public interface PricingAgent {
    TariffZone supports();

    long calculatePrice(ParkingRequestDto request);
}
