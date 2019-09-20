package no.nav.registre.tss.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.tss.consumer.rs.responses.Response910;
import no.nav.registre.tss.domain.Person;

@Service
@Slf4j
public class IdentService {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private SyntetiseringService syntetiseringService;

    @Autowired
    private JmsService jmsService;

    public List<String> opprettLegerITss(String miljoe, List<String> identer) {
        String koeNavnAjourhold = jmsService.hentKoeNavnAjour(miljoe);
        String koeNavnSamhandler = jmsService.hentKoeNavnSamhandler(miljoe);

        List<Person> identerMedNavn = hentPersondataFraHodejegeren(miljoe, identer);

        List<String> syntetiskeTssRutiner = syntetiseringService.opprettSyntetiskeTssRutiner(identerMedNavn);

        jmsService.sendTilTss(syntetiskeTssRutiner, koeNavnAjourhold);

        List<String> opprettedeLeger = new ArrayList<>();

        for (Person ident : identerMedNavn) {
            try {
                Response910 response = jmsService.sendOgMotta910RutineFraTss(ident.getFnr(), koeNavnSamhandler);
                if (!response.getResponse110().isEmpty() || !response.getResponse111().isEmpty() || !response.getResponse125().isEmpty()) {
                    opprettedeLeger.add(ident.getFnr());
                }
            } catch (JMSException e) {
                log.error("Kunne ikke hente lege " + ident + " fra TSS", e);
            }
        }

        return opprettedeLeger;
    }

    public Map<String, Response910> hentLegerIAvspillergruppeFraTss(Long avspillergruppeId, Integer antallLeger, String miljoe) throws JMSException {
        String koeNavn = jmsService.hentKoeNavnSamhandler(miljoe);
        List<String> alleLeger = hodejegerenConsumer.getLevende(avspillergruppeId);
        List<String> utvalgteLeger = new ArrayList<>();
        if (antallLeger != null) {
            Collections.shuffle(alleLeger);
            utvalgteLeger.addAll(alleLeger.subList(0, antallLeger));
        } else {
            utvalgteLeger.addAll(alleLeger);
        }
        return jmsService.sendOgMotta910RutineFraTss(utvalgteLeger, koeNavn);
    }

    public Response910 hentLegeFraTss(String ident, String miljoe) throws JMSException {
        String koeNavn = jmsService.hentKoeNavnSamhandler(miljoe);
        return jmsService.sendOgMotta910RutineFraTss(ident, koeNavn);
    }

    private List<Person> hentPersondataFraHodejegeren(String miljoe, List<String> identer) {
        List<Person> identerMedNavn = new ArrayList<>(identer.size());

        for (String ident : identer) {
            identerMedNavn.add(new Person(ident, hodejegerenConsumer.getPersondata(ident, miljoe).getKortnavn()));
        }

        return identerMedNavn;
    }
}
