package exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.example.wallet.exception.GlobalExceptionHandler;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    @Test
    void handleRuntimeException_ShouldReturnBadRequestResponse() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ResponseEntity<Map<String, String>> response =
                handler.handleRuntimeException(new RuntimeException("Insufficient balance"));

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("BAD_REQUEST", response.getBody().get("code"));
        assertEquals("Insufficient balance", response.getBody().get("message"));
    }
}