package ua.tonkoshkur.cloudstorage.auth.signup;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        SignUpRequest request = (SignUpRequest) obj;
        return request.password().equals(request.confirmPassword());
    }
}
