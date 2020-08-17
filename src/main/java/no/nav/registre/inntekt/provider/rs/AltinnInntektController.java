package no.nav.registre.inntekt.provider.rs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.inntekt.provider.rs.requests.AltinnInntektsmeldingRequest;
import no.nav.registre.inntekt.provider.rs.requests.GenererAltinnInntektRequest;
import no.nav.registre.inntekt.provider.rs.response.AltinnInntektResponse;
import no.nav.registre.inntekt.service.AltinnInntektService;
import no.nav.registre.inntekt.utils.ValidationException;

@Slf4j
@RestController
@RequestMapping("/api/v1/altinnInntekt")
@RequiredArgsConstructor
public class AltinnInntektController {

    private final AltinnInntektService altinnInntektService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/generer")
    public void generer(
            @RequestParam(required = false, defaultValue = "true") Boolean opprettPaaEksisterende,
            @RequestBody GenererAltinnInntektRequest request
    ) throws ValidationException {
        throw new ValidationException("NOT IMPLEMENTED");
    }

    @PostMapping(value = "/enkeltident")
    @ResponseBody
    public ResponseEntity<?> genererMeldingForIdent(
            @RequestHeader("Nav-Call-Id") String navCallId,
            @RequestHeader("Nav-Consumer-Id") String navConsuemrId,
            @RequestBody AltinnInntektsmeldingRequest dollyRequest,
            @RequestParam(value = "validerArbeidsfohrold", required = false, defaultValue = "false") Boolean validerArbeidsforhold,
            @RequestParam(value = "includeXml", required = false, defaultValue = "false") Boolean includeXml,
            @RequestParam(value = "continueOnError", required = false, defaultValue = "false") Boolean continueOnError
    ) {
        try {
            validerInntektsmelding(dollyRequest);
            var altinnInntektResponse = altinnInntektService.utfoerAltinnInntektMeldingRequest(navCallId, dollyRequest, continueOnError, validerArbeidsforhold, includeXml);
            return ResponseEntity.ok(altinnInntektResponse);
        } catch (ValidationException e) {
            log.error("Feil ved laging av Altinn meldinger", e);
            return new ResponseEntity<>(e.getErrors(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Feil ved opprettelse av enkeltindent", e);
            return new ResponseEntity<>(e.getStackTrace(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validerInntektsmelding(AltinnInntektsmeldingRequest dollyRequest) throws ValidationException {
        for (var inntekt : dollyRequest.getInntekter()) {
            var arbeidsgiver = inntekt.getArbeidsgiver();
            var arbeidsgiverPrivat = inntekt.getArbeidsgiverPrivat();

            if (arbeidsgiver != null && arbeidsgiverPrivat != null) {
                throw new ValidationException("Arbeidsgiver og privatarbeidsgiver kan ikke begge være utfylt");
            }
            if (arbeidsgiver == null && arbeidsgiverPrivat == null) {
                throw new ValidationException("En av arbeidsgiver eller privat arbeidsgiver må være utfylt");
            }
        }
    }
}
