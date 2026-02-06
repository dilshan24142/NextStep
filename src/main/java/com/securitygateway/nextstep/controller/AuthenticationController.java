package com.securitygateway.nextstep.controller;

import com.securitygateway.nextstep.payload.requests.*;
import com.securitygateway.nextstep.payload.responses.GeneralAPIResponse;
import com.securitygateway.nextstep.payload.responses.RegisterResponse;
import com.securitygateway.nextstep.service.AuthenticationService;
import com.securitygateway.nextstep.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to handle Authentication and OTP Verification.
 * Allowed Origin: http://localhost:5173 (React Frontend)
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173") // Frontend එකට අවසර ලබා දීම
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    // 1. User Registration
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Register request received for email: {}", registerRequest.getEmail());
        return authenticationService.registerUser(registerRequest);
    }

    // 2. Initial Registration Verification (Via Email Link/OTP)
    @PostMapping(value = "/verify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifyRegistration(@Valid @RequestBody RegisterVerifyRequest registerVerifyRequest) {
        log.info("Registration verification request received for email: {}", registerVerifyRequest.getEmail());
        return authenticationService.verifyUserRegistration(registerVerifyRequest);
    }

    // 3. User Login
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request received for email: {}", loginRequest.getEmail());
        return authenticationService.loginUser(loginRequest);
    }

    // 4. Send/Resend OTP (For Forgot Password or Verification)
    @PostMapping(value = "/send-otp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendOtp(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        log.info("OTP request received for email: {}", forgotPasswordRequest.getEmail());
        return authenticationService.resendOtp(forgotPasswordRequest);
    }

    // 5. Verify OTP
    @PostMapping(value = "/verify-otp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody RegisterVerifyRequest registerVerifyRequest) {
        log.info("OTP verification request received for email: {}", registerVerifyRequest.getEmail());
        return authenticationService.verifyOtp(registerVerifyRequest);
    }

    // 6. Reset Password using OTP
    @PostMapping(value = "/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        log.info("Password reset request received for email: {}", resetPasswordRequest.getEmail());
        return authenticationService.resetPassword(resetPasswordRequest);
    }

    // 7. Refresh Token Generation
    @GetMapping("/getRefreshToken")
    public ResponseEntity<?> refreshToken(@RequestParam(name = "refreshToken") String refreshToken) {
        log.info("Refresh token request received");
        return jwtService.generateAccessTokenFromRefreshToken(refreshToken);
    }

    // 8. Health Check / Keep-Alive Endpoint
    @PostMapping("/hello")
    public ResponseEntity<?> hello() {
        log.info("Health check request received");
        return new ResponseEntity<>(
                GeneralAPIResponse.builder()
                        .message("API is active and running.")
                        .build(),
                HttpStatus.OK
        );
    }
}