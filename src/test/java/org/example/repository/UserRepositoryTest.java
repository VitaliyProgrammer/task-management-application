package repository;

import java.util.Optional;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Saves and finds email of user")
    void saveAndFindUserByEmail() {

        User user = new User();

        user.setEmail("test@gmail.com");
        user.setPassword("encoded-password");
        user.setFirstName("Bob");
        user.setLastName("Johnson");

        userRepository.save(user);

        Optional<User> result = userRepository.findByEmail("test@gmail.com");

        assertTrue(result.isPresent());
        assertEquals("test@gmail.com", result.get().getEmail());
    }

    @Test
    @DisplayName("Returns existing email of user")
    void returnTrueIfEmailExists() {

        User user = new User();

        user.setEmail("test@gmail.com");
        user.setPassword("encoded-password");
        user.setFirstName("Bob");
        user.setLastName("Johnson");

        userRepository.save(user);

        boolean existsUserMail = userRepository.existsByEmail("test@gmail.com");

        assertTrue(existsUserMail);
    }
}
