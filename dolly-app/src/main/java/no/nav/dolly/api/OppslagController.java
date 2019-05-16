package no.nav.dolly.api;

import static no.nav.dolly.config.CachingConfig.CACHE_KODEVERK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.dolly.domain.resultset.kodeverk.KodeverkAdjusted;
import no.nav.dolly.kodeverk.KodeverkConsumer;
import no.nav.dolly.kodeverk.KodeverkMapper;
import no.nav.dolly.norg2.Norg2Consumer;
import no.nav.dolly.norg2.Norg2EnhetResponse;
import no.nav.dolly.personoppslag.PersonoppslagConsumer;
import no.nav.tjenester.kodeverk.api.v1.GetKodeverkKoderBetydningerResponse;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class OppslagController {

    @Autowired
    private KodeverkMapper kodeverkMapper;

    @Autowired
    private KodeverkConsumer kodeverkConsumer;

    @Autowired
    private Norg2Consumer norg2Consumer;

    @Autowired
    private PersonoppslagConsumer personoppslagConsumer;

    @Cacheable(CACHE_KODEVERK)
    @GetMapping("/kodeverk/{kodeverkNavn}")
    public KodeverkAdjusted fetchKodeverkByName(@PathVariable("kodeverkNavn") String kodeverkNavn) {
        GetKodeverkKoderBetydningerResponse response = kodeverkConsumer.fetchKodeverkByName(kodeverkNavn);
        return kodeverkMapper.mapBetydningToAdjustedKodeverk(kodeverkNavn, response.getBetydninger());
    }

    @Cacheable(CACHE_KODEVERK)
    @GetMapping("/norg2/enhet/{tknr}")
    public Norg2EnhetResponse fetchEnhetByTknr(@PathVariable("tknr") String tknr) {
        return norg2Consumer.fetchEnhetByEnhetNr(tknr);
    }

    @GetMapping("/personoppslag/ident/{ident}")
    public ResponseEntity personoppslag(@PathVariable("ident") String ident) {
        return personoppslagConsumer.fetchPerson(ident);
    }
}
