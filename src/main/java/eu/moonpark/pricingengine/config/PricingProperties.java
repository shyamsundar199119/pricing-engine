package eu.moonpark.pricingengine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.LocalTime;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "pricing")
public record PricingProperties(

        @DefaultValue("60")
        long m1PricePerHour,

        @DefaultValue("100")
        long m2WeekdayPricePerHour,

        @DefaultValue("200")
        long m2WeekendPricePerHour,

        @DefaultValue("2")
        long m3PricePerMinuteDaytime,

        @DefaultValue("3")
        long m3PricePerMinuteOutsideDaytime,

        @DefaultValue("60")
        int m3FreeMinutes,

        @DefaultValue("8")
        int m3DaytimeStart,

        @DefaultValue("16")
        int m3DaytimeEnd

) {}