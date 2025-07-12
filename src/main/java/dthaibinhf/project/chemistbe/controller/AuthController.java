package dthaibinhf.project.chemistbe.controller;


import dthaibinhf.project.chemistbe.dto.AccountDTO;
import dthaibinhf.project.chemistbe.dto.request.AuthenticationRequest;
import dthaibinhf.project.chemistbe.dto.request.RegisterRequest;
import dthaibinhf.project.chemistbe.dto.response.ApiResponse;
import dthaibinhf.project.chemistbe.dto.response.AuthenticationResponse;
import dthaibinhf.project.chemistbe.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AccountDTO> register(
            @RequestBody RegisterRequest registerRequest
    ) {
        return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AccountDTO>> getAccount() {
        AccountDTO account =  authenticationService.getAuthentication();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Success", account));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody AuthenticationRequest authenticationRequest
            ) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }
}
