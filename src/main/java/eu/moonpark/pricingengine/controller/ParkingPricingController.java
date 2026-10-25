package eu.moonpark.pricingengine.controller;

import eu.moonpark.pricingengine.dto.RateRequest;
import eu.moonpark.pricingengine.dto.RateResponseDto;
import eu.moonpark.pricingengine.model.ParkingRequestDto;
import eu.moonpark.pricingengine.model.PricingResult;
import eu.moonpark.pricingengine.service.ParkingPricingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class ParkingPricingController implements ParkingPricingApi {

    private final ParkingPricingService pricingService;

    public ParkingPricingController(ParkingPricingService pricingService) {
        this.pricingService = pricingService;
    }

    @Override
    public RateResponseDto getRate(@Valid @ModelAttribute RateRequest request) {
        ParkingRequestDto parkingRequest = new ParkingRequestDto(
                request.startTime(),
                request.endTime(),
                request.tariffZone()
        );

        PricingResult result = pricingService.calculatePrice(parkingRequest);
        return new RateResponseDto(result.price());
    }
}
