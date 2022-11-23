package no.nav.registre.testnorge.generersyntameldingservice.provider;

import java.util.List;

import lombok.RequiredArgsConstructor;

import no.nav.registre.testnorge.generersyntameldingservice.provider.request.SyntAmeldingRequest;
import no.nav.registre.testnorge.generersyntameldingservice.provider.response.ArbeidsforholdDTO;
import no.nav.registre.testnorge.generersyntameldingservice.service.GenererService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/generer/amelding")
@RequiredArgsConstructor
public class SyntController {

    private final GenererService genererService;

    @PostMapping
    public List<ArbeidsforholdDTO> generateSyntheticAmeldinger(
            @RequestBody SyntAmeldingRequest request
    ) {
        return genererService.generateAmeldinger(request);
    }

}
