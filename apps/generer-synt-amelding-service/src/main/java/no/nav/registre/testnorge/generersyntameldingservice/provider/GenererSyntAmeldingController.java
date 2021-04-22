package no.nav.registre.testnorge.generersyntameldingservice.provider;

import java.util.List;

import lombok.RequiredArgsConstructor;

import no.nav.registre.testnorge.generersyntameldingservice.provider.request.SyntAmeldingRequest;
import no.nav.registre.testnorge.generersyntameldingservice.provider.response.ArbeidsforholdDTO;
import no.nav.registre.testnorge.generersyntameldingservice.service.GenererSyntAmeldingService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static no.nav.registre.testnorge.generersyntameldingservice.utils.InputValidator.validateInput;

@RestController
@RequestMapping("/api/v1/amelding")
@RequiredArgsConstructor
public class GenererSyntAmeldingController {

    private final GenererSyntAmeldingService genererSyntAmeldingService;

    @PostMapping
    public List<ArbeidsforholdDTO> generateSyntheticAmeldinger(
            @RequestBody SyntAmeldingRequest request
    ) {
        validateInput(request.getArbeidsforholdType());
        return genererSyntAmeldingService.generateAmeldinger(request);
    }

}
