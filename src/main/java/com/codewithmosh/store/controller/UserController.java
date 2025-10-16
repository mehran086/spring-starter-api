package com.codewithmosh.store.controller;

import com.codewithmosh.store.dtos.RegisterUserDto;
import com.codewithmosh.store.dtos.UpdateUserDto;
import com.codewithmosh.store.entities.User;
import com.codewithmosh.store.entities.UserDto;
import com.codewithmosh.store.mapper.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@RestController
public class UserController {

    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

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
    public ResponseEntity<UserDto> addUser(@RequestBody RegisterUserDto request
    , UriComponentsBuilder uriBuilder){
        // we cannot directly use userDto as it doesnot contain password
        // we could include password in userDto and put@Jsonignore there , but it only works for
        // serrialization and deserialization

        var user = userMapper.ToEntity(request);
        System.out.println(user);
        userRepository.save(user);
        // we will return a user dto. so we would have to use mapper again
        var userDto = userMapper.toDto(user);
       var uri= uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    // updating basic account info , not password, as password is security
    // thing, we would need to confirm old and new password for that.

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<UserDto> updateUser(
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

}
