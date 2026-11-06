package back_conecta.controllers;

import back_conecta.dto.AuthResponse;
import back_conecta.dto.CadastroUsuarioRequest;
import back_conecta.dto.ForgotPasswordRequest;
import back_conecta.dto.LoginRequest;
import back_conecta.dto.MensagemResponse;
import back_conecta.dto.ResetPasswordRequest;
import back_conecta.dto.VerifyRecoveryCodeRequest;
import back_conecta.dto.RecoveryVerificationResponse;
import back_conecta.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> cadastrar(@Valid @RequestBody CadastroUsuarioRequest request) {
        AuthResponse resposta = authService.cadastrar(request);
        return ResponseEntity.status(201).body(resposta);
    }

    @PostMapping("/login")
    public AuthResponse autenticar(@Valid @RequestBody LoginRequest request) {
        return authService.autenticar(request);
    }

    @PostMapping("/forgot-password")
    public MensagemResponse solicitarRecuperacao(@Valid @RequestBody ForgotPasswordRequest request) {
        return authService.solicitarRecuperacao(request);
    }

    @PostMapping("/verify-recovery-code")
    public RecoveryVerificationResponse verificarCodigoRecuperacao(
            @Valid @RequestBody VerifyRecoveryCodeRequest request
    ) {
        return authService.verificarCodigoRecuperacao(request);
    }

    @PostMapping("/reset-password")
    public MensagemResponse redefinirSenha(@Valid @RequestBody ResetPasswordRequest request) {
        return authService.redefinirSenha(request);
    }
}
