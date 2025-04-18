package com.example.security.security.authorization;

import com.example.security.security.context.Authentication;
import java.util.function.Supplier;

@FunctionalInterface
public interface AuthorizationManager {

    AuthorizationDecision authorize(Supplier<Authentication> authentication);
}
