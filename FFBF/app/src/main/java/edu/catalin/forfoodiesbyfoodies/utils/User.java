package edu.catalin.forfoodiesbyfoodies.utils;

public class User {

    private String firstName;
    private String surname;
    private String email;
    private String password;
    private String role;

    public User(String firstName, String surname, String email, String password, String role) {
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
