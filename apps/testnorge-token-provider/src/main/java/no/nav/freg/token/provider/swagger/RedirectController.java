package no.nav.freg.token.provider.swagger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    @GetMapping("/")
    public String redirect() {
        return "redirect:/swagger-ui.html";
    }

}