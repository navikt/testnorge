package no.nav.registre.testnorge.arbeidsforhold.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.arbeidsforhold.consumer.AaregSyntConsumer;
import no.nav.registre.testnorge.arbeidsforhold.domain.Opplysningspliktig;
import no.nav.registre.testnorge.arbeidsforhold.service.OpplysningspliktigSerivce;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.OpplysningspliktigDTO;

@Slf4j
@RestController
@RequestMapping("/api/v1/opplysningspliktig")
@RequiredArgsConstructor
public class OpplysningspliktigController {
    private final OpplysningspliktigSerivce serivce;
    private final AaregSyntConsumer aaregSyntConsumer;

    @PutMapping
    public ResponseEntity<HttpStatus> createOpplysningspliktig(
            @RequestBody OpplysningspliktigDTO opplysningspliktigDTO,
            @RequestHeader("miljo") String miljo
    ) {
        Opplysningspliktig opplysningspliktig = new Opplysningspliktig(opplysningspliktigDTO);
        aaregSyntConsumer.saveOpplysningspliktig(opplysningspliktig);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{orgnummer}")
    public ResponseEntity<OpplysningspliktigDTO> getOpplysningspliktig(
            @PathVariable("orgnummer") String orgnummer,
            @RequestHeader("miljo") String miljo
    ) {
        Opplysningspliktig opplysningspliktig = serivce.getOpplysningspliktig(orgnummer, miljo);
        if (opplysningspliktig == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(opplysningspliktig.toDTO());
    }
}