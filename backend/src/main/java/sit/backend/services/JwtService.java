package sit.backend.services;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.backend.configurations.JwtProperties;
import sit.backend.dtos.JwtClaimDTO;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;

@Service
public class JwtService {
    private final JwtProperties jwtProperties;
    private static final long DEFAULT_EXPIRATION = 24 * 60 * 60 * 1000; // 24 hours

    private static final long ACCESS_TOKEN_EXP = 30 * 60 * 1000; // 30 นาที
    private static final long REFRESH_TOKEN_EXP = DEFAULT_EXPIRATION;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateAccessToken(Integer userId, String nickname, String email, String role) throws JOSEException {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXP);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer("https://intproj24.sit.kmutt.ac.th/kk3/")
                .issueTime(now)
                .expirationTime(expiryDate)
                .claim("nickname", nickname)
                .claim("id", userId)
                .claim("email", email)
                .claim("role", role)
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        signedJWT.sign(new MACSigner(jwtProperties.getSecretAccessToken().getBytes(StandardCharsets.UTF_8)));

        return signedJWT.serialize();
    }

    public String generateRefreshToken(Integer userId, String email,String role,String nickName) throws JOSEException {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXP);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(email)
                .issueTime(now)
                .expirationTime(expiryDate)
                .claim("id", userId)
                .claim("role", role)
                .claim("nickName", nickName)
                .issuer("https://intproj24.sit.kmutt.ac.th/kk3/")
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        signedJWT.sign(new MACSigner(jwtProperties.getSecretRefreshToken().getBytes(StandardCharsets.UTF_8)));

        return signedJWT.serialize();
    }

    public void validateRefreshToken(String token) throws JOSEException, ParseException {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No refresh token");


        }
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(jwtProperties.getSecretRefreshToken().getBytes(StandardCharsets.UTF_8));

        if (!signedJWT.verify(verifier)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid refresh token");
        }

        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expirationTime == null || new Date().after(expirationTime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "token expired");
        }
    }

    public String getAccessTokenFromRefreshToken(String refreshToken)
            throws ParseException, JOSEException {
        validateRefreshToken(refreshToken);

        SignedJWT signedJWT = SignedJWT.parse(refreshToken);
        Integer userId = ((Long) signedJWT.getJWTClaimsSet().getClaim("id")).intValue();
        String email = signedJWT.getJWTClaimsSet().getSubject();
        String role = signedJWT.getJWTClaimsSet().getClaim("role").toString();
        String nickname = signedJWT.getJWTClaimsSet().getClaim("nickName").toString();

        return generateAccessToken(userId, nickname, email, role);
    }

    public void validateAccessToken(String token) throws JOSEException, ParseException {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Access token is required");
        }
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(jwtProperties.getSecretAccessToken().getBytes(StandardCharsets.UTF_8));

        if (!signedJWT.verify(verifier)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid access token");
        }

        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expirationTime == null || new Date().after(expirationTime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Access token expired");
        }
    }

    public JwtClaimDTO extractUserClaims(String token) throws ParseException {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        SignedJWT signedJWT = SignedJWT.parse(token);
        String email = signedJWT.getJWTClaimsSet().getClaim("email").toString();
        String userType = signedJWT.getJWTClaimsSet().getClaim("role").toString();
        Long user_id = (Long) signedJWT.getJWTClaimsSet().getClaim("id");

        Integer user_id_int = user_id.intValue();
        JwtClaimDTO emailVerifyDTO = new JwtClaimDTO();
        emailVerifyDTO.setEmail(email);
        emailVerifyDTO.setRole(userType);
        emailVerifyDTO.setUserId(user_id_int);

        return emailVerifyDTO;
    }
}
