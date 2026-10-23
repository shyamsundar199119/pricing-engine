package eu.moonpark.pricingengine.strategy;

import eu.moonpark.pricingengine.model.ParkingRequestDto;
import eu.moonpark.pricingengine.model.TariffZone;
import eu.moonpark.pricingengine.validation.ParkingRequestValidator;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class M3Strategy implements PricingAgent {

    private static final long FREE_MINUTES = 60;
    private static final long DAYTIME_RATE_PER_MINUTE = 2;
    private static final long OUTSIDE_DAYTIME_RATE_PER_MINUTE = 3;
    private static final int DAYTIME_START_HOUR = 8;
    private static final int DAYTIME_END_HOUR = 16;
    private static final long SECONDS_PER_MINUTE = 60;

    @Override
    public TariffZone supports() {
        return TariffZone.M3;
    }

    @Override
    public long calculatePrice(ParkingRequestDto request) {
        ParkingRequestValidator.validate(request, this);

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
        long freeSeconds = FREE_MINUTES * SECONDS_PER_MINUTE;

        if (durationSeconds <= freeSeconds) {
            return 0;
        }

        long paidSeconds = durationSeconds - freeSeconds;
        long startedMinutes = ceilDiv(paidSeconds, SECONDS_PER_MINUTE);

        return startedMinutes * DAYTIME_RATE_PER_MINUTE;
    }

    private long calculateOutsideDaytimePrice(long durationSeconds) {
        long startedMinutes = ceilDiv(durationSeconds, SECONDS_PER_MINUTE);

        return startedMinutes * OUTSIDE_DAYTIME_RATE_PER_MINUTE;
    }

    private boolean isSunday(LocalDateTime startTime) {
        return startTime.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    private boolean isDaytime(LocalDateTime startTime) {
        int hour = startTime.getHour();

        return hour >= DAYTIME_START_HOUR
                && hour < DAYTIME_END_HOUR;
    }

    private long ceilDiv(long value, long divisor) {
        return value / divisor + (value % divisor == 0 ? 0 : 1);
    }
}

