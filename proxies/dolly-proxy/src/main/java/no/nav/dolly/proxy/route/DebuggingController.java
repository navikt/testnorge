package no.nav.dolly.proxy.route;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/debug")
class DebuggingController {

    @GetMapping("test")
    Mono<String> test() {
        return Mono.just("Debugging endpoint is working!");
    }

}
