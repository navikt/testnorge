package no.nav.registre.testnorge.oversiktfrontend.provider;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForwardController {

    @RequestMapping(value = {"/app/**"})
    public String forwardToRoot() {
        return "forward:/";
    }
}
