package no.nav.registre.tss.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.tss.consumer.rs.response.Response910;
import no.nav.registre.tss.domain.Person;
import no.nav.registre.tss.domain.Samhandler;
import no.nav.registre.tss.domain.TssType;

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
        List<Samhandler> leger = hentPersondataFraHodejegeren(miljoe, identer).stream()
                .map(person -> new Samhandler(person, TssType.LE))
                .collect(Collectors.toList());
        return opprettSamhandler(miljoe, leger);
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

    public List<String> opprettSamhandlereITss(String miljoe, List<String> identer) {

        List<Person> personer = hentPersondataFraHodejegeren(miljoe, identer);

        int chunkSize = (identer.size() / TssType.values().length) - 1;
        log.info("Lik fordeling blant samhandlere på størrelse " + chunkSize);

        int counter = 0;
        List<Samhandler> samhandlere = new ArrayList<>();
        for (TssType type : TssType.values()) {
            samhandlere.addAll(personer.subList(counter, counter + chunkSize).stream()
                    .map(person -> new Samhandler(person, type))
                    .collect(Collectors.toList()));
            counter += chunkSize;
        }

        log.info("Antall identer ikke brukt: {}", identer.size() - counter);

        return opprettSamhandler(miljoe, samhandlere);
    }

    private List<Person> hentPersondataFraHodejegeren(String miljoe, List<String> identer) {
        List<Person> identerMedNavn = new ArrayList<>(identer.size());

        for (String ident : identer) {
            identerMedNavn.add(new Person(ident, hodejegerenConsumer.getPersondata(ident, miljoe).getKortnavn()));
        }

        return identerMedNavn;
    }

    private List<String> opprettSamhandler(String miljoe, List<Samhandler> identer) {
        String koeNavnAjourhold = jmsService.hentKoeNavnAjour(miljoe);
        String koeNavnSamhandler = jmsService.hentKoeNavnSamhandler(miljoe);

        List<String> syntetiskeTssRutiner = syntetiseringService.opprettSyntetiskeTssRutiner(identer);

        jmsService.sendTilTss(syntetiskeTssRutiner, koeNavnAjourhold);

        List<String> opprettedeSamhandlere = new ArrayList<>();

        for (Samhandler ident : identer) {
            try {
                Response910 response = jmsService.sendOgMotta910RutineFraTss(ident.getIdent(), koeNavnSamhandler);
                if (!response.getResponse110().isEmpty() || !response.getResponse111().isEmpty() || !response.getResponse125().isEmpty()) {
                    opprettedeSamhandlere.add(ident.getIdent());
                }
            } catch (JMSException e) {
                log.error("Kunne ikke hente samhandler " + ident + " fra TSS", e);
            }
        }

        return opprettedeSamhandlere;
    }
}
