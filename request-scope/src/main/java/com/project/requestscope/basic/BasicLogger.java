package com.project.requestscope.basic;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class BasicLogger {

    private final Logger log = LoggerFactory.getLogger(BasicLogger.class);

    private String uuid;
    private String requestURL;

    @PostConstruct
    public void init() {
        uuid = java.util.UUID.randomUUID().toString();
        log.info("[{}] request scope bean create: {}", uuid, this);
    }

    @PreDestroy
    public void close() {
        log.info("[{}] request scope bean close: {}", uuid, this);
    }

    public void log(String message) {
        log.info("[{}][{}] {}", uuid, requestURL, message);
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }
}
