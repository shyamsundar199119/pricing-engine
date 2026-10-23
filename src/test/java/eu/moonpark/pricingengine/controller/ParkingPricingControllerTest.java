package eu.moonpark.pricingengine.controller;

import eu.moonpark.pricingengine.exception.InvalidParkingRequestException;
import eu.moonpark.pricingengine.model.ParkingRequestDto;
import eu.moonpark.pricingengine.service.ParkingPricingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
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
        when(pricingService.calculatePrice(any())).thenReturn(60L);

        mockMvc.perform(get("/rate")
                        .param("startTime", "2026-10-26T10:00:00")
                        .param("endTime", "2026-10-26T11:00:00")
                        .param("tariffZone", "M1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(60));
    }

    @Test
    void shouldReturnPriceForM2Request() throws Exception {
        when(pricingService.calculatePrice(any())).thenReturn(100L);

        mockMvc.perform(get("/rate")
                        .param("startTime", "2026-10-26T10:00:00")
                        .param("endTime", "2026-10-26T11:00:00")
                        .param("tariffZone", "M2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(100));
    }

    @Test
    void shouldReturnPriceForM3Request() throws Exception {
        when(pricingService.calculatePrice(any())).thenReturn(0L);

        mockMvc.perform(get("/rate")
                        .param("startTime", "2026-10-26T10:00:00")
                        .param("endTime", "2026-10-26T11:00:00")
                        .param("tariffZone", "M3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(0));
    }

    @Test
    void shouldReturnBadRequestWhenStartTimeIsMissing() throws Exception {
        mockMvc.perform(get("/rate")
                        .param("endTime", "2026-10-26T11:00:00")
                        .param("tariffZone", "M1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenEndTimeIsMissing() throws Exception {
        mockMvc.perform(get("/rate")
                        .param("startTime", "2026-10-26T10:00:00")
                        .param("tariffZone", "M1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenTariffZoneIsMissing() throws Exception {
        mockMvc.perform(get("/rate")
                        .param("startTime", "2026-10-26T10:00:00")
                        .param("endTime", "2026-10-26T11:00:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenStartTimeIsInvalid() throws Exception {
        mockMvc.perform(get("/rate")
                        .param("startTime", "not-a-date")
                        .param("endTime", "2026-10-26T11:00:00")
                        .param("tariffZone", "M1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenTariffZoneIsInvalid() throws Exception {
        mockMvc.perform(get("/rate")
                        .param("startTime", "2026-10-26T10:00:00")
                        .param("endTime", "2026-10-26T11:00:00")
                        .param("tariffZone", "M9"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenEndTimeIsBeforeStartTime()
            throws Exception {

        when(pricingService.calculatePrice(any(ParkingRequestDto.class)))
                .thenThrow(new InvalidParkingRequestException(
                        "End time cannot be before start time"
                ));

        mockMvc.perform(get("/rate")
                        .param("startTime", "2026-10-26T11:00:00")
                        .param("endTime", "2026-10-26T10:00:00")
                        .param("tariffZone", "M1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("End time cannot be before start time"));
    }
}
