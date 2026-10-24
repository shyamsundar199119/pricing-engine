package eu.moonpark.pricingengine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Calculated parking price response")
public record RateResponseDto(
        @Schema(
                description = "Total parking fee in Norwegian kroner (NOK)",
                example = "60"
        )
        long price) {
}
