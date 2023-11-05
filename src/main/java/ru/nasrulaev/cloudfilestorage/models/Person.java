package ru.nasrulaev.cloudfilestorage.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import ru.nasrulaev.cloudfilestorage.security.Password;

@Entity
@Table
public class Person {
    @Id
    @Column
    private long id;

    @Column(unique = true)
    @Email
    private String email;

    @Column
    @Password
    private String password;

    public Person() {
    }

    public Person(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Person(long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
