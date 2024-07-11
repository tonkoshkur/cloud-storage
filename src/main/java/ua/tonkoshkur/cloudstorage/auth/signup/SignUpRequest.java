package ua.tonkoshkur.cloudstorage.auth.signup;

import jakarta.validation.constraints.Size;

@PasswordMatches
public record SignUpRequest(

        @Size(min = 3)
        String username,

        @Size(min = 3)
        String password,

        String confirmPassword
) {
}
