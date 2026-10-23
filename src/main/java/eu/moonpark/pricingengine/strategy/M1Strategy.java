package eu.moonpark.pricingengine.strategy;

import eu.moonpark.pricingengine.exception.InvalidParkingRequestException;
import eu.moonpark.pricingengine.model.ParkingRequestDto;
import eu.moonpark.pricingengine.model.TariffZone;
import eu.moonpark.pricingengine.validation.ParkingRequestValidator;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class M1Strategy implements PricingAgent {

    private static final long RATE_PER_HOUR = 60; // 60 NOK per hour
    private static final long SECOND_PER_MINUTE = 60; // 60 seconds per minute
    private static final long RATE_PER_MINUTE = RATE_PER_HOUR/60; // 1 NOK per started minute

    @Override
    public TariffZone supports() {
        return TariffZone.M1;
    }

    @Override
    public long calculatePrice(ParkingRequestDto request) {
        ParkingRequestValidator.validate(request, this);

        long durationSeconds = Duration.between(
                request.startTime(),
                request.endTime()
        ).getSeconds();

        // Charge 1 NOK for each started minute.
        // Ceiling division: (seconds + 59) / 60.
        return ((durationSeconds + SECOND_PER_MINUTE - 1) / SECOND_PER_MINUTE) * RATE_PER_MINUTE;
    }
}
