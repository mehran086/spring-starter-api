package com.codewithmosh.store.controller;

import com.codewithmosh.store.dtos.JwtResponse;
import com.codewithmosh.store.dtos.LoginRequest;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.mapper.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import com.codewithmosh.store.services.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private  AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserMapper userMapper;

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody  LoginRequest request){


        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // we have to generate token with id , but in request we only have email and password
         var user=   userRepository.findByEmail(request.getEmail()).orElseThrow(()->
                 new UsernameNotFoundException("Invalid email or password"));
//           var token = jwtService.generateToken(request.getEmail());
           var token = jwtService.generateToken(user);
            //  Authentication successful
            return ResponseEntity.ok(new JwtResponse(token));

        } catch (BadCredentialsException e) {
            //  Invalid credentials
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
            throw  e;
        }

    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException(){

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // checking whether a token is valid that is sent by the client
    @PostMapping("/validate")
    public boolean validate(@RequestHeader("Authorization") String authHeader){
//        var token = authHeader.substring(7); // to skip "bearer "
//          You could also use
        System.out.println("Validate called ");
        var token = authHeader.replace("Bearer ","");
        return jwtService.validateToken(token);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(){

        //Extracting the current principal.
        System.out.println("in me");
      var authentication=  SecurityContextHolder.getContext().getAuthentication(); // we set it up in jwtAuthentication filter
//         var email =(String) authentication.getPrincipal();  // in our implementation we stored email as authentication object
         var userID =(Long) authentication.getPrincipal();  // in our implementation we stored id  as authentication object now instead of email.

        System.out.println("The email is : "+userID);
        var user=  userRepository.findById(userID).orElse(null);
      if(user==null){
          return ResponseEntity.notFound().build();
      }

      return ResponseEntity.ok(userMapper.toDto(user));
    }
}
