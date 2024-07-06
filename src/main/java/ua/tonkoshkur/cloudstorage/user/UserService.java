package ua.tonkoshkur.cloudstorage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void save(String username, String password) throws UserAlreadyExistsException {
        try {
            userRepository.save(
                    new User(username, passwordEncoder.encode(password)));
        } catch (DataIntegrityViolationException s) {
            throw new UserAlreadyExistsException();
        }
    }
}
