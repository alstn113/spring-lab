package com.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    private static final Logger log = LoggerFactory.getLogger(DemoController.class);

    @GetMapping("/all")
    public String all() {
        log.trace("all");
        log.debug("all");
        log.info("all");
        log.warn("all");
        log.error("all");

        return "all";
    }

    @GetMapping("/trace")
    public String trace() {
        log.trace("trace");

        return "trace";
    }

    @GetMapping("/debug")
    public String debug() {
        log.debug("debug");

        return "debug";
    }

    @GetMapping("/info")
    public String info() {
        log.info("info");

        return "info";
    }

    @GetMapping("/warn")
    public String warn() {
        log.warn("warn");

        return "warn";
    }

    @GetMapping("/error")
    public String error() {
        log.error("error");

        return "error";
    }
}
