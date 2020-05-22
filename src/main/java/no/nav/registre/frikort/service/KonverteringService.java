package no.nav.registre.frikort.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponse;
import no.nav.registre.frikort.domain.xml.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class KonverteringService {

    @Autowired
    private Marshaller marshaller;

    public List<String> konverterFrikortTilXmlString(Map<String, List<SyntFrikortResponse>> egenandeler) throws JAXBException {
        try {
            List<Egenandelsmelding> egenandelsmeldingListe = lagEgenandelsmeldingListe(egenandeler);

            List<String> xmlMeldinger = new ArrayList<>();
            for (Egenandelsmelding melding : egenandelsmeldingListe){
                xmlMeldinger.add(konverterEgenandelsmeldingTilXMLString(melding));
            }

            return xmlMeldinger;
        } catch (JAXBException e) {
            log.error("Kunne ikke konvertere til xml.");
            throw e;
        }
    }

    private List<Egenandelsmelding> lagEgenandelsmeldingListe(Map<String, List<SyntFrikortResponse>> egenandeler){
        List<Egenandelsmelding> egenandelsmeldingListe = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH.mm.ss,SSSSSS");

        egenandeler.forEach((id, infoListe) -> {
            for (SyntFrikortResponse res : infoListe) {

                EgenandelListe listeAvEgenandeler = lagEgenandelsListe(res, id, formatter);
                SamhandlerListe listeAvSamhandlere = lagSamhandlerListe(res, listeAvEgenandeler);

                Egenandelsmelding egenandelsmelding = Egenandelsmelding.builder()
                        .avsender(res.getKildesystemkode())
                        .listeAvSamhandlere(listeAvSamhandlere)
                        .datoSendt(LocalDateTime.now())
                        .build();

                egenandelsmeldingListe.add(egenandelsmelding);
            }
        });

        return egenandelsmeldingListe;
    }

    private EgenandelListe lagEgenandelsListe(SyntFrikortResponse res, String id, DateTimeFormatter formatter){
        List<Egenandel> egenandelListe = new ArrayList<>();

        long borgerid = Long.parseLong(id);
        Borger borger = Borger.builder().borgerid(borgerid).build();

        Egenandel egenandel = Egenandel.builder()
                .betaltEgenandel(Boolean.parseBoolean(res.getBetalt()))
                .borger(borger)
                .datoMottatt(LocalDateTime.parse(res.getDato_mottatt(), formatter))
                .datoTjeneste(LocalDateTime.parse(res.getDatotjeneste(), formatter))
                .egenandelsbelop(res.getEgenandelsats())
                .egenandelsats(res.getEgenandelsats())
                .egenandelskode(res.getEgenandelskode())
                .enkeltregningsstatus(res.getEnkeltregningsstatuskode())
                .build();

        egenandelListe.add(egenandel);

        return EgenandelListe.builder().egenandelListe(egenandelListe).build();
    }

    private SamhandlerListe lagSamhandlerListe(SyntFrikortResponse res, EgenandelListe listeAvEgenandeler){
        List<Samhandler> samhandlerListe = new ArrayList<>();

        Samhandler samhandler = Samhandler.builder()
                .type(res.getSamhandlertypekode())
                .listeAvEgenandeler(listeAvEgenandeler)
                .build();
        samhandlerListe.add(samhandler);

        return SamhandlerListe.builder().samhandlerListe(samhandlerListe).build();
    }

    private String konverterEgenandelsmeldingTilXMLString(Egenandelsmelding melding) throws JAXBException {
        StringWriter sw = new StringWriter();

        // convert object to XML
        marshaller.marshal(melding, sw);

        return sw.toString();
    }
}
