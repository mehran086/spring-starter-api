package com.codewithmosh.store.controller;

import com.codewithmosh.store.config.JwtConfig;
import com.codewithmosh.store.dtos.JwtResponse;
import com.codewithmosh.store.dtos.LoginRequest;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.mapper.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import com.codewithmosh.store.services.JwtService;
import com.codewithmosh.store.services.TokenBlackListService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    Logger logger = LoggerFactory.getLogger(AuthController.class);
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

   @Autowired
   private TokenBlackListService tokenBlackListService;

    @Autowired
    private JwtConfig jwtConfig;
    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * you are return access token via response
     * and refresh token via cookie.
     * @param request
     * @param httpServletResponse
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody  LoginRequest request,
            HttpServletResponse httpServletResponse
    ){


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
//          var token = jwtService.generateToken(request.getEmail());
           var accessToken = jwtService.generateAccessToken(user).toString();
           var refreshToken = jwtService.generateRefreshToken(user).toString();

           // we will use refresh
            // Cookie to send this refresh token.
           var refreshCookie = new Cookie("refreshToken" , refreshToken);
           refreshCookie.setHttpOnly(true); // cannot be accessed by javascript.
//            refreshCookie.setPath("/auth/refresh"); doesn't reflect cookie in postman.
//            refreshCookie.setPath("/auth/refresh");   only send to refresh.
            refreshCookie.setPath("/auth"); // sends to all inside /auth/**
            refreshCookie.setMaxAge(jwtConfig.getRefreshTokenExpiration()); // 7days same as token expiration
            refreshCookie.setSecure(true);

            httpServletResponse.addCookie(refreshCookie);
            //  Authentication successful
            return ResponseEntity.ok(new JwtResponse(accessToken));

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
//      var authentication=  SecurityContextHolder.getContext().getAuthentication(); // we set it up in jwtAuthentication filter
////         var email =(String) authentication.getPrincipal();  // in our implementation we stored email as authentication object
//         var userID =(Long) authentication.getPrincipal();  // in our implementation we stored id  as authentication object now instead of email.
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userID;

        if (principal instanceof String) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//            logger.error("userId is a string with value {}",principal);
//            userID = Long.valueOf((String) principal);
//            System.out.println(userID); // if its anonymous users return
        } else if (principal instanceof Long) {
            userID = (Long) principal;
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        System.out.println("The email is : "+userID);
        var user=  userRepository.findById(userID).orElse(null);
      if(user==null){
          return ResponseEntity.notFound().build();
      }

      return ResponseEntity.ok(userMapper.toDto(user));
    }

    /**
     * Takes in refresh token as a cookie
     * and generate access token and gives Access token in response
     *
     * @param refreshToken
     * @return
     */
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
            @CookieValue(value="refreshToken") String refreshToken
    ){
//        if(!jwtService.validateToken(refreshToken)){
        var jwt = jwtService.parseToken(refreshToken);
//        if(jwt==null || jwt.isExpired()){
        if(!jwtService.validateToken(refreshToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
//        var userId = jwtService.getUserIdFromToken(refreshToken);
        var user = userRepository.findById(jwt.getUserId()).orElseThrow(()->new UsernameNotFoundException("user not found"));
        var accessToken = jwtService.generateAccessToken(user);
        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

    @PostMapping ("/logout")
    public  ResponseEntity<?> logout(
            @CookieValue(value="refreshToken") String refreshToken
    ,@RequestHeader("Authorization") String authHeader)
    {
        //first check whether the user that is trying to logout is logged in ,lol
        var jwt = jwtService.parseToken(refreshToken);
//        if(jwt==null || jwt.isExpired()){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
        var accesstoken = authHeader.replace("Bearer ","");

        if(!jwtService.validateToken(refreshToken) || !jwtService.validateToken(accesstoken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // now mark it as blacklisted.
        tokenBlackListService.blacklistToken(refreshToken,jwt.getExpiration()/1000);
        tokenBlackListService.blacklistToken(accesstoken,jwt.getExpiration()/1000);
        logger.info("Blacklist the refresh toke {}",refreshToken);
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // delete cookie
        cookie.setSecure(true);

        return ResponseEntity.ok().build();
    }
}
