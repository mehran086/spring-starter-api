package com.codewithmosh.store.filters;

import com.codewithmosh.store.entities.Role;
import com.codewithmosh.store.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

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
                filterChain.doFilter(request,response);// move onto the next filter.
                return;
            }
            var token = authHeader.replace("Bearer ","");
            //if the token is not valid move onto the next filter
            var jwt = jwtService.parseToken(token);
//        if(jwt==null || jwt.isExpired()){
        if(!jwtService.validateToken(token)){
        filterChain.doFilter(request,response);
            return;
        }
            // if you reach this point it means the token is valid and move forward
//        var userIdString = jwtService.getUserIdFromToken(token); // returns String from JWT
        var userId = jwt.getUserId(); // returns String from JWT
//        var userId = Long.valueOf(userIdString);
//        var role = jwtService.getRoleFromToken(token);
        var role = jwt.getRole();
        var authentication = new UsernamePasswordAuthenticationToken(
                userId,
                null,
//                List.of(Role.ADMIN.name())
                List.of(new SimpleGrantedAuthority("ROLE_"+role))

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
