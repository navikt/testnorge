package no.nav.testnav.apps.tpsmessagingservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.consumer.TestmiljoerServiceConsumer;
import no.nav.testnav.apps.tpsmessagingservice.service.skd.DoedsmeldingAnnulleringBuilderService;
import no.nav.testnav.apps.tpsmessagingservice.service.skd.DoedsmeldingBuilderService;
import no.nav.testnav.apps.tpsmessagingservice.service.skd.SendSkdMeldinger;
import no.nav.testnav.apps.tpsmessagingservice.utils.ResponseStatus;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.DoedsmeldingResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DoedsmeldingService {

    private final SendSkdMeldinger sendSkdMeldinger;
    private final DoedsmeldingBuilderService doedsmeldingBuilderService;
    private final DoedsmeldingAnnulleringBuilderService doedsmeldingAnnulleringBuilderService;
    private final TestmiljoerServiceConsumer testmiljoerServiceConsumer;

    public DoedsmeldingResponse sendDoedsmelding(String ident, LocalDate doedsdato, List<String> miljoer) {

        if (miljoer.isEmpty()) {
            miljoer = testmiljoerServiceConsumer.getMiljoer();
        }

        var melding = doedsmeldingBuilderService.build(ident, doedsdato);
        var skdMelding = melding.toString();

        var miljoerStatus = sendSkdMeldinger.sendMeldinger(skdMelding, miljoer);
        prepareStatus(miljoerStatus);

        return DoedsmeldingResponse.builder()
                .ident(ident)
                .miljoStatus(miljoerStatus)
                .build();
    }

    public DoedsmeldingResponse annulerDoedsmelding(PersonDTO person, List<String> miljoer) {

        if (miljoer.isEmpty()) {
            miljoer = testmiljoerServiceConsumer.getMiljoer();
        }

        var melding = doedsmeldingAnnulleringBuilderService.execute(person);
        var skdMelding = melding.toString();

        var miljoerStatus = sendSkdMeldinger.sendMeldinger(skdMelding, miljoer);
        prepareStatus(miljoerStatus);

        return DoedsmeldingResponse.builder()
                .ident(person.getIdent())
                .miljoStatus(miljoerStatus)
                .build();
    }

    private void prepareStatus(Map<String, String> sentStatus) {

        sentStatus.replaceAll((env, status) -> status.matches("^00.*") ? "OK" : ResponseStatus.extract(status));
    }
}