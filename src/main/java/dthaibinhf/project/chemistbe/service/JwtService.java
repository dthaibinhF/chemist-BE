package dthaibinhf.project.chemistbe.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

import static dthaibinhf.project.chemistbe.constants.ApplicationEnvironment.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtService {

    @Value(JWT_SECRET_DEFAULT_VALUE)
    String jwtSecret;
    @Value(JWT_EXPIRATION)
    long jwtExpiration;
    @Value(JWT_EXPIRATION_REFRESH_TOKEN)
    long refreshExpiration;


    public String extractUsername(String jwt) {
        return extractClaims(jwt).getSubject();
    }

    public Date extractExpiration(String jwt) {
        return extractClaims(jwt).getExpiration();
    }

//    private <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver){
//        final Claims claims = extractClaims(jwt);
//        //This allows the caller to specify which claim to extract
//        //by providing an appropriate claimsResolver function.
//        return claimsResolver.apply(claims);
//    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return generateToken(claims, userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims,
                              UserDetails userDetails,
                              long expiration) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claims(extraClaims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey()).compact();
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        final String username = extractUsername(jwt);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(jwt);
    }

    private boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }

    private Claims extractClaims(String jwt) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(jwt).getPayload();
    }

    public String extractRole(String jwt) {
        List<String> roles = extractRoles(jwt);
        return roles.isEmpty() ? "PUBLIC" : roles.get(0); // Return primary role (first role)
    }

    public List<String> extractRoles(String jwt) {
        try {
            Claims claims = extractClaims(jwt);
            Object authorities = claims.get("authorities");
            if (authorities instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> authList = (List<String>) authorities;
                return authList.stream()
                        .map(this::extractRoleFromAuthority)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            } else if (authorities != null) {
                String authStr = authorities.toString();
                List<String> roles = new ArrayList<>();
                String role = extractRoleFromAuthority(authStr);
                if (role != null) {
                    roles.add(role);
                }
                return roles;
            }
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public String extractPrimaryRole(String jwt) {
        return extractRole(jwt); // Same as extractRole - returns first role
    }

    public boolean hasRole(String jwt, String targetRole) {
        return extractRoles(jwt).contains(targetRole);
    }

    private String extractRoleFromAuthority(String authority) {
        if (authority == null) return null;
        
        // Handle authority strings like "ROLE_ADMIN", "ADMIN", "[ROLE_ADMIN]", etc.
        String cleanAuth = authority.replaceAll("[\\[\\],\\s]", "");
        if (cleanAuth.startsWith("ROLE_")) {
            return cleanAuth.substring(5); // Remove "ROLE_" prefix
        } else if (cleanAuth.matches("[A-Z_]+")) {
            return cleanAuth;
        }
        return null;
    }

    private SecretKey getSecretKey() {
        byte[] jwtSecretBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(jwtSecretBytes);
    }
}
