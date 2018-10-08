package no.nav.identpool.ident.rest.v1;

import static no.nav.identpool.util.PersonidentifikatorValidatorUtil.valider;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.ident.exception.IdentAlleredeIBrukException;
import no.nav.identpool.ident.exception.UgyldigPersonidentifikatorException;
import no.nav.identpool.ident.repository.IdentEntity;
import no.nav.identpool.ident.service.IdentpoolService;

@Slf4j
@RestController
@RequestMapping("/api/v1/identifikator")
@RequiredArgsConstructor
public class IdentpoolController {

    private final IdentpoolService identpoolService;

    @PostMapping
    public List<String> rekvirer(@RequestBody HentIdenterRequest hentIdenterRequest) throws Exception{
        return identpoolService.finnIdenter(hentIdenterRequest);
    }

    @PostMapping("/bruk")
    public void markerBrukt(
            @RequestParam String personidentifikator,
            @RequestParam String bruker
    ) throws IdentAlleredeIBrukException, UgyldigPersonidentifikatorException {
        valider(personidentifikator);
        MarkerBruktRequest markerBruktRequest = MarkerBruktRequest.builder()
                .personidentifikator(personidentifikator)
                .bruker(bruker)
                .build();
        identpoolService.markerBrukt(markerBruktRequest);
    }

    @GetMapping("/ledig")
    public Boolean erLedig(
            @RequestParam String personidentifikator
    ) throws UgyldigPersonidentifikatorException {
        valider(personidentifikator);
        return identpoolService.erLedig(personidentifikator);
    }

    @GetMapping
    public IdentEntity lesInnhold(@RequestParam(value = "personidentifikator") String personidentifikator
    ) throws UgyldigPersonidentifikatorException {
        valider(personidentifikator);
        return identpoolService.lesInnhold(personidentifikator);
    }
}
