package no.nav.registre.inntektsmeldingstub.provider;

import lombok.RequiredArgsConstructor;
import no.seres.xsd.nav.inntektsmelding_m._20180924.XMLInntektsmeldingM;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.inntektsmeldingstub.MeldingsType;
import no.nav.registre.inntektsmeldingstub.database.model.Inntektsmelding;
import no.nav.registre.inntektsmeldingstub.service.InntektsmeldingService;
import no.nav.registre.inntektsmeldingstub.util.XmlInntektsmelding201809;
import no.nav.registre.inntektsmeldingstub.util.XmlInntektsmelding201812;

@RestController
@RequestMapping("/api/v1/inntektsmelding")
@RequiredArgsConstructor
//TODO: Fiks xml representasjon av output, nå klarer den ikke konvertere
public class InntektsmeldingProvider {

    private final InntektsmeldingService service;

    @GetMapping(value = "/2018/09/xml/{id}", produces = "application/xml")
    public XMLInntektsmeldingM hentInntektsmelding201809(@PathVariable Integer id) {
        return XmlInntektsmelding201809.createInntektsmelding(service.findInntektsmelding(id));
    }

    @GetMapping("/2018/09/json/{id}")
    public ResponseEntity<Inntektsmelding> hentInntektsmeldingJSON201809(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findInntektsmelding(id));
    }

    @PostMapping("/2018/09")
    public ResponseEntity<List<Inntektsmelding>> opprettInntektsmeldinger201809(@RequestBody List<Inntektsmelding> meldinger) {
        return ResponseEntity.ok(
                service.saveMeldinger(meldinger, MeldingsType.TYPE_2018_09)
        );
    }

    @GetMapping(value = "/2018/12/xml/{id}", produces = "application/xml")
    public no.seres.xsd.nav.inntektsmelding_m._20181211.XMLInntektsmeldingM hentInntektsmelding201812(@PathVariable Integer id) {
        return XmlInntektsmelding201812.createInntektsmelding(service.findInntektsmelding(id));
    }

    @GetMapping("/2018/12/json/{id}")
    public ResponseEntity<Inntektsmelding> hentInntektsmeldingJSON201812(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findInntektsmelding(id));
    }

    @PostMapping("/2018/12")
    public ResponseEntity<List<Inntektsmelding>> opprettInntektsmeldinger201812(@RequestBody List<Inntektsmelding> meldinger) {
        return ResponseEntity.ok(
                service.saveMeldinger(meldinger, MeldingsType.TYPE_2018_12)
        );
    }

}
