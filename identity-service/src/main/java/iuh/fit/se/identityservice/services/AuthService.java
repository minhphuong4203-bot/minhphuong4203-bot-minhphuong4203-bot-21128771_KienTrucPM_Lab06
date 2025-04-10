package iuh.fit.se.identityservice.services;

import iuh.fit.se.identityservice.dtos.SignInRequest;
import iuh.fit.se.identityservice.dtos.SignUpRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> signIn(SignInRequest signInRequest);
    ResponseEntity<?> signUp(SignUpRequest signUpRequest);
}