package no.nav.registre.testnav.inntektsmeldingservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.dovarkiv.v1.ProsessertInntektDokument;
import no.nav.registre.testnav.inntektsmeldingservice.controller.requests.InntektsmeldingRequest;
import no.nav.registre.testnav.inntektsmeldingservice.service.InntektsmeldingService;
import no.nav.registre.testnav.inntektsmeldingservice.controller.response.InntektsmeldingResponse;

@Slf4j
@RestController
@RequestMapping("/api/v1/inntektsmelding")
@RequiredArgsConstructor
public class InntektsmeldingController {

    private final InntektsmeldingService inntektsmeldingService;

    @PostMapping
    @ResponseBody
    public ResponseEntity<InntektsmeldingResponse> genererMeldingForIdent(
            @RequestHeader("Nav-Call-Id") String navCallId,
            @RequestBody InntektsmeldingRequest request
    ) {
        log.info("Oppretter inntektsmelding for {} i {}.", request.getArbeidstakerFnr(), request.getMiljoe());

        validerInntektsmelding(request);
        List<ProsessertInntektDokument> prosessertInntektDokuments = inntektsmeldingService.opprettInntektsmelding(navCallId, request);

        var response = new InntektsmeldingResponse(
                request.getArbeidstakerFnr(),
                prosessertInntektDokuments.stream().map(ProsessertInntektDokument::toResponse).collect(Collectors.toList())
        );

        return ResponseEntity.ok(response);
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
