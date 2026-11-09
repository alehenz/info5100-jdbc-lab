/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utility;

import java.time.LocalDate;
import java.time.Period;
import model.User;

/**
 * The input rules for a user. The create form and the Update button share these
 * rules so they cannot drift apart.
 */
public class UserValidator {

    private static final String NAME_PATTERN = "[\\p{L} '\\-]+";

    // Returns a message describing the first problem found, or null when the user is valid
    public static String validate(User u) {
        String firstName = u.getFirstName();
        if (firstName == null || firstName.trim().isEmpty()) {
            return "Please provide your first name";
        }
        if (!firstName.trim().matches(NAME_PATTERN)) {
            return "First name can only contain letters, spaces, hyphens, and apostrophes";
        }

        String lastName = u.getLastName();
        if (lastName == null || lastName.trim().isEmpty()) {
            return "Please provide your last name";
        }
        if (!lastName.trim().matches(NAME_PATTERN)) {
            return "Last name can only contain letters, spaces, hyphens, and apostrophes";
        }

        LocalDate dateOfBirth = u.getDateOfBirth();
        if (dateOfBirth == null || Period.between(dateOfBirth, LocalDate.now()).getYears() < 18) {
            return "Please select a valid date of birth. Must be 18 or older";
        }

        String gender = u.getGender();
        if (gender == null || gender.trim().isEmpty()) {
            return "Please select your gender";
        }

        String phone = u.getPhone();
        if (phone == null || !phone.matches("\\d{3}-\\d{3}-\\d{4}")) {
            return "Invalid phone number format. \nPlease use the following format: 123-456-7890";
        }

        String email = u.getEmail();
        if (email == null || email.trim().isEmpty()) {
            return "Please provide your email";
        }
        if (!email.trim().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            return "Please enter a valid email address, for example name@example.com";
        }

        String continent = u.getContinent();
        if (continent == null || continent.trim().isEmpty()) {
            return "Please select an option in the continent field";
        }

        String hobbies = u.getHobbies();
        if (hobbies == null || hobbies.trim().isEmpty()) {
            return "Please enter at least one hobby";
        }

        return null;
    }
}
