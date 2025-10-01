package no.nav.dolly.proxy.route;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug")
class DebuggingController {

    @GetMapping("test")
    String test() {
        return "Debugging endpoint is working!";
    }

}
