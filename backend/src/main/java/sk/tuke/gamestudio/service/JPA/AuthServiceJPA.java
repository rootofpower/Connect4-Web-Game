package sk.tuke.gamestudio.service.JPA;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import sk.tuke.gamestudio.dto.UserDTO;
import sk.tuke.gamestudio.dto.requests.LoginRequest;
import sk.tuke.gamestudio.dto.requests.RegisterRequest;
import sk.tuke.gamestudio.dto.responses.AuthResponse;
import sk.tuke.gamestudio.entity.User;
import sk.tuke.gamestudio.entity.enums.Role;
import sk.tuke.gamestudio.mapper.UserMapper;
import sk.tuke.gamestudio.security.JwtService;
import sk.tuke.gamestudio.service.interfaces.AuthService;
import sk.tuke.gamestudio.service.interfaces.UserService;

@Service
@Transactional
public class AuthServiceJPA implements AuthService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public AuthServiceJPA(UserService userService,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public UserDTO register(RegisterRequest registerRequest) {
        if (userService.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        if (userService.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER);

        User savedUser = userService.saveUser(user);
        return userMapper.toUserDTO(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getIdentifier(),
                        loginRequest.getPassword()));
        User userDetails = (User) authentication.getPrincipal();

        String jwtToken = jwtService.generateToken(userDetails);
        UserDTO userDTO = userMapper.toUserDTO(userDetails);
        return new AuthResponse(jwtToken, userDTO);
    }
}
