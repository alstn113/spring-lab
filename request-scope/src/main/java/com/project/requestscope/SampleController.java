package com.project.requestscope;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    private final MyLogger myLogger;

    public SampleController(MyLogger myLogger) {
        this.myLogger = myLogger;
    }

    @GetMapping("/request")
    public String request(HttpServletRequest request) {
        myLogger.setRequestURL(request.getRequestURI());
        myLogger.log("controller test");
        return "OK";
    }
}
