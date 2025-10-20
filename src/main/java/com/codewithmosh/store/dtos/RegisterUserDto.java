package com.codewithmosh.store.dtos;

import com.codewithmosh.store.validation.Lowercase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterUserDto {
    @NotBlank(message="Name is required")
    @Size(max=255, message = "Name must be less than 255 characters")
    private  String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message="Email must be valid")
    @Lowercase
    private  String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min=6 , max=20 ,message = "Message must be between 6 to 20 characters")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
