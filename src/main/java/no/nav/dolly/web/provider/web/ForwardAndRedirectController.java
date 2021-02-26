package no.nav.dolly.web.provider.web;

import static no.nav.dolly.web.provider.web.ProxyController.API_URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForwardAndRedirectController {


    @Value("${DOLLY_BACKEND_HOST}")
    private String dollyUrl;

    @RequestMapping(value = {"/minside/**", "/team/**", "/gruppe/**", "/maler/**", "/soek/**"})
    public String forwardToRoot() {
        return "forward:/";
    }

    @GetMapping("/swagger-ui.html")
    public String redirect() {
        return "redirect:" + dollyUrl.split(API_URI)[0] + "/swagger-ui.html";
    }
}
