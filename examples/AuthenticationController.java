package org.example.uzgotuje.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.example.uzgotuje.database.entity.auth.User;
import org.example.uzgotuje.services.authorization.*;
import org.example.uzgotuje.services.token.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * REST controller for handling authentication-related requests.
 */
@RestController
@RequestMapping(path = "/auth")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RecaptchaService recaptchaService;

    /**
     * Registers a new user.
     *
     * @param request the registration request containing user details
     * @return a response entity with the registration response and HTTP status
     */
    @PostMapping(path = "/register")
    public ResponseEntity<RegistrationResponse> register(@RequestBody RegistrationRequest request) {
        RegistrationResponse response = authenticationService.register(request);
        if ("Success".equals(response.getMessage()) || "Send new Token".equals(response.getMessage())) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Verifies the reCAPTCHA token.
     *
     * @param token the reCAPTCHA token to verify
     * @return a response entity with the verification result and HTTP status
     */
    @PostMapping("/verifyCaptcha")
    public ResponseEntity<String> verifyCaptcha(@RequestParam String token) {
        boolean isValid = recaptchaService.verifyRecaptcha(token);

        if (isValid) {
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failure", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Confirms the email verification token.
     *
     * @param token the email verification token
     * @return a response entity with the token response and HTTP status
     */
    @GetMapping(path = "/confirm")
    public ResponseEntity<TokenResponse> confirm(@RequestParam("token") String token) {
        TokenResponse response = authenticationService.confirmToken(token);
        if ("Email confirmed".equals(response.getMessage())) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Logs in the user and creates a session cookie.
     *
     * @param loginRequest the login request containing user credentials
     * @param response the HTTP servlet response to add the cookie to
     * @return a response entity with the login result and HTTP status
     */
    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
            String cookieValue = authenticationService.login(loginRequest.getEmail(), loginRequest.getPassword());

            if(Objects.equals(cookieValue, "Invalid credentials")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
            // Set the cookie in the response
            Cookie cookie = new Cookie("SESSION_ID", cookieValue);
            cookie.setHttpOnly(true); // Prevent client-side access to the cookie
            cookie.setPath("/");
            cookie.setMaxAge(2 * 60 * 60); // 2 hours

        System.out.println("Cookie created: " + cookie.getName() + " = " + cookie.getValue());
            response.addCookie(cookie);

            return ResponseEntity.ok("Login successful");
    }

    /**
     * Checks if the session cookie is valid.
     *
     * @param cookieValue the session cookie value
     * @return a response entity with the validation result and HTTP status
     */
    @GetMapping("/check")
    public ResponseEntity<String> checkCookie(@CookieValue(value = "SESSION_ID", required = false) String cookieValue) {
        if (cookieValue != null && authenticationService.validateCookie(cookieValue)) {
            return ResponseEntity.ok("Cookie is valid");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired cookie");
        }
    }

    /**
     * Logs out the user and deletes the session cookie.
     *
     * @param cookieValue the session cookie value
     * @param response the HTTP servlet response to remove the cookie from
     * @return a response entity with the logout result and HTTP status
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(value = "SESSION_ID", required = false) String cookieValue, HttpServletResponse response) {
        if (cookieValue != null) {
            authenticationService.logout(cookieValue);

            // Remove the cookie from the client
            Cookie cookie = new Cookie("SESSION_ID", null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            return ResponseEntity.ok("Logged out successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No active session");
        }
    }

    /**
     * Sends a password reset email.
     *
     * @param email the email request containing the user's email address
     * @return a response entity with the result and HTTP status
     */
    @PostMapping("/resetPasswordEmail")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordEmailRequest email) {
        String response = authenticationService.resetPasswordEmail(email.getEmail());
        if ("Success".equals(response)) {
            return ResponseEntity.ok("Password reset email sent");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email not found");
        }
    }

    /**
     * Resets the user's password.
     *
     * @param token the password reset token
     * @param passwordRequest the password reset request containing the new password
     * @return a response entity with the result and HTTP status
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestBody ResetPasswordRequest passwordRequest) {

        String response = authenticationService.resetPassword(token, passwordRequest.getPassword(), passwordRequest.getRepeatPassword());
        if("Passwords do not match".equals(response)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords do not match");
        }
        if ("Success".equals(response)) {
            return ResponseEntity.ok("Password reset successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token not found");
        }
    }

    /**
     * Retrieves the authenticated user's details.
     *
     * @param cookieValue the session cookie value
     * @return a response entity with the user details and HTTP status
     */
    @GetMapping("/user")
    public ResponseEntity<User> getUsername(@CookieValue(value = "SESSION_ID", required = false) String cookieValue) {
        User user = authenticationService.validateCookieAndGetUser(cookieValue);
        if (cookieValue != null && user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

}
