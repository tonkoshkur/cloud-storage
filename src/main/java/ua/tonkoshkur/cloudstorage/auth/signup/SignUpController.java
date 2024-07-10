package ua.tonkoshkur.cloudstorage.auth.signup;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ua.tonkoshkur.cloudstorage.user.UserAlreadyExistsException;
import ua.tonkoshkur.cloudstorage.user.UserService;

@RequestMapping("auth/signup")
@Controller
@RequiredArgsConstructor
public class SignUpController {

    private static final String SIGN_UP_PAGE = "signup";
    private final UserService userService;

    @GetMapping
    public String signUpPage(@ModelAttribute("request") SignUpRequest request) {
        return SIGN_UP_PAGE;
    }

    @PostMapping
    public ModelAndView signUp(@ModelAttribute("request") @Valid SignUpRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return new ModelAndView(SIGN_UP_PAGE);
        }

        try {
            userService.save(request.username(), request.password());
        } catch (UserAlreadyExistsException ex) {
            return new ModelAndView(SIGN_UP_PAGE, "error", ex.getMessage());
        }

        return new ModelAndView("redirect:/auth/signin");
    }
}
