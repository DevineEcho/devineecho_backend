package com.example.devineecho.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            System.out.println("Authorization header missing or invalid");
            chain.doFilter(request, response);
            return;
        }

        final String jwt = authorizationHeader.substring(7);
        String username = null;

        try {
            username = jwtUtil.extractUsername(jwt);
            System.out.println("Extracted username: " + username);
        } catch (Exception e) {
            System.out.println("Failed to extract username from token: " + e.getMessage());
            chain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, null);

            if (jwtUtil.isTokenValid(jwt, username)) {
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("Token valid. Authentication set for user: " + username);
            } else {
                System.out.println("Invalid token for user: " + username);
            }
        }
        chain.doFilter(request, response);
    }



    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") || path.startsWith("/api/player/");
    }


}
