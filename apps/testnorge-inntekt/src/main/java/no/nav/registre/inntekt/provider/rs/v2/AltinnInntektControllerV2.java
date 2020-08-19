package no.nav.registre.inntekt.provider.rs.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.ProsessertInntektDokument;
import no.nav.registre.inntekt.provider.rs.requests.AltinnInntektsmeldingRequest;
import no.nav.registre.inntekt.provider.rs.v2.response.AltinnInntektResponseV2;
import no.nav.registre.inntekt.service.AltinnInntektService;
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

@Slf4j
@RestController
@RequestMapping("/api/v2/altinn-inntektsmelding")
@RequiredArgsConstructor
public class AltinnInntektControllerV2 {

    private final AltinnInntektService altinnInntektService;

    @PostMapping
    @ResponseBody
    public ResponseEntity<AltinnInntektResponseV2> opprett(
            @RequestHeader("Nav-Call-Id") String navCallId,
            @RequestBody AltinnInntektsmeldingRequest request
    ) {
        validerInntektsmelding(request);
        List<ProsessertInntektDokument> prosessertInntektDokuments = altinnInntektService.utfoerAltinnInntektMeldingRequest(navCallId, request);

        var response = new AltinnInntektResponseV2(
                request.getArbeidstakerFnr(),
                prosessertInntektDokuments.stream().map(ProsessertInntektDokument::toResponseV2).collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }

    private void validerInntektsmelding(AltinnInntektsmeldingRequest dollyRequest) {
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
