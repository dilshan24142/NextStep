package com.securitygateway.nextstep.service;


import com.securitygateway.nextstep.Dtos.requests.*;
import com.securitygateway.nextstep.Dtos.responses.RegisterResponse;
import org.springframework.http.ResponseEntity;




public interface AuthenticationService {
      ResponseEntity<RegisterResponse> registerUser(RegisterRequest registerRequest);
      ResponseEntity<?> verifyUserRegistration(RegisterVerifyRequest registerVerifyRequest);
      ResponseEntity<?> loginUser(LoginRequest loginRequest);
      ResponseEntity<?> resendOtp(ForgotPasswordRequest forgotPasswordRequest);
      ResponseEntity<?> verifyOtp(RegisterVerifyRequest registerVerifyRequest);
      ResponseEntity<?> resetPassword(ResetPasswordRequest resetPasswordRequest);
     ResponseEntity<?> myProfile(ForgotPasswordRequest forgotPasswordRequest);


}
