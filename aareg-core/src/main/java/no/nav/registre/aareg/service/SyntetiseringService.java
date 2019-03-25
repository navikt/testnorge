package no.nav.registre.aareg.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import no.nav.registre.aareg.consumer.rs.AaregSyntetisererenConsumer;
import no.nav.registre.aareg.consumer.rs.AaregstubConsumer;
import no.nav.registre.aareg.consumer.rs.HodejegerenConsumer;
import no.nav.registre.aareg.consumer.rs.responses.ArbeidsforholdsResponse;
import no.nav.registre.aareg.provider.rs.requests.SyntetiserAaregRequest;

@Service
@Slf4j
public class SyntetiseringService {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private AaregSyntetisererenConsumer aaregSyntetisererenConsumer;

    @Autowired
    private AaregstubConsumer aaregstubConsumer;

    @Autowired
    private Random rand;

    public ResponseEntity opprettArbeidshistorikk(SyntetiserAaregRequest syntetiserAaregRequest, Boolean lagreIAareg) {
        List<String> levendeIdenter = hodejegerenConsumer.finnLevendeIdenter(syntetiserAaregRequest.getAvspillergruppeId());
        List<String> nyeIdenter = new ArrayList<>(syntetiserAaregRequest.getAntallNyeIdenter());
        List<String> utvalgteIdenter = new ArrayList<>(aaregstubConsumer.hentEksisterendeIdenter());
        levendeIdenter.removeAll(utvalgteIdenter);

        int antallNyeIdenter = syntetiserAaregRequest.getAntallNyeIdenter();
        if(antallNyeIdenter > levendeIdenter.size()) {
            antallNyeIdenter = levendeIdenter.size();
            log.info("Fant ikke nok ledige identer i avspillergruppe. Lager arbeidsforhold på {} identer.", antallNyeIdenter);
        }

        for (int i = 0; i < antallNyeIdenter; i++) {
            nyeIdenter.add(levendeIdenter.remove(rand.nextInt(levendeIdenter.size())));
        }

        utvalgteIdenter.addAll(nyeIdenter);
        List<ArbeidsforholdsResponse> syntetiserteArbeidsforhold = aaregSyntetisererenConsumer.getSyntetiserteArbeidsforholdsmeldinger(utvalgteIdenter);
        for(ArbeidsforholdsResponse arbeidsforholdsResponse : syntetiserteArbeidsforhold) {
            arbeidsforholdsResponse.setEnvironments(Arrays.asList(syntetiserAaregRequest.getMiljoe()));
        }

        int antallIdenter = utvalgteIdenter.size();
        utvalgteIdenter.removeAll(aaregstubConsumer.sendTilAaregstub(syntetiserteArbeidsforhold, lagreIAareg));

        if(utvalgteIdenter.isEmpty()) {
            return ResponseEntity.ok().body(antallIdenter + " identer fikk opprettet arbeidsforhold og ble sendt til aareg-stub");
        } else {
            log.error("Noe feilet under lagring til aaregstub. Følgende identer ble ikke lagret: {}", utvalgteIdenter.toString());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(String.format("Noe feilet under lagring til aaregstub. Følgende identer ble ikke lagret: {%s}", utvalgteIdenter.toString()));
        }
    }

    public List<ResponseEntity> sendArbeidsforholdTilAareg(List<ArbeidsforholdsResponse> syntetiserteArbeidsforhold) {
        return aaregstubConsumer.sendArbeidsforholdTilAareg(syntetiserteArbeidsforhold);
    }
}
