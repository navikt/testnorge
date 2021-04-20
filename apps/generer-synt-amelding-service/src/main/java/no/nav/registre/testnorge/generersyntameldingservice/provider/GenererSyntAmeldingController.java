package no.nav.registre.testnorge.generersyntameldingservice.provider;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/amelding")
@RequiredArgsConstructor
public class GenererSyntAmeldingController {

    @GetMapping
    public String generateAmeldinger() {
        return "Not implemented yet";
    }
}
