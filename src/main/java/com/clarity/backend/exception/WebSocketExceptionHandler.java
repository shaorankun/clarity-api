package com.clarity.backend.exception;

import com.clarity.backend.dto.ErrorResponse;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class WebSocketExceptionHandler {

    @MessageExceptionHandler(RuntimeException.class)
    @SendToUser("/queue/errors")
    public ErrorResponse handleRuntimeException(RuntimeException e) {
        return new ErrorResponse(400, e.getMessage());
    }
}