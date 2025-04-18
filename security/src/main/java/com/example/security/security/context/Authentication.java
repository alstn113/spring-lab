package com.example.security.security.context;

import com.example.security.security.authorization.GrantedAuthority;
import java.util.Collection;

public interface Authentication {

    Object principal();

    Collection<? extends GrantedAuthority> getAuthorities();
}
