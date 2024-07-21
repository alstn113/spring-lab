package com.project.auth.ui;

import com.project.auth.infra.security.Accessor;
import com.project.auth.infra.security.BindAuth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthTestController {

    @GetMapping("/auth/required")
    public ResponseEntity<AccessorResponse> testAuthRequired(@BindAuth Accessor accessor) {
        return ResponseEntity.ok(new AccessorResponse(accessor.id(), accessor.isGuest()));
    }


    @GetMapping("/auth/not-required")
    public ResponseEntity<AccessorResponse> testAuthNotRequired(@BindAuth(require = false) Accessor accessor) {
        return ResponseEntity.ok(new AccessorResponse(accessor.id(), accessor.isGuest()));
    }

    @GetMapping("/auth/public")
    public ResponseEntity<String> testPublic() {
        return ResponseEntity.ok("ok");
    }
}
