package no.nav.registre.testnorge.endringsmeldingfrontend.config;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForwardAndRedirectController {

    @RequestMapping({"/page/*"})
    public String forwardToRoot() {
        return "forward:/";
    }

}
