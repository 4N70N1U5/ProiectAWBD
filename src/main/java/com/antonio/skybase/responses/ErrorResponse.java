package com.antonio.skybase.responses;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String path;
    private List<String> messages;

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String path, List<String> messages) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.path = path;
        this.messages = messages;
    }
}
