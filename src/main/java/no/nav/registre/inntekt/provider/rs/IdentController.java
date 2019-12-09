package no.nav.registre.inntekt.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.inntekt.consumer.rs.v2.InntektstubV2Consumer;
import no.nav.tjenester.stub.aordningen.inntektsinformasjon.v2.inntekter.Inntektsinformasjon;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class IdentController {

    private final InntektstubV2Consumer inntektstubV2Consumer;

    @GetMapping("/identer")
    public List<String> hentIdenterIInntektstub() {
        return inntektstubV2Consumer.hentEksisterendeIdenter();
    }

    @GetMapping("/inntekter")
    public List<Inntektsinformasjon> hentInntekterTilIdenter(
            @RequestParam List<String> identer
    ) {
        return inntektstubV2Consumer.hentEksisterendeInntekterPaaIdenter(identer);
    }
}
