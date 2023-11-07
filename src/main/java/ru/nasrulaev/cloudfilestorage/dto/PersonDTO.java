package ru.nasrulaev.cloudfilestorage.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import ru.nasrulaev.cloudfilestorage.security.Password;

public class PersonDTO {
    @Email
    private String email;

    @Password
    @Size(min = 8, max = 30, message = "Password length must be between 8 and 30 chars")
    private String password;

    public PersonDTO() {
    }

    public PersonDTO(String email, String password) {
        this.email = email;
        this.password = password;
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
