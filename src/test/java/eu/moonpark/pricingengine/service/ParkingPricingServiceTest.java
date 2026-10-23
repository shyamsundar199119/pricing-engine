package eu.moonpark.pricingengine.service;

import eu.moonpark.pricingengine.model.ParkingRequestDto;
import eu.moonpark.pricingengine.exception.InvalidParkingRequestException;
import eu.moonpark.pricingengine.strategy.PricingAgent;
import eu.moonpark.pricingengine.model.TariffZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ParkingPricingServiceTest {

    private PricingAgent m1Strategy;
    private PricingAgent m2Strategy;
    private PricingAgent m3Strategy;

    private ParkingPricingService pricingService;

    @BeforeEach
    void setUp() {
        m1Strategy = mock(PricingAgent.class);
        m2Strategy = mock(PricingAgent.class);
        m3Strategy = mock(PricingAgent.class);

        when(m1Strategy.supports()).thenReturn(TariffZone.M1);
        when(m2Strategy.supports()).thenReturn(TariffZone.M2);
        when(m3Strategy.supports()).thenReturn(TariffZone.M3);

        pricingService = new ParkingPricingService(
                List.of(m1Strategy, m2Strategy, m3Strategy)
        );
    }

    @Test
    void shouldUseM1StrategyForM1Request() {
        ParkingRequestDto request = createRequest(TariffZone.M1);

        when(m1Strategy.calculatePrice(request)).thenReturn(60L);

        long price = pricingService.calculatePrice(request);

        assertEquals(60L, price);
        verify(m1Strategy).calculatePrice(request);

        verify(m2Strategy, never()).calculatePrice(any());
        verify(m3Strategy, never()).calculatePrice(any());
    }

    @Test
    void shouldUseM2StrategyForM2Request() {
        ParkingRequestDto request = createRequest(TariffZone.M2);

        when(m2Strategy.calculatePrice(request)).thenReturn(100L);

        long price = pricingService.calculatePrice(request);

        assertEquals(100L, price);
        verify(m2Strategy).calculatePrice(request);

        verify(m1Strategy, never()).calculatePrice(any());
        verify(m3Strategy, never()).calculatePrice(any());
    }

    @Test
    void shouldUseM3StrategyForM3Request() {
        ParkingRequestDto request = createRequest(TariffZone.M3);

        when(m3Strategy.calculatePrice(request)).thenReturn(0L);

        long price = pricingService.calculatePrice(request);

        assertEquals(0L, price);
        verify(m3Strategy).calculatePrice(request);

        verify(m1Strategy, never()).calculatePrice(any());
        verify(m2Strategy, never()).calculatePrice(any());
    }

    @Test
    void shouldThrowExceptionWhenEndTimeIsBeforeStartTime() {
        ParkingRequestDto request = new ParkingRequestDto(
                LocalDateTime.of(2026, 10, 26, 11, 0),
                LocalDateTime.of(2026, 10, 26, 10, 0),
                TariffZone.M1
        );

        assertThrows(
                InvalidParkingRequestException.class,
                () -> pricingService.calculatePrice(request)
        );
    }

    @Test
    void shouldThrowExceptionWhenNoStrategySupportsTariffZone() {
        ParkingPricingService serviceWithoutStrategies =
                new ParkingPricingService(List.of());

        ParkingRequestDto request = createRequest(TariffZone.M1);

        assertThrows(
                InvalidParkingRequestException.class,
                () -> serviceWithoutStrategies.calculatePrice(request)
        );
    }

    private ParkingRequestDto createRequest(TariffZone tariffZone) {
        return new ParkingRequestDto(
                LocalDateTime.of(2026, 10, 26, 10, 0),
                LocalDateTime.of(2026, 10, 26, 11, 0),
                tariffZone
        );
    }
}