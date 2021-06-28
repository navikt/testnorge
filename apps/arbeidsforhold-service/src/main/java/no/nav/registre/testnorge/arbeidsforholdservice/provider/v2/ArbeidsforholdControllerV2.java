package no.nav.registre.testnorge.arbeidsforholdservice.provider.v2;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.ArbeidsforholdDTO;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.v2.AaregConsumerV2;
import no.nav.registre.testnorge.arbeidsforholdservice.domain.v2.Arbeidsforhold;
import no.nav.registre.testnorge.arbeidsforholdservice.service.ArbeidsforholdService;
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

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class ArbeidsforholdControllerV2 {
    private final AaregConsumerV2 aaregConsumer;
    private final ArbeidsforholdService arbeidsforholdService;

    @GetMapping("/arbeidstaker/{arbeidstaker}/arbeidsforhold")
    public ResponseEntity<List<Arbeidsforhold>> getArbeidsforhold(
            @PathVariable("arbeidstaker") String ident,
            @RequestHeader("miljo") String miljo
    ) {
        List<ArbeidsforholdDTO> arbeidsforhold = aaregConsumer.getArbeidsforholds(ident, miljo);
        return ResponseEntity.ok(arbeidsforholdService.getArbeidsforhold(arbeidsforhold));
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
