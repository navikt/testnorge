package no.nav.dolly.provider.api;

import static java.util.Arrays.asList;
import static no.nav.dolly.config.CachingConfig.CACHE_KODEVERK;
import static no.nav.dolly.config.CachingConfig.CACHE_NORG2;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.consumer.kodeverk.KodeverkMapper;
import no.nav.dolly.consumer.norg2.Norg2Consumer;
import no.nav.dolly.consumer.norg2.Norg2EnhetResponse;
import no.nav.dolly.consumer.personoppslag.PersonoppslagConsumer;
import no.nav.dolly.consumer.syntdata.SyntdataConsumer;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.domain.resultset.kodeverk.KodeverkAdjusted;
import no.nav.tjenester.kodeverk.api.v1.GetKodeverkKoderBetydningerResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class OppslagController {

    private final KodeverkMapper kodeverkMapper;
    private final KodeverkConsumer kodeverkConsumer;
    private final Norg2Consumer norg2Consumer;
    private final PersonoppslagConsumer personoppslagConsumer;
    private final SyntdataConsumer syntdataConsumer;

    @Cacheable(CACHE_KODEVERK)
    @GetMapping("/kodeverk/{kodeverkNavn}")
    @ApiOperation("Hent kodeverk etter kodeverkNavn")
    public KodeverkAdjusted fetchKodeverkByName(@PathVariable("kodeverkNavn") String kodeverkNavn) {
        GetKodeverkKoderBetydningerResponse response = kodeverkConsumer.fetchKodeverkByName(kodeverkNavn);
        return kodeverkMapper.mapBetydningToAdjustedKodeverk(kodeverkNavn, response.getBetydninger());
    }

    @Cacheable(CACHE_NORG2)
    @GetMapping("/norg2/enhet/{tknr}")
    @ApiOperation("Hent enhet tilhørende Tknr fra NORG")
    public Norg2EnhetResponse fetchEnhetByTknr(@PathVariable("tknr") String tknr) {
        return norg2Consumer.fetchEnhetByEnhetNr(tknr);
    }

    @GetMapping("/personoppslag/ident/{ident}")
    @ApiOperation("Hent person tilhørende ident fra personoppslag")
    public ResponseEntity personoppslag(@PathVariable("ident") String ident) {
        return personoppslagConsumer.fetchPerson(ident);
    }

    @GetMapping("/syntdata")
    @ApiOperation("Hent syntetisk data")
    public ResponseEntity syntdataGenerate(@RequestParam("path") String path, @RequestParam("numToGenerate") Integer numToGenerate) {
        return syntdataConsumer.generate(path, numToGenerate);
    }

    @GetMapping("/systemer")
    @ApiOperation("Hent liste med systemer og deres beskrivelser")
    public List<SystemTyper.SystemBeskrivelse> getSystemTyper() {
        return asList(SystemTyper.values()).stream()
                .map(type -> SystemTyper.SystemBeskrivelse.builder().system(type.name()).beskrivelse(type.getBeskrivelse()).build())
                .collect(Collectors.toList());
    }
}
