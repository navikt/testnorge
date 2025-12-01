package no.nav.testnav.apps.tpsmessagingservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.consumer.TestmiljoerServiceConsumer;
import no.nav.testnav.apps.tpsmessagingservice.service.skd.FoedselsmeldingBuilderService;
import no.nav.testnav.apps.tpsmessagingservice.service.skd.SendSkdMeldinger;
import no.nav.testnav.apps.tpsmessagingservice.utils.ResponseStatus;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.FoedselsmeldingRequest;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.FoedselsmeldingResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FoedselsmeldingService {

    private final SendSkdMeldinger sendSkdMeldinger;
    private final FoedselsmeldingBuilderService foedselsmeldingBuilderService;
    private final TestmiljoerServiceConsumer testmiljoerServiceConsumer;

    public FoedselsmeldingResponse sendFoedselsmelding(FoedselsmeldingRequest persondata, List<String> miljoer) {

        if (miljoer.isEmpty()) {
            miljoer = testmiljoerServiceConsumer.getMiljoer();
        }

        var skdMelding = foedselsmeldingBuilderService.build(persondata);

        var miljoerStatus = sendSkdMeldinger.sendMeldinger(skdMelding.toString(), miljoer);
        prepareStatus(miljoerStatus);

        return FoedselsmeldingResponse.builder()
                .ident(persondata.getBarn().getIdent())
                .miljoStatus(miljoerStatus)
                .build();
    }

    private void prepareStatus(Map<String, String> sentStatus) {

        sentStatus.replaceAll((env, status) -> status.matches("^00.*") ? "OK" : ResponseStatus.extract(status));
    }
}
