package no.nav.registre.testnorge.arbeidsforholdservice.provider;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.arbeidsforholdservice.consumer.AaregConsumer;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@Slf4j
@RequestMapping("/api/v1/arbeidsforhold")
@RequiredArgsConstructor
public class ArbeidsforholdController {
    private final AaregConsumer aaregConsumer;

    @GetMapping("/{ident}/{orgnummer}/{arbeidsforholdId}")
    public ResponseEntity<ArbeidsforholdDTO> getArbeidsforhold(
            @PathVariable("ident") String ident,
            @PathVariable("orgnummer") String orgnummer,
            @PathVariable("arbeidsforholdId") String arbeidsforholdId,
            @RequestHeader("miljo") String miljo
    ) {
        var arbeidsforhold = aaregConsumer.getArbeidsforhold(ident, orgnummer, arbeidsforholdId, miljo);
        return ResponseEntity.ok(arbeidsforhold.toDTO());
    }

    @ControllerAdvice
    public static class ExceptionHandlerAdvice {

        @ExceptionHandler(HttpClientErrorException.class)
        public ResponseEntity<String> handleException(HttpClientErrorException e) {
            log.error("Klarte ikke å finne arbeidsforhold", e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Klarte ikke å finne aktivt arbeidsforhold for personen");
        }
    }
}
