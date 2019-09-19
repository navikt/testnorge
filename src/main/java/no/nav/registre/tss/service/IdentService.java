package no.nav.registre.tss.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.tss.consumer.rs.TssSyntetisererenConsumer;
import no.nav.registre.tss.consumer.rs.responses.TssSyntMessage;
import no.nav.registre.tss.domain.Person;

@Service
public class IdentService {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private TssSyntetisererenConsumer tssSyntetisererenConsumer;

    public void opprettLegerITss(String miljoe, List<String> identer) {
        List<Person> identerMedNavn = hentPersondataFraHodejegeren(miljoe, identer);

        Map<String, List<TssSyntMessage>> identerMedRutiner = tssSyntetisererenConsumer.hentSyntetiskeTssRutiner(identerMedNavn);
    }

    private List<Person> hentPersondataFraHodejegeren(String miljoe, List<String> identer) {
        List<Person> identerMedNavn = new ArrayList<>();

        for (String ident : identer) {
            identerMedNavn.add(new Person(ident, hodejegerenConsumer.getPersondata(ident, miljoe).getKortnavn()));
        }

        return identerMedNavn;
    }
}
