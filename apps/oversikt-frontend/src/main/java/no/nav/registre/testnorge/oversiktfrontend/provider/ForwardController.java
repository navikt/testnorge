package no.nav.registre.testnorge.oversiktfrontend.provider;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ForwardController {
    @RequestMapping(value = {"/app/**"})
    public String forwardToRoot() {
        return "forward:/";
    }
}
