package com.codewithmosh.store.filters;

import com.codewithmosh.store.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
   private final JwtService jwtService;


   @Autowired
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            var authHeader = request.getHeader("Authorization");
            if(authHeader==null || !authHeader.startsWith("Bearer")){
                filterChain.doFilter(request,response);
                return;
            }
            var token = authHeader.replace("Bearer ","");
            //if the token is not valid move onto the next filter
        if(!jwtService.validateToken(token)){
            filterChain.doFilter(request,response);
            return;
        }
            // if you reach this point it means the token is valid and move forward
        var authentication = new UsernamePasswordAuthenticationToken(
                jwtService.getEmailFromToken(token),
                null,
                null
        );
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
// ✅ Mark as authenticated explicitly
//        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request,response);
    }
}
