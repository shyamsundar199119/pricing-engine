package eu.moonpark.pricingengine.strategy;

import eu.moonpark.pricingengine.config.PricingProperties;
import eu.moonpark.pricingengine.model.ParkingRequestDto;
import eu.moonpark.pricingengine.model.TariffZone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Duration;

@Slf4j
@Component
public class M2Strategy implements PricingAgent {
    private static final long SECONDS_PER_HOUR = 3600;
    private final PricingProperties properties;

    public M2Strategy(PricingProperties properties) {
        this.properties = properties;
    }

    @Override
    public TariffZone supports() {
        return TariffZone.M2;
    }

    @Override
    public long calculatePrice(ParkingRequestDto request) {
        log.debug("M2 Strategy parking pricing calculation for request from {} to {}", request.startTime(), request.endTime());

        long durationSeconds = Duration.between(
                request.startTime(),
                request.endTime()
        ).getSeconds();

        if (durationSeconds == 0) {
            return 0;
        }

        long startedHours =
                (durationSeconds + SECONDS_PER_HOUR - 1) / SECONDS_PER_HOUR;

        DayOfWeek day = request.startTime().getDayOfWeek();

        long hourlyRate = isWeekend(day)
                ? properties.m2WeekendPricePerHour()
                : properties.m2WeekdayPricePerHour();

        return startedHours * hourlyRate;
    }

    private boolean isWeekend(DayOfWeek day) {
        return day == DayOfWeek.SATURDAY
                || day == DayOfWeek.SUNDAY;
    }
}
