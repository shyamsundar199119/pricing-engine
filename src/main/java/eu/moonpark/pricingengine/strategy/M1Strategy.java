package eu.moonpark.pricingengine.strategy;

import eu.moonpark.pricingengine.config.PricingProperties;
import eu.moonpark.pricingengine.model.ParkingRequestDto;
import eu.moonpark.pricingengine.model.TariffZone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class M1Strategy implements PricingAgent {
    private final PricingProperties properties;

    public M1Strategy(PricingProperties properties) {
        this.properties = properties;
    }


    private static final long SECOND_PER_MINUTE = 60; // 60 seconds per minute
    @Override
    public TariffZone supports() {
        return TariffZone.M1;
    }

    @Override
    public long calculatePrice(ParkingRequestDto request) {
        long pricePerHour = properties.m1PricePerHour();
        log.debug("M1 Strategy parking pricing calculation for request from {} to {}", request.startTime(), request.endTime());
        long durationSeconds = Duration.between(
                request.startTime(),
                request.endTime()
        ).getSeconds();

        long pricePerMinute = pricePerHour / 60;
        return ((durationSeconds + SECOND_PER_MINUTE - 1) / SECOND_PER_MINUTE) * pricePerMinute;
    }
}
