package eu.moonpark.pricingengine.strategy;

import eu.moonpark.pricingengine.model.ParkingRequestDto;
import eu.moonpark.pricingengine.model.TariffZone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
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
        log.debug("M1 Strategy parking pricing calculation for request from {} to {}", request.startTime(), request.endTime());
        long durationSeconds = Duration.between(
                request.startTime(),
                request.endTime()
        ).getSeconds();

        return ((durationSeconds + SECOND_PER_MINUTE - 1) / SECOND_PER_MINUTE) * RATE_PER_MINUTE;
    }
}
