package Agenda.res.SecurityAuth;

import Agenda.res.SecurityAuth.dtos.JWTUserData;
import Agenda.res.SecurityAuth.service.TokenService;
import Agenda.res.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private TokenService tokenService;
    private UserRepository userRepository;


    public SecurityFilter(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizedHeader = request.getHeader("Authorization");

        if(authorizedHeader != null && authorizedHeader.startsWith("Bearer ")){
            String token = authorizedHeader.replace("Bearer ","");
            Optional<JWTUserData> optUser = tokenService.validateToken(token);

            if(optUser.isPresent()){
                String email = optUser.get().email();

                UserDetails user = userRepository.findByEmailUser(email).orElse(null);

                if(user != null){
                    var authentication = new UsernamePasswordAuthenticationToken(user,null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request,response);
    }
}
