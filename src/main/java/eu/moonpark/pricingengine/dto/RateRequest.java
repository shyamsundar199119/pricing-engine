package eu.moonpark.pricingengine.dto;

import eu.moonpark.pricingengine.model.TariffZone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record RateRequest(
        @Schema(
                description = "Parking start date and time in ISO-8601 format",
                example = "2026-10-26T10:00:00"
        )
        @NotNull(message = "Start time is required")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startTime,

        @Schema(
                description = "Parking end date and time in ISO-8601 format",
                example = "2026-10-26T11:00:00"
        )
        @NotNull(message = "End time is required")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime endTime,

        @Schema(
                description = "Parking tariff zone",
                example = "M1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Tariff zone is required")
        TariffZone tariffZone
) {
}
