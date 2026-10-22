package eu.moonpark.pricingengine.strategy;

import eu.moonpark.pricingengine.exception.InvalidParkingRequestException;
import eu.moonpark.pricingengine.model.ParkingRequestDto;

import java.time.Duration;

public class M1Strategy implements PricingAgent {

    private static final long RATE_PER_HOUR = 60; // 60 NOK per hour
    private static final long SECOND_PER_MINUTE = 60; // 60 seconds per minute
    private static final long RATE_PER_MINUTE = RATE_PER_HOUR/60; // 1 NOK per started minute

    @Override
    public long calculatePrice(ParkingRequestDto request) {
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

        long durationSeconds = Duration.between(
                request.startTime(),
                request.endTime()
        ).getSeconds();

        // Charge 1 NOK for each started minute.
        // Ceiling division: (seconds + 59) / 60.
        return ((durationSeconds + SECOND_PER_MINUTE - 1) / SECOND_PER_MINUTE) * RATE_PER_MINUTE;
    }
}
