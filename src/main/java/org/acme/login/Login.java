package org.acme.login;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class Login {
    public String name;
    public String password;
    private final static String segredo = "b8338e24f11f4692a95738fe2e893c2ab8338e24f11f46";
    
    public Login() {}

    private static Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(segredo);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String createToken(String name, String password, String userRole) {
        // validar se o usuario existe e se a senha da match

        final Date dtCriacao = new Date();
        final Date dtExpiracao = new Date(dtCriacao.getTime() + 1000 * 60 * 15);

        HashMap<String, String> claims = new HashMap<String, String>();
        claims.put("perfil", userRole);

        var jwtToken = Jwts.builder()
            .setIssuedAt(dtCriacao)
            .setExpiration(dtExpiracao)
            .setClaims(claims)
            .signWith(getKey())
            .compact();
            
        return jwtToken;
    }

    public static Jws<Claims> validateToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getKey())
            .build()
            .parseClaimsJws(token);
    }
}
