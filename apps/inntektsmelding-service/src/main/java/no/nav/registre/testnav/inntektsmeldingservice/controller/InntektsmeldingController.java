package no.nav.registre.testnav.inntektsmeldingservice.controller;

import io.swagger.v3.core.util.Json;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnav.inntektsmeldingservice.service.InntektsmeldingService;
import no.nav.testnav.libs.dto.dokarkiv.v1.ProsessertInntektDokument;
import no.nav.testnav.libs.dto.inntektsmeldingservice.v1.requests.InntektsmeldingRequest;
import no.nav.testnav.libs.dto.inntektsmeldingservice.v1.response.InntektsmeldingResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping(
        value = "/api/v1/inntektsmelding",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Slf4j
public class InntektsmeldingController {

    private final InntektsmeldingService inntektsmeldingService;

    public InntektsmeldingController(InntektsmeldingService inntektsmeldingService) {
        this.inntektsmeldingService = inntektsmeldingService;
    }

    @PostMapping
    public InntektsmeldingResponse genererMeldingForIdent(
            @RequestHeader("Nav-Call-Id") String navCallId,
            @RequestBody InntektsmeldingRequest request) {

        log.info("Oppretter inntektsmelding for {} i {} melding {}.", request.getArbeidstakerFnr(), request.getMiljoe(), Json.pretty(request));

        validerInntektsmelding(request);

        try {
            var prosessertInntektDokuments = inntektsmeldingService.opprettInntektsmelding(navCallId, request);
            return new InntektsmeldingResponse(
                    request.getArbeidstakerFnr(),
                    prosessertInntektDokuments
                            .stream()
                            .map(ProsessertInntektDokument::toResponse)
                            .toList()
            );
        } catch (WebClientResponseException.BadRequest ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getResponseBodyAsString(StandardCharsets.UTF_8), ex);
        }
    }

    private void validerInntektsmelding(InntektsmeldingRequest dollyRequest) {

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
