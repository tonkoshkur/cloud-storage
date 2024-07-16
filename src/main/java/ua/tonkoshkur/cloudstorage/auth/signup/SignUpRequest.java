package ua.tonkoshkur.cloudstorage.auth.signup;

import jakarta.validation.constraints.Size;

@PasswordMatches
public record SignUpRequest(

        @Size(min = 3,
                max = 50,
                message = "Username must be between 3 and 50 characters")
        String username,

        @Size(min = 3,
                max = 50,
                message = "Password must between 3 and 50 characters")
        String password,

        String confirmPassword
) {
}
