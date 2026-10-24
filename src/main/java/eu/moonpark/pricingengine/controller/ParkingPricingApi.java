package eu.moonpark.pricingengine.controller;

import eu.moonpark.pricingengine.dto.RateRequest;
import eu.moonpark.pricingengine.dto.RateResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

public interface ParkingPricingApi {

    @GetMapping("/rate")
    @Operation(
            summary = "Calculate parking price",
            description = "Calculates the parking fee based on the start time, end time, and tariff zone."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Parking price calculated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    RateResponseDto getRate(
            @Valid @ModelAttribute RateRequest request
    );
}
