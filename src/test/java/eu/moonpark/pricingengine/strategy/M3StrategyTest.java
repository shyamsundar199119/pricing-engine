package eu.moonpark.pricingengine.strategy;

import eu.moonpark.pricingengine.config.PricingProperties;
import eu.moonpark.pricingengine.model.ParkingRequestDto;
import eu.moonpark.pricingengine.model.TariffZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class M3StrategyTest {

    @Autowired
    private PricingProperties properties;

    private PricingAgent pricingAgent;

    // Monday, October 26, 2026
    private final LocalDateTime monday =
            LocalDateTime.of(2026, 10, 26, 10, 0);

    // Saturday, October 24, 2026
    private final LocalDateTime saturday =
            LocalDateTime.of(2026, 10, 24, 10, 0);

    // Sunday, October 25, 2026
    private final LocalDateTime sunday =
            LocalDateTime.of(2026, 10, 25, 10, 0);

    @BeforeEach
    void setUp() {
        pricingAgent = new M3Strategy(properties);
    }
    private ParkingRequestDto createRequest(
            LocalDateTime start,
            LocalDateTime end) {

        return new ParkingRequestDto(start, end, TariffZone.M3);
    }

    private ParkingRequestDto createRequest(
            LocalDateTime start,
            LocalDateTime end,
            TariffZone tariffZone) {

        return new ParkingRequestDto(start, end, tariffZone);
    }

    // DAYTIME: MONDAY TO SATURDAY, 08:00-16:00

    @Test
    void shouldReturnZeroForZeroDurationDuringDaytime() {
        LocalDateTime start = LocalDateTime.of(2026, 10, 26, 10, 0);

        assertEquals(0, pricingAgent.calculatePrice(
                createRequest(start, start)));
    }

    @Test
    void shouldReturnZeroForThirtyMinutesDuringDaytime() {
        assertEquals(0, pricingAgent.calculatePrice(
                createRequest(monday, monday.plusMinutes(30))));
    }

    @Test
    void shouldReturnZeroForExactlyOneHourDuringDaytime() {
        assertEquals(0, pricingAgent.calculatePrice(
                createRequest(monday, monday.plusHours(1))));
    }

    @Test
    void shouldChargeTwoKronerForOneMinuteAfterFreeHour() {
        assertEquals(2, pricingAgent.calculatePrice(
                createRequest(monday, monday.plusMinutes(61))));
    }

    @Test
    void shouldChargeTwoKronerForOneSecondAfterFreeHour() {
        assertEquals(2, pricingAgent.calculatePrice(
                createRequest(monday, monday.plusHours(1).plusSeconds(1))));
    }

    @Test
    void shouldChargeFourKronerForTwoMinutesAfterFreeHour() {
        assertEquals(4, pricingAgent.calculatePrice(
                createRequest(monday, monday.plusMinutes(62))));
    }

    @Test
    void shouldChargeOneHundredTwentyKronerForTwoHoursDuringDaytime() {
        assertEquals(120, pricingAgent.calculatePrice(
                createRequest(monday, monday.plusHours(2))));
    }

    @Test
    void shouldApplyDaytimeRateWhenSessionCrossesSixteenHundred() {
        // Starts at 15:30: first hour free, then 30 minutes at 2 NOK/min.
        LocalDateTime start = LocalDateTime.of(2026, 10, 26, 15, 30);

        assertEquals(60, pricingAgent.calculatePrice(
                createRequest(start, start.plusMinutes(90))));
    }

    // OUTSIDE DAYTIME: MONDAY TO SATURDAY

    @Test
    void shouldChargeThreeKronerForOneSecondOutsideDaytime() {
        LocalDateTime start = LocalDateTime.of(2026, 10, 26, 7, 0);

        assertEquals(3, pricingAgent.calculatePrice(
                createRequest(start, start.plusSeconds(1))));
    }

    @Test
    void shouldChargeThreeKronerForExactlyOneMinuteOutsideDaytime() {
        LocalDateTime start = LocalDateTime.of(2026, 10, 26, 7, 0);

        assertEquals(3, pricingAgent.calculatePrice(
                createRequest(start, start.plusMinutes(1))));
    }

    @Test
    void shouldChargeSixKronerForOneMinuteAndOneSecondOutsideDaytime() {
        LocalDateTime start = LocalDateTime.of(2026, 10, 26, 7, 0);

        assertEquals(6, pricingAgent.calculatePrice(
                createRequest(start, start.plusMinutes(1).plusSeconds(1))));
    }

    @Test
    void shouldChargeOneHundredEightyKronerForOneHourOutsideDaytime() {
        LocalDateTime start = LocalDateTime.of(2026, 10, 26, 7, 0);

        assertEquals(180, pricingAgent.calculatePrice(
                createRequest(start, start.plusHours(1))));
    }

    @Test
    void shouldChargeThreeKronerWhenParkingStartsAtSixteenHundred() {
        LocalDateTime start = LocalDateTime.of(2026, 10, 26, 16, 0);

        assertEquals(3, pricingAgent.calculatePrice(
                createRequest(start, start.plusSeconds(1))));
    }

    @Test
    void shouldChargeZeroWhenParkingStartsAtEightHundredAndLastsOneHour() {
        LocalDateTime start = LocalDateTime.of(2026, 10, 26, 8, 0);

        assertEquals(0, pricingAgent.calculatePrice(
                createRequest(start, start.plusHours(1))));
    }

    @Test
    void shouldChargeThreeKronerForOneSecondOnSaturdayEvening() {
        LocalDateTime start = LocalDateTime.of(2026, 10, 24, 20, 0);

        assertEquals(3, pricingAgent.calculatePrice(
                createRequest(start, start.plusSeconds(1))));
    }

    // SUNDAY: FREE

    @Test
    void shouldReturnZeroForOneHourOnSunday() {
        assertEquals(0, pricingAgent.calculatePrice(
                createRequest(sunday, sunday.plusHours(1))));
    }

    @Test
    void shouldReturnZeroForTwentyFourHoursOnSunday() {
        assertEquals(0, pricingAgent.calculatePrice(
                createRequest(sunday, sunday.plusHours(24))));
    }

    @Test
    void shouldReturnZeroForParkingStartingOnSundayEvening() {
        LocalDateTime start = LocalDateTime.of(2026, 10, 25, 23, 0);

        assertEquals(0, pricingAgent.calculatePrice(
                createRequest(start, start.plusHours(2))));
    }
}

