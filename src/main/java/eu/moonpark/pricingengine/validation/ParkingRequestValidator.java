package eu.moonpark.pricingengine.validation;

import eu.moonpark.pricingengine.exception.InvalidParkingRequestException;
import eu.moonpark.pricingengine.model.ParkingRequestDto;
import eu.moonpark.pricingengine.strategy.PricingAgent;

public final class ParkingRequestValidator {
    private ParkingRequestValidator() {
        // Utility class; prevent instantiation.
    }

    public static void validate(ParkingRequestDto request, PricingAgent pricingAgent) {
        if (request == null || request.startTime() == null || request.endTime() == null || request.tariffZone() == null) {
            throw new InvalidParkingRequestException(
                    "Start time, end time or Tariff zone cannot be null"
            );
        }

        if (request.endTime().isBefore(request.startTime())) {
            throw new InvalidParkingRequestException(
                    "End time cannot be before start time"
            );
        }

        if (request.tariffZone() == null) {
            throw new InvalidParkingRequestException(
                    "Tariff zone is required"
            );
        }

        if (request.tariffZone() != pricingAgent.supports()) {
            throw new InvalidParkingRequestException(
                    "This strategy supports tariff zone "
                            + pricingAgent.supports()
            );
        }
    }
}
