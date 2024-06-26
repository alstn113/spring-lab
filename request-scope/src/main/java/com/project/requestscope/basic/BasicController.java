package com.project.requestscope.basic;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {

    private final BasicLogger basicLogger;

    public BasicController(BasicLogger basicLogger) {
        this.basicLogger = basicLogger;
    }

    @GetMapping("/request")
    public String request(HttpServletRequest request) {
        basicLogger.setRequestURL(request.getRequestURI());
        basicLogger.log("controller test");
        return "OK";
    }
}
