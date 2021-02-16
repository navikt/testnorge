package no.nav.registre.testnorge.endringsmeldingfrontend.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/open/")
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", defaultValue = "false") boolean loginError) {
        log.info("Error={}",loginError);
        return "forward:/";
    }

    @GetMapping
    public String toRoot() {
        return "/";
    }
}

