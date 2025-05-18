package sk.tuke.gamestudio.service.interfaces;

import sk.tuke.gamestudio.dto.UserDTO;
import sk.tuke.gamestudio.dto.requests.LoginRequest;
import sk.tuke.gamestudio.dto.requests.RegisterRequest;
import sk.tuke.gamestudio.dto.responses.AuthResponse;

public interface AuthService {

    UserDTO register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest loginRequest);
}
