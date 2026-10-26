package Agenda.res.SecurityAuth.service;


import Agenda.res.SecurityAuth.dtos.JWTUserData;
import Agenda.res.models.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
public class TokenService {

    @Value("${JWT_SECRET}")
    private String secret;

    public String generateToken(User user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("EasyAgenda")
                    .withSubject(user.getEmailUser())
                    .withClaim("userId", user.getId())
                    .withClaim("role",user.getTipoUser().toString())
                    .withClaim("nomeUser", user.getNomeUser())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        }
        catch (JWTCreationException e){
            return "";
        }
    }

    public Optional<JWTUserData> validateToken(String token){

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            DecodedJWT decode = JWT.require(algorithm)
                    .build().verify(token);

            return Optional.of(JWTUserData.builder()
                    .id(decode.getClaim("userId").asLong())
                    .email(decode.getSubject())
                    .role(decode.getClaim("role").asString())
                    .build());
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }

    private Instant genExpirationDate() {
        return Instant.now().plus(2, ChronoUnit.HOURS);
    }


}
