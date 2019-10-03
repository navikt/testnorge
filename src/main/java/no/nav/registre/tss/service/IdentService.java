package no.nav.registre.tss.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.tss.consumer.rs.responses.Response910;
import no.nav.registre.tss.domain.Person;
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
        return opprettSamhandler(miljoe, identer, TssType.LE);
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

        Map<TssType, List<String>> samhandlerMapping = Stream.of(TssType.values()).collect(Collectors.toMap(Function.identity(), f -> new ArrayList<>()));

        int chunkSize = (identer.size() / TssType.values().length) - 1;
        log.info("Lik fordeling blant samhandlere på størrelse " + chunkSize);

        int counter = 0;

        for (Map.Entry<TssType, List<String>> entry : samhandlerMapping.entrySet()) {
            entry.getValue().addAll(identer.subList(counter, counter + chunkSize));
            counter += chunkSize;
        }

        log.info("Antall identer ikke brukt: {}", identer.size() - counter);

        return samhandlerMapping.entrySet().stream()
                .map(entry -> opprettSamhandler(miljoe, entry.getValue(), entry.getKey()))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<Person> hentPersondataFraHodejegeren(String miljoe, List<String> identer) {
        List<Person> identerMedNavn = new ArrayList<>(identer.size());

        for (String ident : identer) {
            identerMedNavn.add(new Person(ident, hodejegerenConsumer.getPersondata(ident, miljoe).getKortnavn()));
        }

        return identerMedNavn;
    }

    private List<String> opprettSamhandler(String miljoe, List<String> identer, TssType type) {
        String koeNavnAjourhold = jmsService.hentKoeNavnAjour(miljoe);
        String koeNavnSamhandler = jmsService.hentKoeNavnSamhandler(miljoe);

        List<Person> identerMedNavn = hentPersondataFraHodejegeren(miljoe, identer);

        List<String> syntetiskeTssRutiner = syntetiseringService.opprettSyntetiskeTssRutiner(identerMedNavn, type);

        jmsService.sendTilTss(syntetiskeTssRutiner, koeNavnAjourhold);

        List<String> opprettedeSamhandlere = new ArrayList<>();

        for (Person ident : identerMedNavn) {
            try {
                Response910 response = jmsService.sendOgMotta910RutineFraTss(ident.getFnr(), koeNavnSamhandler);
                if (!response.getResponse110().isEmpty() || !response.getResponse111().isEmpty() || !response.getResponse125().isEmpty()) {
                    opprettedeSamhandlere.add(ident.getFnr());
                }
            } catch (JMSException e) {
                log.error("Kunne ikke hente samhandler " + ident + " fra TSS", e);
            }
        }

        return opprettedeSamhandlere;
    }
}
