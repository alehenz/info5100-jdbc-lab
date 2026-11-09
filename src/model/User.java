/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;

/**
 *
 * @author alehenz
 */
public class User {

    private int userId;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private int age;
    private String phone;
    private String email;
    private String continent;
    private String photoPath; // optional, can be null
    private String experience;
    private String hobbies;

    // === Constructor ===
    public User() {

    }

    // === Getters & Setters ===
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public int getAge() {
        return age;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getContinent() {
        return continent;
    }

    public String getExperience() {
        return experience;
    }

    public String getHobbies() {
        return hobbies;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    // === toString (excludes photo) ===
    @Override
    public String toString() {
        return String.format(
                "User Profile:\nFirst Name: %s \nLast Name: %s \nGender: %s\nDate of birth (yyyy-mm-dd): %s \nAge: %d\nPhone: %s\nEmail: %s\nContinent: %s\nExperience: %s\nHobbies: %s\nPhoto Path: %s",
                firstName, lastName, gender, dateOfBirth, age, phone, email, continent, experience, hobbies, photoPath
        );
    }
}
