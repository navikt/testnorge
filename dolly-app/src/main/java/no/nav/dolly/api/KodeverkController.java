package no.nav.dolly.api;

import no.nav.dolly.domain.kodeverk.Kode;
import no.nav.dolly.domain.kodeverk.Kodeverk;

import java.util.Arrays;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/kodeverk", produces = MediaType.APPLICATION_JSON_VALUE)
public class KodeverkController {

    @GetMapping("/{kodeverkNavn}")
    public Kodeverk fetchKodeverk(){
        List<Kode> koder = Arrays.asList(new Kode("NOR", "NORGE"), new Kode("NLD", "NEDERLAND"), new Kode("OMN", "OMAN"), new Kode("SWE", "SVERIGE"));
        Kodeverk kodeverk = Kodeverk.builder().koder(koder).navn("StartborgerskapFreg").versjon(1).build();
        return kodeverk;
    }
}
