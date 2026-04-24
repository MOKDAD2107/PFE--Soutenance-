package ma.enset.weatherservice.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "ma.enset.weather-service.controller")
public class GlobalExceptionHandler {
    // 404 - Ressource Introuvable
   @ExceptionHandler(RessourceNotFoundException.class)
   public ResponseEntity<ErrorResponse> handleNotfound(RessourceNotFoundException exception, HttpServletRequest request) {
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.of(404,exception.getMessage(),request.getRequestURI()));
   }

   // 409 - location deja existante
    @ExceptionHandler(LocationAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleConflict(LocationAlreadyExistsException exception, HttpServletRequest request) {
       return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.of(409, exception.getMessage(), request.getRequestURI()));
    }

    // 502 - API Extern indisponible
    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApi(ExternalApiException exception, HttpServletRequest request) {
       return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ErrorResponse.of(502, exception.getMessage(), request.getRequestURI()));
    }

    // 500 - Fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric (Exception exception, HttpServletRequest request) {
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.of(500,"Erreur intern du serveur",request.getRequestURI()));
    }

    // 400 - Erreur de validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException exception,
                                                               HttpServletRequest request) {
       // creation d'un champ <nom_du_champ, message_d'erreur>
       // on récupère le resultat de liaison (getBindingResult()),
        // et la liste de toutes les erreurs trouves(getAllErrors())
       Map<String,String> fielderrors =new HashMap<>();
       exception.getBindingResult().getAllErrors().forEach((error)->{
           String field =((FieldError)error).getField();
           fielderrors.put(field,error.getDefaultMessage());
       });

       Map<String,Object> body = new HashMap<>();
       body.put("status",400);
       body.put("message","Erreur de validation");
       body.put("errors",fielderrors);
       body.put("path",request.getRequestURI());
       return ResponseEntity.badRequest().body(body);

    }
}
