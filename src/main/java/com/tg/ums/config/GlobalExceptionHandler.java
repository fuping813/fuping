package com.tg.ums.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "操作失败: " + e.getMessage());
        response.put("errorDetails", e.toString());
        
        // 打印完整堆栈信息到日志
        e.printStackTrace();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
