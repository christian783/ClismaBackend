package io.app.clisma_backend.service;

import io.app.clisma_backend.config.JwtService;
import io.app.clisma_backend.domain.RefreshToken;
import io.app.clisma_backend.domain.User;
import io.app.clisma_backend.domain.enums.UserRole;
import io.app.clisma_backend.model.*;
import io.app.clisma_backend.repos.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
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
                    .role(registerRequest.getRole() != null ? registerRequest.getRole() : UserRole.AUTHORITY)
                    .build();
            repository.save(user);
            String jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder().token(jwtToken).build();
    }


        public LoginResponse login(AuthenticationRequest authenticationRequest){
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    )
            );

            var user = repository.findByUsername(authenticationRequest.getUsername()).orElseThrow(
                    () -> new RuntimeException("User not found")
            );
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
            var jwtToken = jwtService.generateToken(user);
            return LoginResponse.builder()
                    .token(jwtToken)
                    .refreshToken(refreshToken.getToken())
                    .build();
        }

        public TokenRefreshResponse refreshToken(RefreshTokenRequest request){
            String requestRefreshToken = request.getRefreshToken();

            return refreshTokenService.findByToken(requestRefreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        String token = jwtService.generateToken((UserDetails) user);
                        return new TokenRefreshResponse(token, requestRefreshToken);
                    })
                    .orElseThrow(() -> new ValidationException("Refresh token is not in database!"));
        }

        public String logoutUser(RefreshTokenRequest request){
            String requestRefreshToken = request.getRefreshToken();
            return refreshTokenService.findByToken(requestRefreshToken).map(refreshToken -> {
                refreshTokenService.deleteByUserId(refreshToken.getUser().getId());
                return "Log out successful!";
            }).orElseThrow(() -> new ValidationException("Refresh token is not in database!"));
        }


}