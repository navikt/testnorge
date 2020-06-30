package no.nav.registre.frikort.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponse;
import no.nav.registre.frikort.domain.xml.Borger;
import no.nav.registre.frikort.domain.xml.Egenandel;
import no.nav.registre.frikort.domain.xml.EgenandelListe;
import no.nav.registre.frikort.domain.xml.Egenandelsmelding;
import no.nav.registre.frikort.domain.xml.Samhandler;
import no.nav.registre.frikort.domain.xml.SamhandlerListe;

@Slf4j
@Service
@RequiredArgsConstructor
public class KonverteringService {

    private final Marshaller marshaller;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH.mm.ss,SSSSSS");
    private final Random rand;

    public List<String> konverterEgenandelerTilXmlString(Map<String, List<SyntFrikortResponse>> egenandeler) throws JAXBException {
        try {
            var egenandelsmeldingListe = lagEgenandelsmeldingListe(egenandeler);

            var xmlMeldinger = new ArrayList<String>(egenandelsmeldingListe.size());
            for (var melding : egenandelsmeldingListe) {
                xmlMeldinger.add(konverterEgenandelsmeldingTilXMLString(melding));
            }

            return xmlMeldinger;
        } catch (JAXBException e) {
            log.error("Kunne ikke konvertere til xml.");
            throw e;
        }
    }

    private List<Egenandelsmelding> lagEgenandelsmeldingListe(Map<String, List<SyntFrikortResponse>> egenandeler) {
        var egenandelsmeldingListe = new ArrayList<Egenandelsmelding>();

        egenandeler.forEach((id, infoListe) -> {
            for (var res : infoListe) {
                var listeAvEgenandeler = lagEgenandelsListe(res, id);
                var listeAvSamhandlere = lagSamhandlerListe(res, listeAvEgenandeler);

                var egenandelsmelding = Egenandelsmelding.builder()
                        .avsender(res.getKildesystemkode())
                        .listeAvSamhandlere(listeAvSamhandlere)
                        .datoSendt(LocalDateTime.now())
                        .build();

                egenandelsmeldingListe.add(egenandelsmelding);
            }
        });

        return egenandelsmeldingListe;
    }

    private EgenandelListe lagEgenandelsListe(
            SyntFrikortResponse res,
            String id
    ) {
        var egenandelListe = new ArrayList<Egenandel>();

        var borger = Borger.builder().borgerid(id).build();

        var egenandel = Egenandel.builder()
                .betaltEgenandel(konverterTilBoolean(res.getBetalt()))
                .borger(borger)
                .datoMottatt(LocalDateTime.parse(res.getDato_mottatt(), formatter))
                .datoTjeneste(LocalDateTime.parse(res.getDatotjeneste(), formatter))
                .egenandelsbelop(res.getEgenandelsats())
                .egenandelsats(res.getEgenandelsats())
                .egenandelskode(res.getEgenandelskode())
                .enkeltregningsstatus(res.getEnkeltregningsstatuskode())
                .build();

        settGyldigeDatoer(egenandel);

        egenandelListe.add(egenandel);

        return EgenandelListe.builder().egenandelListe(egenandelListe).build();
    }

    private SamhandlerListe lagSamhandlerListe(
            SyntFrikortResponse res,
            EgenandelListe listeAvEgenandeler
    ) {
        var samhandlerListe = new ArrayList<Samhandler>();

        var samhandler = Samhandler.builder()
                .type(res.getSamhandlertypekode())
                .innsendingstype(res.getInnsendingstypekode())
                .listeAvEgenandeler(listeAvEgenandeler)
                .datoMottattEkstern(LocalDate.now().atStartOfDay())
                .datoGenerert(LocalDate.now().atStartOfDay())
                .build();
        samhandlerListe.add(samhandler);

        return SamhandlerListe.builder().samhandlerListe(samhandlerListe).build();
    }

    private String konverterEgenandelsmeldingTilXMLString(Egenandelsmelding melding) throws JAXBException {
        var sw = new StringWriter();

        // convert object to XML
        marshaller.marshal(melding, sw);

        return sw.toString();
    }

    private void settGyldigeDatoer(Egenandel egenandel) {
        try {
            var datoMottatt = LocalDateTime.parse(egenandel.getDatoMottatt());
            var datoTjeneste = LocalDateTime.parse(egenandel.getDatoTjeneste());
            if (ChronoUnit.WEEKS.between(datoMottatt, LocalDateTime.now()) > 12) {
                datoMottatt = LocalDateTime.now().minusWeeks(rand.nextInt(6)).minusWeeks(1);
                datoTjeneste = datoMottatt.minusDays(rand.nextInt(7));
            }
            egenandel.setDatoMottatt(datoMottatt);
            egenandel.setDatoTjeneste(datoTjeneste);
        } catch (DateTimeParseException e) {
            log.error("Kunne ikke oppdatere datofelt i syntetisk egenmelding", e);
        }
    }

    private boolean konverterTilBoolean(String value) {
        return "1".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value);
    }
}
