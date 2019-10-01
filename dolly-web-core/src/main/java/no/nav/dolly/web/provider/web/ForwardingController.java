package no.nav.dolly.web.provider.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ForwardingController {

    @RequestMapping(value = {"/profil/**", "/team/**", "/gruppe/**", "/maler/**", "/tpsendring/**"}, method = RequestMethod.GET)
    public String index() {
        return "forward:/";
    }
}
