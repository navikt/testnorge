package no.nav.registre.tss.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.tss.consumer.rs.response.Response110;
import no.nav.registre.tss.consumer.rs.response.Response910;
import no.nav.registre.tss.consumer.rs.response.TssMessage;
import no.nav.registre.tss.domain.Person;
import no.nav.registre.tss.domain.Samhandler;
import no.nav.registre.tss.domain.TssType;
import no.nav.registre.tss.domain.TssTypeGruppe;
import no.nav.registre.tss.provider.rs.request.Rutine130Request;
import no.nav.registre.tss.utils.Rutine110Util;
import no.nav.registre.tss.utils.rutine130.Rutine130Util;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdentService {

    private final HodejegerenConsumer hodejegerenConsumer;

    private final SyntetiseringService syntetiseringService;

    private final JmsService jmsService;

    private final EregService eregService;


    public List<String> opprettLegeITss(String miljoe, String ident, TssType type) {
        List<Samhandler> leger = hentPersondataFraHodejegeren(miljoe, Collections.singletonList(ident)).stream()
                .map(person -> new Samhandler(person, type))
                .collect(Collectors.toList());
        return opprettSamhandler(miljoe, leger);
    }

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
        return jmsService.sendOgMotta910RutineFraTss(utvalgteLeger.stream()
                .map(ident -> new Samhandler(new Person(ident, ""), TssType.LE))
                .collect(Collectors.toList()), koeNavn);
    }

    public Response910 hentSamhandlerFraTss(String ident, TssType type, String miljoe) throws JMSException {
        String koeNavn = jmsService.hentKoeNavnSamhandler(miljoe);
        return jmsService.sendOgMotta910RutineFraTss(ident, type, koeNavn);
    }

    public List<String> opprettSamhandlereITss(String miljoe, List<TssTypeGruppe> eksluderteGrupper, List<String> identer) throws JMSException {

        var tssResponse = jmsService.sendOgMotta910RutineFraTss(identer.stream()
                .map(ident -> new Samhandler(new Person(ident, ""), TssType.LE)).collect(Collectors.toList()), jmsService.hentKoeNavnSamhandler(miljoe));

        var ikkeEksisterendeIdenter = identer.stream().filter(ident -> !tssResponse.containsKey(ident)).collect(Collectors.toList());

        List<Samhandler> samhandlere = getSamhandlere(miljoe, eksluderteGrupper, ikkeEksisterendeIdenter);
        Map<TssType, List<Samhandler>> samhandlerForType = samhandlere.stream()
                .collect(Collectors.groupingBy(Samhandler::getType));


        Map<TssType, List<String>> orgnrForType = eregService.hentEnheterIEreg();
        if (orgnrForType.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.FAILED_DEPENDENCY, "Bedrifter må eksistere i EREG og være lagt til i csv fil lokalt i testnorge-tss. Kontakt administrator av applikasjonen");
        }

        for (var entry : samhandlerForType.entrySet()) {
            if (!TssTypeGruppe.skalHaOrgnummer(TssTypeGruppe.getGruppe(entry.getKey()))) {
                continue;
            }

            List<String> orgnr = orgnrForType.get(entry.getKey());
            List<Samhandler> samhandlerListe = entry.getValue();
            for (int i = 0; i < samhandlerListe.size(); i++) {
                if (orgnr.size() <= i) {
                    log.info("Ikke nok orgnummere i forhold til samhandlere. Fjerner {} samhandlere", samhandlerListe.size() - orgnr.size());
                    samhandlerListe = samhandlerListe.subList(0, i);
                    break;
                }
                samhandlerListe.get(i).setIdent(orgnr.get(i));
            }
            entry.setValue(samhandlerListe);
        }

        return opprettSamhandler(miljoe, samhandlerForType.values().stream().flatMap(List::stream).collect(Collectors.toList()));
    }

    private List<Samhandler> getSamhandlere(String miljoe, List<TssTypeGruppe> eksluderteGrupper, List<String> identer) {
        List<Person> personer = hentPersondataFraHodejegeren(miljoe, identer);
        int size = (int) List.of(TssType.values()).stream().filter(type -> !TssTypeGruppe.skalHaOrgnummer(TssTypeGruppe.getGruppe(type))).count();

        Integer chunkSize = (personer.size() / size);
        log.info("Lik fordeling blant samhandlere på størrelse " + chunkSize);
        int counter = 0;
        List<Samhandler> samhandlere = new ArrayList<>();
        for (TssType type : TssType.values()) {
            TssTypeGruppe gruppe = TssTypeGruppe.getGruppe(type);
            if (eksluderteGrupper.contains(gruppe)) {
                continue;
            }
            if (!TssTypeGruppe.skalHaOrgnummer(gruppe)) {
                samhandlere.addAll(personer.subList(counter, counter + chunkSize).stream()
                        .map(person -> new Samhandler(person, type))
                        .collect(Collectors.toList()));
                counter += chunkSize;
            } else if (TssTypeGruppe.skalHaOrgnummer(gruppe)) {
                samhandlere.addAll(IntStream.range(0, chunkSize).mapToObj(ignored -> new Samhandler(new Person("", ""), type)).collect(Collectors.toList()));
            }
        }
        log.info("Prøver å opprette {} antall samhandlere", samhandlere.size());
        return samhandlere;
    }

    public String leggTilAdresse(String ident, TssType type, String miljoe, Rutine130Request message) {
        Response910 response910 = null;
        StringBuilder fullRutine = new StringBuilder();
        try {
            response910 = hentSamhandlerFraTss(ident, type, miljoe);
        } catch (JMSException e) {
            log.error("Kunne ikke hente samhandler fra TSS", e);
        }

        if (response910 != null) {
            Response110 response110 = response910.getResponse110().get(0);
            TssMessage rutine110 = TssMessage.builder()
                    .idKode(response110.getIdKode())
                    .idOff(response110.getIdOff())
                    .kodeIdenttype(response110.getKodeIdenttype())
                    .kodeSamhType(response110.getKodeSamhType())
                    .navn(response110.getNavn())
                    .oppdater("N")
                    .build();
            String rutine110Flatfil = Rutine110Util.opprett110Rutine(rutine110);
            String rutine130Flatfil = Rutine130Util.opprett130Rutine(message);
            fullRutine
                    .append(rutine110Flatfil)
                    .append(rutine130Flatfil);
        }

        return fullRutine.toString();
    }

    private List<Person> hentPersondataFraHodejegeren(String miljoe, List<String> identer) {
        List<Person> identerMedNavn = new ArrayList<>(identer.size());

        for (String ident : identer) {
            identerMedNavn.add(new Person(ident, hodejegerenConsumer.getPersondata(ident, miljoe).getKortnavn()));
        }

        return identerMedNavn;
    }

    public List<String> opprettSamhandler(String miljoe, List<Samhandler> samhandlere) {
        String koeNavnAjourhold = jmsService.hentKoeNavnAjour(miljoe);
        String koeNavnSamhandler = jmsService.hentKoeNavnSamhandler(miljoe);

        List<String> syntetiskeTssRutiner = syntetiseringService.opprettSyntetiskeTssRutiner(samhandlere);
        log.info("Syntetisk rutiner hentet fra synt: {}", syntetiskeTssRutiner);
        jmsService.sendTilTss(syntetiskeTssRutiner, koeNavnAjourhold);
        log.info("Sendt til TSS");
        List<String> opprettedeSamhandlere = new ArrayList<>();

        for (Samhandler samhandler : samhandlere) {
            try {
                Response910 response = jmsService.sendOgMotta910RutineFraTss(samhandler.getIdent(), samhandler.getType(), koeNavnSamhandler);
                if (!response.getResponse110().isEmpty() || !response.getResponse111().isEmpty() || !response.getResponse125().isEmpty()) {
                    opprettedeSamhandlere.add(samhandler.getIdent());
                }
                log.info("Svar fra TSS110 på ident {}: {}", samhandler.getIdent(), response.getResponse110());
                log.info("Svar fra TSS111 på ident {}: {}", samhandler.getIdent(), response.getResponse111());
                log.info("Svar fra TSS125 på ident {}: {}", samhandler.getIdent(), response.getResponse125());
            } catch (JMSException e) {
                log.error("Kunne ikke hente samhandler " + samhandler.getIdent() + " fra TSS", e);
            }
        }

        return opprettedeSamhandlere;
    }
}
