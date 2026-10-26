package Agenda.res.SecurityAuth.service;

import Agenda.res.exception.UserAlreadyExistsException;
import Agenda.res.models.TipoDeUser;
import Agenda.res.models.User;
import Agenda.res.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    //profissional e cliente sao criados la nos seus services

    public User criarUsuario(String nome, String email, String senha, TipoDeUser tipo){
        if(userRepository.findByEmailUser(email).isPresent()){
            throw new UserAlreadyExistsException("O usuário ja existe");
        }
        var user = new User();
        String senhaHash= passwordEncoder.encode(senha);
        user.setNomeUser(nome);
        user.setEmailUser(email);
        user.setSenha(senhaHash);
        user.setTipoUser(tipo);
        return userRepository.save(user);

    }
}
