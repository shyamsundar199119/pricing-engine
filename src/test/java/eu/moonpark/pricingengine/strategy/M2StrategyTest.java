package eu.moonpark.pricingengine.strategy;

import eu.moonpark.pricingengine.model.ParkingRequestDto;
import eu.moonpark.pricingengine.exception.InvalidParkingRequestException;
import eu.moonpark.pricingengine.model.TariffZone;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class M2StrategyTest {

    private final PricingAgent pricingAgent = new M2Strategy();

    // Monday
    private final LocalDateTime weekday =
            LocalDateTime.of(2026, 10, 26, 10, 0);

    // Saturday
    private final LocalDateTime weekend =
            LocalDateTime.of(2026, 10, 24, 10, 0);

    private ParkingRequestDto createRequest(
            LocalDateTime start,
            LocalDateTime end) {

        return new ParkingRequestDto(start, end, TariffZone.M2);
    }

    // WEEKDAY TESTS

    @Test
    void shouldReturnZeroForZeroDurationOnWeekday() {
        assertEquals(0, pricingAgent.calculatePrice(
                createRequest(weekday, weekday)));
    }

    @Test
    void shouldChargeOneHundredKronerForOneSecondOnWeekday() {
        assertEquals(100, pricingAgent.calculatePrice(
                createRequest(weekday, weekday.plusSeconds(1))));
    }

    @Test
    void shouldChargeOneHundredKronerForFiftyNineMinutes() {
        assertEquals(100, pricingAgent.calculatePrice(
                createRequest(weekday, weekday.plusMinutes(59))));
    }

    @Test
    void shouldChargeOneHundredKronerForExactlyOneHourOnWeekday() {
        assertEquals(100, pricingAgent.calculatePrice(
                createRequest(weekday, weekday.plusHours(1))));
    }

    @Test
    void shouldChargeTwoHundredKronerForOneHourAndOneSecondOnWeekday() {
        assertEquals(200, pricingAgent.calculatePrice(
                createRequest(weekday, weekday.plusHours(1).plusSeconds(1))));
    }

    @Test
    void shouldChargeTwoHundredKronerForTwoHoursOnWeekday() {
        assertEquals(200, pricingAgent.calculatePrice(
                createRequest(weekday, weekday.plusHours(2))));
    }

    @Test
    void shouldChargeTwoThousandFourHundredKronerForTwentyFourHoursOnWeekday() {
        assertEquals(2400, pricingAgent.calculatePrice(
                createRequest(weekday, weekday.plusHours(24))));
    }

    // WEEKEND TESTS

    @Test
    void shouldReturnZeroForZeroDurationOnWeekend() {
        assertEquals(0, pricingAgent.calculatePrice(
                createRequest(weekend, weekend)));
    }

    @Test
    void shouldChargeTwoHundredKronerForOneSecondOnWeekend() {
        assertEquals(200, pricingAgent.calculatePrice(
                createRequest(weekend, weekend.plusSeconds(1))));
    }

    @Test
    void shouldChargeTwoHundredKronerForFiftyNineMinutesOnWeekend() {
        assertEquals(200, pricingAgent.calculatePrice(
                createRequest(weekend, weekend.plusMinutes(59))));
    }

    @Test
    void shouldChargeTwoHundredKronerForExactlyOneHourOnWeekend() {
        assertEquals(200, pricingAgent.calculatePrice(
                createRequest(weekend, weekend.plusHours(1))));
    }

    @Test
    void shouldChargeFourHundredKronerForOneHourAndOneSecondOnWeekend() {
        assertEquals(400, pricingAgent.calculatePrice(
                createRequest(weekend, weekend.plusHours(1).plusSeconds(1))));
    }

    @Test
    void shouldChargeFourHundredKronerForTwoHoursOnWeekend() {
        assertEquals(400, pricingAgent.calculatePrice(
                createRequest(weekend, weekend.plusHours(2))));
    }

    @Test
    void shouldChargeFourThousandEightHundredKronerForTwentyFourHoursOnWeekend() {
        assertEquals(4800, pricingAgent.calculatePrice(
                createRequest(weekend, weekend.plusHours(24))));
    }

    @Test
    void shouldApplyWeekdayRateWhenSessionCrossesIntoWeekend() {
        // Friday 23:00 to Saturday 01:00
        LocalDateTime start = LocalDateTime.of(2026, 10, 23, 23, 0);
        LocalDateTime end = start.plusHours(2);

        ParkingRequestDto request =
                createRequest(start, end);

        assertEquals(200, pricingAgent.calculatePrice(request));
    }

    @Test
    void shouldApplyWeekendRateWhenSessionCrossesIntoWeekday() {
        // Sunday 23:00 to Monday 01:00
        LocalDateTime start = LocalDateTime.of(2026, 10, 25, 23, 0);
        LocalDateTime end = start.plusHours(2);

        ParkingRequestDto request =
                createRequest(start, end);

        assertEquals(400, pricingAgent.calculatePrice(request));
    }
}

