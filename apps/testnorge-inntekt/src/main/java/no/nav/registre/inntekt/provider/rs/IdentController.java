package no.nav.registre.inntekt.provider.rs;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.Inntektsinformasjon;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import no.nav.registre.inntekt.consumer.rs.InntektstubV2Consumer;
import no.nav.registre.inntekt.domain.inntektstub.RsInntekt;

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

    @PostMapping("/inntekter")
    public List<Inntektsinformasjon> opprettInntektPaaIdent(
            @RequestBody Map<String, List<RsInntekt>> inntekter
    ) {
        return inntektstubV2Consumer.leggInntekterIInntektstub(inntekter);
    }
}
