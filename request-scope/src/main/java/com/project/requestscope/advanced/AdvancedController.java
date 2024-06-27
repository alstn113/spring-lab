package com.project.requestscope.advanced;

import com.project.requestscope.advanced.infra.security.Auth;
import com.project.requestscope.advanced.infra.security.BindAuth;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdvancedController {

    @GetMapping("/a")
    public String testArgumentResolver(@BindAuth Auth auth) {
        return "ok";
    }

    @GetMapping("/check/a")
    public String testInterceptor() {
        return "ok";
    }

    @GetMapping("/check/b")
    public String testArgumentResolverAndInterceptor(@BindAuth Auth auth) {
        return "ok";
    }
}
