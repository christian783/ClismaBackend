package io.app.clisma_backend.service;

import io.app.clisma_backend.config.JwtService;
import io.app.clisma_backend.domain.User;
import io.app.clisma_backend.model.AuthenticationRequest;
import io.app.clisma_backend.model.AuthenticationResponse;
import io.app.clisma_backend.model.RegisterRequest;
import io.app.clisma_backend.repos.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        if (repository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new ValidationException("Username already exists");
        }
        if (repository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new ValidationException("Email already exists");
        }

            User user = User.builder()
                    .username(registerRequest.getUsername())
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(registerRequest.getRole())
                    .build();
            repository.save(user);
            String jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder().token(jwtToken).build();
        }


        public AuthenticationResponse login(AuthenticationRequest authenticationRequest){
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    )
            );

            var user = repository.findByUsername(authenticationRequest.getUsername()).orElseThrow(
                    () -> new RuntimeException("User not found")
            );
            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder().token(jwtToken).build();
        }
    }