package eu.moonpark.pricingengine.model;

import java.time.LocalDateTime;

public record ParkingRequestDto(
        LocalDateTime startTime,
        LocalDateTime endTime,
        TariffZone tariffZone
) {
}
