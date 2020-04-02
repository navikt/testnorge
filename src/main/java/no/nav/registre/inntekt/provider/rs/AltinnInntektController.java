package no.nav.registre.inntekt.provider.rs;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import no.nav.registre.inntekt.provider.rs.requests.AltinnDollyFullMeldingRequest;
import no.nav.registre.inntekt.provider.rs.requests.AltinnDollyRequest;
import no.nav.registre.inntekt.provider.rs.requests.AltinnRequest;
import no.nav.registre.inntekt.service.AltinnInntektService;
import no.nav.registre.inntekt.utils.ValidationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestBody AltinnRequest request
            ) throws ValidationException {
        throw new ValidationException("NOT IMPLEMENTED");
    }

    @PostMapping(value = "/enkeltident")
    public ResponseEntity<?> genererMeldingForIdent(
            @RequestBody AltinnDollyRequest dollyRequest
            ) {
        try {
            return new ResponseEntity<>(altinnInntektService.lagAltinnMeldinger(dollyRequest), HttpStatus.CREATED);
        } catch (ValidationException e) {
            return new ResponseEntity<>(e.getErrors(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getStackTrace(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/enkeltident/full-melding")
    public ResponseEntity<?> genererFullMelding(
            @RequestBody AltinnDollyFullMeldingRequest request
    ) {
        try {
            return new ResponseEntity<>(altinnInntektService.lagFullAltinnMelding(request), HttpStatus.CREATED);
        } catch (ValidationException e) {
            return new ResponseEntity<>(e.getErrors(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getStackTrace(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
