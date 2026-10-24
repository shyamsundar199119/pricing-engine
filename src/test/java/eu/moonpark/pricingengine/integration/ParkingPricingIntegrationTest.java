
package eu.moonpark.pricingengine.integration;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ParkingPricingIntegrationTest {

    @LocalServerPort
    private int port;

    private io.restassured.specification.RequestSpecification request() {
        return RestAssured.given().baseUri("http://localhost").port(port);
    }

    // ---------- Successful pricing ----------

    @Test
    void shouldCalculateM1Price() {
        request()
                .queryParam("startTime", "2026-10-26T10:00:00")
                .queryParam("endTime", "2026-10-26T11:00:00")
                .queryParam("tariffZone", "M1")
                .when()
                .get("/v1/rate")
                .then()
                .statusCode(200)
                .body("price", equalTo(60));
    }

    @Test
    void shouldCalculateM2WeekdayPrice() {
        request()
                .queryParam("startTime", "2026-10-26T10:00:00")
                .queryParam("endTime", "2026-10-26T11:00:00")
                .queryParam("tariffZone", "M2")
                .when()
                .get("/v1/rate")
                .then()
                .statusCode(200)
                .body("price", equalTo(100));
    }

    @Test
    void shouldCalculateM2WeekendPrice() {
        request()
                .queryParam("startTime", "2026-10-24T10:00:00")
                .queryParam("endTime", "2026-10-24T11:00:00")
                .queryParam("tariffZone", "M2")
                .when()
                .get("/v1/rate")
                .then()
                .statusCode(200)
                .body("price", equalTo(200));
    }

    @Test
    void shouldReturnZeroForM3FirstFreeHour() {
        request()
                .queryParam("startTime", "2026-10-26T10:00:00")
                .queryParam("endTime", "2026-10-26T11:00:00")
                .queryParam("tariffZone", "M3")
                .when()
                .get("/v1/rate")
                .then()
                .statusCode(200)
                .body("price", equalTo(0));
    }

    @Test
    void shouldReturnZeroForM3Sunday() {
        request()
                .queryParam("startTime", "2026-10-25T10:00:00")
                .queryParam("endTime", "2026-10-25T12:00:00")
                .queryParam("tariffZone", "M3")
                .when()
                .get("/v1/rate")
                .then()
                .statusCode(200)
                .body("price", equalTo(0));
    }

    // ---------- Tariff boundaries and rounding ----------

    @Test
    void shouldRoundM1UpToNextStartedMinute() {
        request()
                .queryParam("startTime", "2026-10-26T10:00:00")
                .queryParam("endTime", "2026-10-26T10:01:01")
                .queryParam("tariffZone", "M1")
                .when()
                .get("/v1/rate")
                .then()
                .statusCode(200)
                .body("price", equalTo(2));
    }

    @Test
    void shouldRoundM2UpToNextStartedHour() {
        request()
                .queryParam("startTime", "2026-10-26T10:00:00")
                .queryParam("endTime", "2026-10-26T11:00:01")
                .queryParam("tariffZone", "M2")
                .when()
                .get("/v1/rate")
                .then()
                .statusCode(200)
                .body("price", equalTo(200));
    }

    @Test
    void shouldChargeM3AfterFirstFreeHour() {
        request()
                .queryParam("startTime", "2026-10-26T10:00:00")
                .queryParam("endTime", "2026-10-26T11:01:00")
                .queryParam("tariffZone", "M3")
                .when()
                .get("/v1/rate")
                .then()
                .statusCode(200)
                .body("price", equalTo(2));
    }

    @Test
    void shouldChargeM3OutsideDaytimeRate() {
        request()
                .queryParam("startTime", "2026-10-26T16:00:00")
                .queryParam("endTime", "2026-10-26T16:01:00")
                .queryParam("tariffZone", "M3")
                .when()
                .get("/v1/rate")
                .then()
                .statusCode(200)
                .body("price", equalTo(3));
    }

    // ---------- Invalid requests ----------

    @Test
    void shouldRejectMissingStartTime() {
        request()
                .queryParam("endTime", "2026-10-26T11:00:00")
                .queryParam("tariffZone", "M1")
                .when()
                .get("/v1/rate")
                .then()
                .statusCode(400)
                .body("errors[0]", equalTo("Start time is required"));
    }

    @Test
    void shouldRejectMissingEndTime() {
        request()
                .queryParam("startTime", "2026-10-26T10:00:00")
                .queryParam("tariffZone", "M1")
                .when()
                .get("/v1/rate")
                .then()
                .statusCode(400)
                .body("errors[0]", equalTo("End time is required"));
    }

    @Test
    void shouldRejectMissingTariffZone() {
        request()
                .queryParam("startTime", "2026-10-26T10:00:00")
                .queryParam("endTime", "2026-10-26T11:00:00")
                .when()
                .get("/v1/rate")
                .then()
                .statusCode(400)
                .body("errors[0]", equalTo("Tariff zone is required"));
    }

    @Test
    void shouldRejectInvalidStartTimeFormat() {
        request()
                .queryParam("startTime", "not-a-date")
                .queryParam("endTime", "2026-10-26T11:00:00")
                .queryParam("tariffZone", "M1")
                .when()
                .get("/v1/rate")
                .then()
                .statusCode(400)
                .body("errors[0]", notNullValue());
    }

    @Test
    void shouldRejectInvalidTariffZone() {
        request()
                .queryParam("startTime", "2026-10-26T10:00:00")
                .queryParam("endTime", "2026-10-26T11:00:00")
                .queryParam("tariffZone", "M9")
                .when()
                .get("/v1/rate")
                .then()
                .statusCode(400)
                .body("errors[0]", notNullValue());
    }

    @Test
    void shouldRejectEndTimeBeforeStartTime() {
        request()
                .queryParam("startTime", "2026-10-26T11:00:00")
                .queryParam("endTime", "2026-10-26T10:00:00")
                .queryParam("tariffZone", "M1")
                .when()
                .get("/v1/rate")
                .then()
                .statusCode(400)
                .body("errors[0]",
                        equalTo("End time cannot be before start time"));
    }
}

