package ua.tonkoshkur.cloudstorage.auth.signup;

import jakarta.validation.constraints.Size;

@PasswordMatches
public record SignUpRequest(

        @Size(min = 3, message = "Username must be 3+ characters")
        String username,

        @Size(min = 3, message = "Password must be 3+ characters")
        String password,

        String confirmPassword
) {
}
