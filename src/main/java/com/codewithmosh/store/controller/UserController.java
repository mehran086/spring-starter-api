package com.codewithmosh.store.controller;

import com.codewithmosh.store.dtos.ChangePasswordRequest;
import com.codewithmosh.store.dtos.RegisterUserDto;
import com.codewithmosh.store.dtos.UpdateUserDto;
import com.codewithmosh.store.entities.Role;
import com.codewithmosh.store.entities.User;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.mapper.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@RequestMapping("/users")
@RestController
public class UserController {

    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/getUsers")

    public List<UserDto> getAllUsers(
            @RequestHeader(name="x-auth-token",required = false) String authToken,
            @RequestParam(defaultValue = "",required = false,
                    name = "sort"
    ) String sort){
        System.out.println(authToken);
        if(!Set.of("email","name").contains(sort)){
            sort="name";
        }
        return userRepository.findAll(Sort.by(sort).descending()).stream().
                map(userMapper::toDto)
                .toList();
    }
    @GetMapping("/User/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id){
//        return userRepository.findById(id).orElseThrow(null);
      var user = userRepository.findById(id).orElse(null);
      if(user==null) {
//          return new ResponseEntity<>(HttpStatus.NOT_FOUND);
          return ResponseEntity.notFound().build();
      }
      return ResponseEntity.ok(user);

    }
    @PostMapping("/createUser")
    public ResponseEntity<?> registerUser(@Valid
                                               @RequestBody RegisterUserDto request
    , UriComponentsBuilder uriBuilder){
        // we cannot directly use userDto as it doesnot contain password
        // we could include password in userDto and put@Jsonignore there , but it only works for
        // serrialization and deserialization
//        userRepository


        // check if the user with this email id already exists
        if(userRepository.existsByEmail(request.getEmail()) ){
            return ResponseEntity.badRequest().body(Map.of("message","email already exists"));
        }
        var user = userMapper.ToEntity(request);
        System.out.println(user);
        // hash password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);  // setting role as user as of now.
        userRepository.save(user);
        // we will return a user dto. so we would have to use mapper again
        var userDto = userMapper.toDto(user);
       var uri= uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    // updating basic account info , not password, as password is security
    // thing, we would need to confirm old and new password for that.

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<UserDto> updateUser(@Valid
            @PathVariable(name="id") Long id,
            @RequestBody UpdateUserDto request
            ){
        var user = userRepository.findById(id).orElse(null);
        if(user==null){
            return ResponseEntity.notFound().build();
        }
        userMapper.update(request,user);
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        var user= userRepository.findById(id).orElse(null);
        if(user==null){
            System.out.println("user not found");
            return ResponseEntity.notFound().build();
        }
//            userRepository.deleteById(id);
            userRepository.delete(user);
        return ResponseEntity.noContent().build();

    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request,
                                                 @PathVariable Long id ){
        var user= userRepository.findById(id).orElse(null);
        if(user==null){
            System.out.println("user not found");
            return ResponseEntity.notFound().build();
        }

        // check that the old password given by user is the actual password
        if(! user.getPassword().equals(request.getOldPassword())){
//            return ResponseEntity.badRequest().build();
            // a better approach would be to use unauthorized , but it is not present in this
            //so
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }


//    @PostMapping("/auth/login")
//    public void loginUser(@RequestBody User request){
//        var dbuser = userRepository.findByEmail(request.getEmail());
////        System.out.println(dbuser);
////        if(!passwordEncoder.encode(request.getPassword()).equals(dbuser.getPassword())){
//        if(!passwordEncoder.matches(request.getPassword(), dbuser.getPassword())){
//            System.out.println(passwordEncoder.encode(request.getPassword()));
//            System.out.println(dbuser.getPassword());
//            System.out.println(request.getPassword());
//            System.out.println("Incorrect password");
//        }
//        else{
//            System.out.println("Correct password");
//        }
//    }

}
//$2a$10$f4vwHX9hXmz6Om.KHTMCx.eTB607Y3HofLoPmnImQpRbU9jbqa9pq