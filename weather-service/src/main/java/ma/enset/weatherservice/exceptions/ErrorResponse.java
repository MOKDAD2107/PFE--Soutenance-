package ma.enset.weatherservice.exceptions;


import java.time.LocalDateTime;

public record ErrorResponse(
        int status ,
        String message ,
        String path,
        LocalDateTime dateTime
) {
    public static ErrorResponse of(int status, String message, String path) {
        return new ErrorResponse(status, message, path, LocalDateTime.now());
    }
}
