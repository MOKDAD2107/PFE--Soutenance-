package ma.enset.iotservice.exceptions;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String path,
        String message,
        LocalDateTime dateTime
) {
    public static ErrorResponse of(int status, String path, String message) {
        return new ErrorResponse(status, path, message, LocalDateTime.now());
    }

}
