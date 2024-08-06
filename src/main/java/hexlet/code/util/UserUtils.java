package hexlet.code.util;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUtils {

    private final UserRepository userRepository;

//    @Value("${admin.admin-email}")
//    private String adminEmail;

//    public User getCurrentUser() {
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return null;
//        }
//        var email = authentication.getName();
//        return userRepository.findByEmail(email).orElseThrow();
//    }

//    public boolean isCurrentUserAdmin() {
//        var email = getCurrentUser().getEmail();
//        return email.equals("hexlet@example.com");
//    }

    public User getTestUser() {
        return  userRepository.findByEmail("hexlet@example.com")
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
