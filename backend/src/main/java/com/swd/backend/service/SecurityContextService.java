package com.swd.backend.service;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SecurityContextService {
    public String extractUsernameFromContext(SecurityContext securityContext) throws ClassCastException {
        UserDetails userDetails = (UserDetails) securityContext.getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }
}
