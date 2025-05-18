package sk.tuke.gamestudio.service.interfaces;

import sk.tuke.gamestudio.entity.User;

import java.util.Optional;

public interface UserService {

    User saveUser(User user);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
