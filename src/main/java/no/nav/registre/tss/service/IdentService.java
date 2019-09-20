package no.nav.registre.tss.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.List;

import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.tss.consumer.rs.responses.Response910;
import no.nav.registre.tss.domain.Person;

@Service
@Slf4j
public class IdentService {

    @Value("${queue.queueNameAjourhold.Q1}")
    private String ajourholdQ1;

    @Value("${queue.queueNameAjourhold.Q2}")
    private String ajourholdQ2;

    @Value("${queue.queueNameSamhandlerService.Q1}")
    private String samhandlerQ1;

    @Value("${queue.queueNameSamhandlerService.Q2}")
    private String samhandlerQ2;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private TssService tssService;

    public List<String> opprettLegerITss(String miljoe, List<String> identer) {
        String koeNavnAjourhold = "";
        String koeNavnSamhandler = "";

        if ("q1".equals(miljoe)) {
            koeNavnAjourhold = ajourholdQ1;
            koeNavnSamhandler = samhandlerQ1;
        } else if ("q2".equals(miljoe)) {
            koeNavnAjourhold = ajourholdQ2;
            koeNavnSamhandler = samhandlerQ2;
        }

        List<Person> identerMedNavn = hentPersondataFraHodejegeren(miljoe, identer);

        List<String> syntetiskeTssRutiner = tssService.opprettSyntetiskeTssRutiner(identerMedNavn);

        tssService.sendTilTss(syntetiskeTssRutiner, koeNavnAjourhold);

        List<String> opprettedeLeger = new ArrayList<>();

        for (Person ident : identerMedNavn) {
            try {
                Response910 response = tssService.sendOgMotta910RutineFraTss(ident.getFnr(), koeNavnSamhandler);
                if (!response.getResponse110().isEmpty() || !response.getResponse111().isEmpty() || !response.getResponse125().isEmpty()) {
                    opprettedeLeger.add(ident.getFnr());
                }
            } catch (JMSException e) {
                log.error("Kunne ikke hente lege " + ident + " fra TSS", e);
            }
        }

        return opprettedeLeger;
    }

    private List<Person> hentPersondataFraHodejegeren(String miljoe, List<String> identer) {
        List<Person> identerMedNavn = new ArrayList<>(identer.size());

        for (String ident : identer) {
            identerMedNavn.add(new Person(ident, hodejegerenConsumer.getPersondata(ident, miljoe).getKortnavn()));
        }

        return identerMedNavn;
    }
}
