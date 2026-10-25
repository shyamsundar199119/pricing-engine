package eu.moonpark.pricingengine.controller;

import eu.moonpark.pricingengine.exception.InvalidParkingRequestException;
import eu.moonpark.pricingengine.model.ParkingRequestDto;
import eu.moonpark.pricingengine.model.PricingResult;
import eu.moonpark.pricingengine.service.ParkingPricingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Currency;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParkingPricingController.class)
class ParkingPricingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ParkingPricingService pricingService;

    @Test
    void shouldReturnPriceForM1Request() throws Exception {
        when(pricingService.calculatePrice(any())).thenReturn(new PricingResult(60L, Currency.getInstance("NOK")));

        mockMvc.perform(get("/v1/rate")
                        .param("startTime", "2026-10-26T10:00:00")
                        .param("endTime", "2026-10-26T11:00:00")
                        .param("tariffZone", "M1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(60));
    }

    @Test
    void shouldReturnPriceForM2Request() throws Exception {
        when(pricingService.calculatePrice(any())).thenReturn(new PricingResult(100L, Currency.getInstance("NOK")));

        mockMvc.perform(get("/v1/rate")
                        .param("startTime", "2026-10-26T10:00:00")
                        .param("endTime", "2026-10-26T11:00:00")
                        .param("tariffZone", "M2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(100));
    }

    @Test
    void shouldReturnPriceForM3Request() throws Exception {
        when(pricingService.calculatePrice(any())).thenReturn(new PricingResult(0L, Currency.getInstance("NOK")));

        mockMvc.perform(get("/v1/rate")
                        .param("startTime", "2026-10-26T10:00:00")
                        .param("endTime", "2026-10-26T11:00:00")
                        .param("tariffZone", "M3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(0));
    }

    @Test
    void shouldReturnBadRequestWhenStartTimeIsMissing() throws Exception {
        mockMvc.perform(get("/v1/rate")
                        .param("endTime", "2026-10-26T11:00:00")
                        .param("tariffZone", "M1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0]").exists());
    }

    @Test
    void shouldReturnBadRequestWhenEndTimeIsMissing() throws Exception {
        mockMvc.perform(get("/v1/rate")
                        .param("startTime", "2026-10-26T10:00:00")
                        .param("tariffZone", "M1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0]").exists());
    }

    @Test
    void shouldReturnBadRequestWhenTariffZoneIsMissing() throws Exception {
        mockMvc.perform(get("/v1/rate")
                        .param("startTime", "2026-10-26T10:00:00")
                        .param("endTime", "2026-10-26T11:00:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0]").exists());
    }

    @Test
    void shouldReturnBadRequestWhenStartTimeIsInvalid() throws Exception {
        mockMvc.perform(get("/v1/rate")
                        .param("startTime", "not-a-date")
                        .param("endTime", "2026-10-26T11:00:00")
                        .param("tariffZone", "M1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0]").exists());
    }

    @Test
    void shouldReturnBadRequestWhenTariffZoneIsInvalid() throws Exception {
        mockMvc.perform(get("/v1/rate")
                        .param("startTime", "2026-10-26T10:00:00")
                        .param("endTime", "2026-10-26T11:00:00")
                        .param("tariffZone", "M9"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0]").exists());
    }

    @Test
    void shouldReturnBadRequestWhenEndTimeIsBeforeStartTime()
            throws Exception {

        when(pricingService.calculatePrice(any(ParkingRequestDto.class)))
                .thenThrow(new InvalidParkingRequestException(
                        "End time cannot be before start time"));

        mockMvc.perform(get("/v1/rate")
                        .param("startTime", "2026-10-26T11:00:00")
                        .param("endTime", "2026-10-26T10:00:00")
                        .param("tariffZone", "M1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("End time cannot be before start time"));
    }
}