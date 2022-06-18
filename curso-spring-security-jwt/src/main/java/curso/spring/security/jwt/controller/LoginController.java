package curso.spring.security.jwt.controller;

import curso.spring.security.jwt.dtos.Login;
import curso.spring.security.jwt.dtos.Sessao;
import curso.spring.security.jwt.model.User;
import curso.spring.security.jwt.repository.UserRepository;
import curso.spring.security.jwt.security.JWTCreator;
import curso.spring.security.jwt.security.JWTObject;
import curso.spring.security.jwt.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class LoginController {
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private SecurityConfig securityConfig;
    @Autowired
    private UserRepository repository;

    @PostMapping("/login")
    public Sessao logar(@RequestBody Login login){
        User user = repository.findByUsername(login.getUsername());
        if(user!=null){
            boolean passwordOk = encoder.matches(login.getPassword(), user.getPassword());
            if(!passwordOk) {
                throw new RuntimeException("Senha inválida para o login: " + login.getUsername());
            }
            Sessao sessao = new Sessao();
            sessao.setLogin(user.getUsername());

            JWTObject jwtObject = new JWTObject();
            jwtObject.setIssuedAt(new Date(System.currentTimeMillis()));
            jwtObject.setExpiration(new Date(System.currentTimeMillis() + SecurityConfig.EXPIRATION));
            jwtObject.setRoles(user.getRoles());
            sessao.setToken(JWTCreator.create(SecurityConfig.PREFIX, SecurityConfig.KEY,jwtObject));
            return sessao;
        }else{
            throw new RuntimeException("Erro ao tentar fazer login");
        }

    }
}
