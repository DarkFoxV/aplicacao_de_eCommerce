package com.compass.application.security;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.compass.application.services.UserService;
import com.compass.application.services.exceptions.ObjectNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    TokenService tokenService;

    @Autowired
    UserService userService;

    @Autowired
    public SecurityFilter(HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException,
            ObjectNotFoundException, TokenExpiredException {
        String token = recoverToken(request);
        try {
            if (token != null) {
                String email = tokenService.validateToken(token);
                UserDetails user = userService.findByEmail(email);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }

    private String recoverToken(HttpServletRequest request) {
        String authToken = request.getHeader("Authorization");
        if (authToken == null || authToken.isEmpty()) {
            return null;
        }
        return authToken.replace("Bearer", "").trim();
    }

}
