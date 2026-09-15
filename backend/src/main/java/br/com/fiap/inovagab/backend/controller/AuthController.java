package br.com.fiap.inovagab.backend.controller;

import br.com.fiap.inovagab.backend.dto.auth.LoginRequest;
import br.com.fiap.inovagab.backend.dto.auth.LoginResponse;
import br.com.fiap.inovagab.backend.dto.auth.UserResponse;
import br.com.fiap.inovagab.backend.security.AuthenticatedUser;
import br.com.fiap.inovagab.backend.security.CurrentUser;
import br.com.fiap.inovagab.backend.service.AuthService;
import br.com.fiap.inovagab.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacao", description = "Login e dados do usuario autenticado")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica por e-mail e senha e devolve o JWT")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Perfil do usuario dono do token")
    public ResponseEntity<UserResponse> me() {
        AuthenticatedUser current = CurrentUser.require();
        return ResponseEntity.ok(UserResponse.from(userService.findById(current.getId())));
    }
}
