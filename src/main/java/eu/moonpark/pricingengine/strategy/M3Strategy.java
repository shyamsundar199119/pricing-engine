package eu.moonpark.pricingengine.strategy;

import eu.moonpark.pricingengine.config.PricingProperties;
import eu.moonpark.pricingengine.model.ParkingRequestDto;
import eu.moonpark.pricingengine.model.TariffZone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
@Slf4j
public class M3Strategy implements PricingAgent {
    private static final long SECONDS_PER_MINUTE = 60;

    private final PricingProperties properties;

    public M3Strategy(PricingProperties properties) {
        this.properties = properties;
    }

    @Override
    public TariffZone supports() {
        return TariffZone.M3;
    }

    @Override
    public long calculatePrice(ParkingRequestDto request) {
        log.debug("M3 Strategy parking pricing calculation for request from {} to {}", request.startTime(), request.endTime());

        LocalDateTime startTime = request.startTime();
        LocalDateTime endTime = request.endTime();

        // Sunday parking is free for the entire session.
        if (isSunday(startTime)) {
            return 0;
        }

        Duration duration = Duration.between(startTime, endTime);

        if (duration.isZero()) {
            return 0;
        }

        // Round up fractional seconds so any positive fraction
        // of a minute is billed as a started minute.
        long durationSeconds = duration.getSeconds()
                + (duration.getNano() > 0 ? 1 : 0);

        if (isDaytime(startTime)) {
            return calculateDaytimePrice(durationSeconds);
        }

        return calculateOutsideDaytimePrice(durationSeconds);
    }

    private long calculateDaytimePrice(long durationSeconds) {
        int freeMinutes = properties.m3FreeMinutes();

        long freeSeconds = freeMinutes * SECONDS_PER_MINUTE;

        if (durationSeconds <= freeSeconds) {
            return 0;
        }

        long paidSeconds = durationSeconds - freeSeconds;
        long startedMinutes = ceilDiv(paidSeconds, SECONDS_PER_MINUTE);
        long daytimeRate = properties.m3PricePerMinuteDaytime();

        return startedMinutes * daytimeRate;
    }

    private long calculateOutsideDaytimePrice(long durationSeconds) {
        long startedMinutes = ceilDiv(durationSeconds, SECONDS_PER_MINUTE);
        long outsideDaytimeRate = properties.m3PricePerMinuteOutsideDaytime();
        return startedMinutes * outsideDaytimeRate;
    }

    private boolean isSunday(LocalDateTime startTime) {
        return startTime.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    private boolean isDaytime(LocalDateTime startTime) {
        int hour = startTime.getHour();
        int daytimeStart = properties.m3DaytimeStart();
        int daytimeEnd = properties.m3DaytimeEnd();
        return hour >= daytimeStart
                && hour < daytimeEnd;
    }

    private long ceilDiv(long value, long divisor) {
        return value / divisor + (value % divisor == 0 ? 0 : 1);
    }
}

