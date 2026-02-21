package no.nav.testnav.apps.tpsmessagingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tpsmessagingservice.consumer.TestmiljoerServiceConsumer;
import no.nav.testnav.apps.tpsmessagingservice.service.skd.DoedsmeldingAnnulleringBuilderService;
import no.nav.testnav.apps.tpsmessagingservice.service.skd.DoedsmeldingBuilderService;
import no.nav.testnav.apps.tpsmessagingservice.service.skd.SendSkdMeldinger;
import no.nav.testnav.apps.tpsmessagingservice.utils.ResponseStatus;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.DoedsmeldingRequest;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.DoedsmeldingResponse;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoedsmeldingService {

    private final SendSkdMeldinger sendSkdMeldinger;
    private final DoedsmeldingBuilderService doedsmeldingBuilderService;
    private final DoedsmeldingAnnulleringBuilderService doedsmeldingAnnulleringBuilderService;
    private final TestmiljoerServiceConsumer testmiljoerServiceConsumer;

    public DoedsmeldingResponse sendDoedsmelding(DoedsmeldingRequest request, List<String> miljoer) {

        if (miljoer.isEmpty()) {
            miljoer = testmiljoerServiceConsumer.getMiljoer();
        }

        var skdMelding = doedsmeldingBuilderService.build(request);

        var miljoerStatus = sendSkdMeldinger.sendMeldinger(skdMelding.toString(), miljoer);
        prepareStatus(miljoerStatus);

        return DoedsmeldingResponse.builder()
                .ident(request.getIdent())
                .miljoStatus(miljoerStatus)
                .build();
    }

    public DoedsmeldingResponse annulerDoedsmelding(PersonDTO person, List<String> miljoer) {

        if (miljoer.isEmpty()) {
            miljoer = testmiljoerServiceConsumer.getMiljoer();
        }

        var skdMelding = doedsmeldingAnnulleringBuilderService.execute(person);

        var miljoerStatus = sendSkdMeldinger.sendMeldinger(skdMelding.toString(), miljoer);
        prepareStatus(miljoerStatus);

        return DoedsmeldingResponse.builder()
                .ident(person.getIdent())
                .miljoStatus(miljoerStatus)
                .build();
    }

    private void prepareStatus(Map<String, String> sentStatus) {

        sentStatus.forEach((env, status) -> log.info("Doedsmelding i miljø {} status {} ", env, status));

        sentStatus.replaceAll((env, status) -> status.matches("^00.*") ? "OK" : ResponseStatus.extract(status));
    }
}