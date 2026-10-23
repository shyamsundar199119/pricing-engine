package eu.moonpark.pricingengine.validation;

import eu.moonpark.pricingengine.exception.InvalidParkingRequestException;
import eu.moonpark.pricingengine.model.ParkingRequestDto;

public final class ParkingRequestValidator {
    private ParkingRequestValidator() {
        // Utility class; prevent instantiation.
    }

    public static void validate(ParkingRequestDto request) {
        if (request == null) {
            throw new InvalidParkingRequestException(
                    "Parking request cannot be null");
        }

        if (request.endTime().isBefore(request.startTime())) {
            throw new InvalidParkingRequestException("End time cannot be before start time");
        }
    }
}
