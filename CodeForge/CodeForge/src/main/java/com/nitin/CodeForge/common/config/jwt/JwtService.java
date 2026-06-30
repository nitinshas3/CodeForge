package com.nitin.CodeForge.common.config.jwt;

import com.nitin.CodeForge.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;


@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // ##############    Generate JWT      ##############################
    //to generate token there is a class , we only need to uild object and fill in field values right?
    //-> no jwt is not a POJO , only thing that seperates this from pojo is signature other than that all same like jwts is a class we need to set the field values like payload etc then after that sign it
    //3 parts right , first header contains signuatrue algorithm like what algorithm we have used to sign the key
    // payload contains issued at epriy and email
    // and last signature conatins key
    public String generateToken(User user) {

        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    //################     Extract email from JWT ##############################
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    //######################## Validate JWT ############################
    public boolean isTokenValid(String token, User user) {

        String email = extractEmail(token);

        return email.equals(user.getEmail())
                && !isTokenExpired(token);
    }

    // ###############   Check expiration   #########################
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration)
                .before(new Date());
    }

    // Extract any claim
    private <T> T extractClaim(String token, Function<Claims, T> resolver) {

        Claims claims = extractAllClaims(token);

        return resolver.apply(claims);
    }

    //############ Parse all claims ######################
    //so simple , jwts.parser creats a claim object , claims is a wrapper class , it only contains the payload of the jwt token, jwt parser and jwt builder are differnt, bulder buidls the token , this also verifies teh signature , only signature nothing more like exprity and all , this is the extra work this does , should have been done by validate function but this does it as extra work ,
    private Claims extractAllClaims(String token) {

        return Jwts
                .parser()
                .verifyWith((SecretKey) getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ########## Signing key
    // our secret is in string , jwt wont accept it , had to be converted to key object so
    // takes secret from application properties which is base64 encoded and turns it to rawbytes (decode)
    // then converts to key object which will be ready to undergo hmacs algorithm
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
