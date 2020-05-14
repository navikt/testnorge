package no.nav.registre.frikort.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponse;
import no.nav.registre.frikort.domain.xml.*;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static no.nav.registre.frikort.utils.XMLUtils.convertEgenandelsmeldingToXMLString;

@Slf4j
@Service
public class KonverteringService {

    public List<String> konverterFrikortTilXmlString(Map<String, List<SyntFrikortResponse>> egenandeler) throws JAXBException {
        try {
            List<Egenandelsmelding> egenandelsmeldingListe = new ArrayList<>();

            egenandeler.forEach((id, infoListe) -> {
                for (SyntFrikortResponse res : infoListe) {
                    List<Egenandel> egenandelListe = new ArrayList<>();
                    List<Samhandler> samhandlerListe = new ArrayList<>();

                    long borgerid = Long.parseLong(id);
                    Borger borger = Borger.builder().borgerid(borgerid).build();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH.mm.ss,SSSSSS");

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

                    EgenandelListe listeAvEgenandeler = EgenandelListe.builder().egenandelListe(egenandelListe).build();
                    Samhandler samhandler = Samhandler.builder()
                            .type(res.getSamhandlertypekode())
                            .listeAvEgenandeler(listeAvEgenandeler)
                            .build();
                    samhandlerListe.add(samhandler);

                    Egenandelsmelding egenandelsmelding = Egenandelsmelding.builder()
                            .avsender(res.getKildesystemkode())
                            .listeAvSamhandlere(SamhandlerListe.builder().samhandlerListe(samhandlerListe).build())
                            .datoSendt(LocalDateTime.now())
                            .build();
                    egenandelsmeldingListe.add(egenandelsmelding);
                }
            });

            List<String> meldinger = egenandelsmeldingListe
                    .stream()
                    .map(melding -> {
                        try {
                            return convertEgenandelsmeldingToXMLString(melding);
                        } catch (JAXBException e) {
                            return "";
                        }
                    }).collect(Collectors.toList());
            if (meldinger.contains("")) {
                throw new JAXBException("Kunne ikke konvertere til xml.");
            }
            return meldinger;
        } catch (JAXBException e) {
            log.error("Kunne ikke konvertere til xml.");
            throw e;
        }
    }
}
