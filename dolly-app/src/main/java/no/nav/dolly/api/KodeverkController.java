package no.nav.dolly.api;

import no.nav.dolly.domain.resultset.kodeverk.KodeverkAdjusted;
import no.nav.dolly.kodeverk.KodeverkConsumer;
import no.nav.dolly.kodeverk.KodeverkMapper;
import no.nav.tjenester.kodeverk.api.v1.GetKodeverkKoderBetydningerResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/kodeverk", produces = MediaType.APPLICATION_JSON_VALUE)
public class KodeverkController {

    @Autowired
    KodeverkMapper kodeverkMapper;

    @Autowired
    KodeverkConsumer kodeverkConsumer;

    @GetMapping("/{kodeverkNavn}")
    public KodeverkAdjusted fetchKodeverk(@PathVariable("kodeverkNavn") String kodeverkNavn) {
        GetKodeverkKoderBetydningerResponse response = kodeverkConsumer.fetchKodeverkByName(kodeverkNavn);
        return kodeverkMapper.mapBetydningToAdjustedKodeverk(kodeverkNavn, response.getBetydninger());
    }
}
