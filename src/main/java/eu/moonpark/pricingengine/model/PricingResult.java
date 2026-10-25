package eu.moonpark.pricingengine.model;

import java.util.Currency;

public record PricingResult(
        long price,
        Currency currency
) {
}
