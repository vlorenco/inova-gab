package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.dto.auth.LoginRequest;
import br.com.fiap.inovagab.backend.dto.auth.LoginResponse;
import br.com.fiap.inovagab.backend.dto.auth.UserResponse;
import br.com.fiap.inovagab.backend.model.User;
import br.com.fiap.inovagab.backend.repository.UserRepository;
import br.com.fiap.inovagab.backend.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new BadCredentialsException("Credenciais invalidas."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Credenciais invalidas.");
        }

        String token = jwtService.generateToken(user);
        return new LoginResponse(token, "Bearer", jwtService.getExpirationMs(), UserResponse.from(user));
    }
}
