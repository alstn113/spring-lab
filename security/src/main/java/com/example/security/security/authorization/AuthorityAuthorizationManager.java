package com.example.security.security.authorization;

import com.example.security.security.context.Authentication;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class AuthorityAuthorizationManager implements AuthorizationManager {

    private final Set<String> authorities;

    public AuthorityAuthorizationManager(String... authorities) {
        this.authorities = Set.of(authorities);
    }

    public static AuthorizationManager hasAuthority(String authority) {
        return new AuthorityAuthorizationManager(authority);
    }

    @Override
    public AuthorizationDecision authorize(Supplier<Authentication> authentication) {
        boolean isGranted = isGranted(authentication.get(), authorities);

        return new AuthorityAuthorizationDecision(isGranted, createAuthorities(authorities));
    }

    private boolean isGranted(Authentication authentication, Collection<String> authorities) {
        return authentication != null && isAuthenticated(authentication, authorities);
    }

    private boolean isAuthenticated(Authentication authentication, Collection<String> authorities) {
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if (authorities.contains(grantedAuthority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    private Collection<GrantedAuthority> createAuthorities(Collection<String> authorities) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(authorities.size());
        for (String authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority));
        }
        return grantedAuthorities;
    }
}
