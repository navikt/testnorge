package no.nav.identpool.ident.rest.v1;

import static no.nav.identpool.util.PersonidentifikatorValidatorUtil.valider;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.ident.ajourhold.util.PersonIdentifikatorUtil;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Kjoenn;
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
    public List<String> rekvirer(
            @RequestParam(value = "antall") Integer antall,
            @RequestParam(value = "identtype", defaultValue = "UBESTEMT") String identtypeString,
            @RequestParam(value = "foedtfoer", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate foedtFoer,
            @RequestParam(value = "foedtetter", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate foedtEtter,
            @RequestParam(value = "kjoenn", defaultValue = "UBESTEMT") String kjoenn
    ) {
        HentIdenterRequest hentIdenterRequest = HentIdenterRequest.builder()
                .pageable(PageRequest.of(0, antall))
                .identtype(Identtype.enumFromString(identtypeString))
                .foedtFoer(foedtFoer)
                .foedtEtter(foedtEtter)
                .kjoenn(Kjoenn.enumFromString(kjoenn))
                .build();
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
    public IdentEntity lesInnhold(
            @RequestParam(value = "personidentifikator") String personidentifikator
    ) throws UgyldigPersonidentifikatorException {
        valider(personidentifikator);
        return identpoolService.lesInnhold(personidentifikator);
    }
}
