package com.monteBravo.be.Exceptions;

import com.monteBravo.be.Config.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageResponse> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse("Error: " + e.getMessage()));
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<MessageResponse> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body( new MessageResponse("Error: " + ex.getMessage()));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error interno del servidor: " + e.getMessage()));
    }
    @ExceptionHandler(UsuarioException.class)
    public ResponseEntity<MessageResponse> handleValidationException(UsuarioException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Error: " + e.getMessage()));
    }
}
