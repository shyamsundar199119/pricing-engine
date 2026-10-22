package eu.moonpark.pricingengine.exception;

public class InvalidParkingRequestException extends RuntimeException {
    public InvalidParkingRequestException(String message) {
        super(message);
    }
}