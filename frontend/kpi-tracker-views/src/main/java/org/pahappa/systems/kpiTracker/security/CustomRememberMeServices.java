package org.pahappa.systems.kpiTracker.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Custom Remember Me Services that sets cookie path to "/" for better
 * compatibility
 */
public class CustomRememberMeServices extends TokenBasedRememberMeServices {

    public CustomRememberMeServices(String key, UserDetailsService userDetailsService) {
        super();
        setKey(key);
        setUserDetailsService(userDetailsService);
    }

    @Override
    protected void setCookie(String[] tokens, int maxAge, HttpServletRequest request, HttpServletResponse response) {
        // Create the cookie with path set to "/"
        Cookie cookie = new Cookie(getCookieName(), encodeCookie(tokens));
        cookie.setMaxAge(maxAge);
        cookie.setPath("/"); // Set path to root for better compatibility
        cookie.setSecure(false); // Allow on HTTP for localhost
        cookie.setHttpOnly(false); // Allow JavaScript access if needed

        response.addCookie(cookie);
    }
}
