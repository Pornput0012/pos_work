package sit.backend.controllers;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sit.backend.configurations.JwtProperties;
import sit.backend.dtos.AccessTokenResponseDTO;
import sit.backend.entities.UserAccount;
import sit.backend.repositories.UserAccountRepository;
import sit.backend.services.JwtService;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v2/auth")
public class AuthController {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    private static final long DEFAULT_EXPIRATION = 24 * 60 * 60 * 1000; // 24 hours

    private static final long REFRESH_TOKEN_EXP = DEFAULT_EXPIRATION;


    @PostMapping("/login")
    public ResponseEntity<?> signIn(@RequestBody Map<String, String> body) throws JOSEException {
        String rawEmail = body.get("email") != null ? body.get("email") : "";
        String password = body.get("password") != null ? body.get("password") : "";

        // ✅ Trim เฉพาะหัวท้ายเพื่อกัน user copy/paste ผิดพลาด
        String email = rawEmail.trim();

        // ✅ Validate email format (basic regex)
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return ResponseEntity.status(400).body(Map.of("error", "Email or Password is not valid."));
        }

        Optional<UserAccount> userOpt = userAccountRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "User not found."));
        }

        UserAccount user = userOpt.get();
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            return ResponseEntity.status(403).body(Map.of("error", "You need to active your account before signin in."));
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "Email or Password is incorrect."));
        }

        AccessTokenResponseDTO accessTokenResponseDTO = new AccessTokenResponseDTO();

        accessTokenResponseDTO.setAccess_token(jwtService.generateAccessToken(user.getId(),user.getNickname(),user.getEmail(),user.getAccountType().toUpperCase()));

        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail(), user.getAccountType().toUpperCase(), user.getNickname());
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .maxAge(REFRESH_TOKEN_EXP)
                .path("/")
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(accessTokenResponseDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logoutUser(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) throws ParseException, JOSEException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity
                    .badRequest()
                    .body("No access token");
        }

        String token = authorizationHeader.substring(7);
        jwtService.validateAccessToken(token);

        ResponseCookie deleteRefreshToken = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity
                .noContent()
                .header(HttpHeaders.SET_COOKIE, deleteRefreshToken.toString())
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponseDTO> getAccessTokenFromRefreshToken(@CookieValue(name = "refresh_token", required = false) String refreshToken) throws JOSEException, ParseException {
        jwtService.validateRefreshToken(refreshToken);

        String accessToken = jwtService.getAccessTokenFromRefreshToken(refreshToken);
        AccessTokenResponseDTO dtoResponse = new AccessTokenResponseDTO();
        dtoResponse.setAccess_token(accessToken);

        return ResponseEntity.ok(dtoResponse);
    }



}

