package no.nav.dolly.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class EnvironmentVarController {

    @Value("${tpsf.server.url}")
    private String tpsfHost;

    @GetMapping("/config")
    public String fetchTpsfUrl(){
        return tpsfHost;
    }
}
