package ua.tonkoshkur.cloudstorage.auth.signin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("auth/signin")
@Controller
public class SignInController {
    @GetMapping
    public String signInPage(@ModelAttribute("request") SignInRequest request) {
        return "signin";
    }
}
