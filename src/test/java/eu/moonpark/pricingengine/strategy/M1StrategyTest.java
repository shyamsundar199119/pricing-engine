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
class M1StrategyTest {

    @Autowired
    private PricingProperties properties;

    private PricingAgent pricingAgent;

    @BeforeEach
    void setUp() {
        pricingAgent = new M1Strategy(properties);
    }

    private final LocalDateTime startTime =
            LocalDateTime.of(2026, 10, 26, 10, 0);

    private ParkingRequestDto createRequest(
            LocalDateTime start,
            LocalDateTime end) {

        return new ParkingRequestDto(start, end, TariffZone.M1);
    }

    @Test
    void shouldReturnZeroForZeroSeconds() {
        assertEquals(0, pricingAgent.calculatePrice(
                createRequest(startTime, startTime)));
    }

    @Test
    void shouldChargeOneKroneForOneSecond() {
        assertEquals(1, pricingAgent.calculatePrice(
                createRequest(startTime, startTime.plusSeconds(1))));
    }

    @Test
    void shouldChargeOneKroneForThirtySeconds() {
        assertEquals(1, pricingAgent.calculatePrice(
                createRequest(startTime, startTime.plusSeconds(30))));
    }

    @Test
    void shouldChargeOneKroneForFiftyNineSeconds() {
        assertEquals(1, pricingAgent.calculatePrice(
                createRequest(startTime, startTime.plusSeconds(59))));
    }

    @Test
    void shouldChargeOneKroneForExactlyOneMinute() {
        assertEquals(1, pricingAgent.calculatePrice(
                createRequest(startTime, startTime.plusSeconds(60))));
    }

    @Test
    void shouldChargeTwoKronerForSixtyOneSeconds() {
        assertEquals(2, pricingAgent.calculatePrice(
                createRequest(startTime, startTime.plusSeconds(61))));
    }

    @Test
    void shouldChargeTwoKronerForOneMinuteAndOneSecond() {
        assertEquals(2, pricingAgent.calculatePrice(
                createRequest(startTime, startTime.plusMinutes(1).plusSeconds(1))));
    }

    @Test
    void shouldChargeTwoKronerForExactlyTwoMinutes() {
        assertEquals(2, pricingAgent.calculatePrice(
                createRequest(startTime, startTime.plusMinutes(2))));
    }

    @Test
    void shouldChargeThreeKronerForTwoMinutesAndOneSecond() {
        assertEquals(3, pricingAgent.calculatePrice(
                createRequest(startTime, startTime.plusMinutes(2).plusSeconds(1))));
    }

    @Test
    void shouldChargeThirtyKronerForThirtyMinutes() {
        assertEquals(30, pricingAgent.calculatePrice(
                createRequest(startTime, startTime.plusMinutes(30))));
    }

    @Test
    void shouldChargeSixtyKronerForOneHour() {
        assertEquals(60, pricingAgent.calculatePrice(
                createRequest(startTime, startTime.plusHours(1))));
    }

    @Test
    void shouldChargeOneThousandFourHundredFortyKronerForTwentyFourHours() {
        assertEquals(1440, pricingAgent.calculatePrice(
                createRequest(startTime, startTime.plusHours(24))));
    }
}

