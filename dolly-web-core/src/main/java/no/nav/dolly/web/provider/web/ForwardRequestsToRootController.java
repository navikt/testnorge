package no.nav.dolly.web.provider.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForwardRequestsToRootController {

    @RequestMapping(value = {"/profil/**", "/team/**", "/gruppe/**", "/maler/**", "/tpsendring/**"})
    public String forwardToRoot() {
        return "forward:/";
    }
}
