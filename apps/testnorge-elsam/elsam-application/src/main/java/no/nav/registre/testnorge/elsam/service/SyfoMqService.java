package no.nav.registre.testnorge.elsam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.elsam.consumer.rs.response.SykemeldingResponse;
import no.nav.registre.testnorge.elsam.exception.InvalidEnvironmentException;

@Service
@DependencyOn(value = "syfo-dokmottak", external = true)
public class SyfoMqService {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("#{${miljoer.med.koenavn}}")
    private Map<String, String> miljoerMedKoeNavn;

    public void leggTilMeldingerPaaKoe(List<SykemeldingResponse> syntetiserteSykemeldinger, String koeNavn) {
        syntetiserteSykemeldinger.stream().peek(melding -> jmsTemplate.convertAndSend(koeNavn, melding));
    }

    public void opprettSykmeldingNyttMottak(String miljoe, String xml) throws InvalidEnvironmentException {
        var koeNavn = miljoerMedKoeNavn.get(miljoe.toLowerCase());
        if (koeNavn == null) {
            throw new InvalidEnvironmentException("Miljø " + miljoe + " er ikke støttet. Gyldige miljøer: " + miljoerMedKoeNavn.keySet());
        }
        jmsTemplate.send(koeNavn, session -> session.createTextMessage(xml));
    }
}
