package sk.tuke.gamestudio.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import sk.tuke.gamestudio.entity.User;
import sk.tuke.gamestudio.entity.enums.Role;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user1;

    @BeforeEach
    void setUp() {
        entityManager.clear();
        user1 = new User("testuser1", "test1@example.com", "hashedpassword1", Role.USER);
        entityManager.persist(user1);
        entityManager.flush(); // sync with h2
    }

    @Test
    void whenSaveNewUser_thenSuccess() {
        //given
        User newUser = new User("newUser", "new@example.com", "hashedpassword", Role.USER);
        //when
        User savedUser = userRepository.save(newUser);
        //then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(newUser.getUsername());
        assertThat(savedUser.getEmail()).isEqualTo(newUser.getEmail());
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
        assertThat(savedUser.getCreatedAt()).isNotNull();

        User foundUser = entityManager.find(User.class, savedUser.getId());
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo(newUser.getUsername());
    }

    @Test
    void whenFindByUsername_thenReturnUser(){
        //given

        //when
        Optional<User> foundUser = userRepository.findByUsername(user1.getUsername());

        //then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo(user1.getUsername());
        assertThat(foundUser.get().getEmail()).isEqualTo(user1.getEmail());
    }
    @Test
    void whenFindByUsername_withNonExistingUsername_thenReturnEmpty() {
        // When
        Optional<User> found = userRepository.findByUsername("nonexisting");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void whenFindByEmail_thenReturnUser() {
        // Given

        // When
        Optional<User> found = userRepository.findByEmail("test1@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test1@example.com");
    }

    @Test
    void whenFindByEmail_withNonExistingEmail_thenReturnEmpty() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexisting@mail.com");

        // Then
        assertThat(found).isEmpty();
    }


    @Test
    void whenExistsByUsername_thenReturnTrue() {
        // Given

        // When
        boolean exists = userRepository.existsByUsername("testuser1");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void whenExistsByEmail_thenReturnTrue() {
        // Given

        // When
        boolean exists = userRepository.existsByEmail("test1@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void whenExistsByUsername_withNonExisting_thenReturnFalse() {
        // When
        boolean exists = userRepository.existsByUsername("nonexisting");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void whenExistsByEmail_withNonExisting_thenReturnFalse() {
        // When
        boolean exists = userRepository.existsByEmail("nonexisting@mail.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void whenSaveUserWithDuplicateUsername_thenThrowException() {
        // Given
        // When/Then
        User duplicateUser = new User("testuser1", "another@email.com", "pass", Role.USER);
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(duplicateUser);
        });
    }

    @Test
    void whenSaveUserWithDuplicateEmail_thenThrowException() {
        // Given

        // When/Then
        User duplicateEmailUser = new User("anotherUsername", "test1@example.com", "pass", Role.USER);
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(duplicateEmailUser);
        });
    }
}
