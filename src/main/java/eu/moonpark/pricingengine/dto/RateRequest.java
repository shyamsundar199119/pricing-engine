package eu.moonpark.pricingengine.dto;

import eu.moonpark.pricingengine.model.TariffZone;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record RateRequest(

        @NotNull(message = "Start time is required")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startTime,

        @NotNull(message = "End time is required")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime endTime,

        @NotNull(message = "Tariff zone is required")
        TariffZone tariffZone
) {
}
