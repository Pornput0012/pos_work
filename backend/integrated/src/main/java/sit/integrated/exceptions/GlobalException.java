package sit.integrated.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalException extends RuntimeException {

    @ExceptionHandler(SaleItemNotFound.class)
    public ResponseEntity<Map<String, Object>> handleSaleItemNotFound(SaleItemNotFound saleItemNotFound) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("message", saleItemNotFound.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

}
