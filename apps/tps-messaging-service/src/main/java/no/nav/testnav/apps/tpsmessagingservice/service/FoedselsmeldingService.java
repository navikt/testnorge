package no.nav.testnav.apps.tpsmessagingservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.dto.endringsmeldinger.SkdMeldingTrans1;
import no.nav.testnav.apps.tpsmessagingservice.service.skd.FoedselsmeldingBuilderService;
import no.nav.testnav.apps.tpsmessagingservice.service.skd.SendSkdMeldinger;
import no.nav.testnav.apps.tpsmessagingservice.utils.ExtractErrorStatus;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.FoedselsmeldingRequest;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.FoedselsmeldingResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FoedselsmeldingService {

    private final SendSkdMeldinger sendSkdMeldinger;
    private final FoedselsmeldingBuilderService foedselsmeldingBuilderService;

    public FoedselsmeldingResponse sendFoedselsmelding(FoedselsmeldingRequest persondata, List<String> miljoer) {

        SkdMeldingTrans1 melding = foedselsmeldingBuilderService.build(persondata);
        var skdMelding = melding.toString();

        var miljoerStatus = sendSkdMeldinger.sendMeldinger(skdMelding, miljoer);
        prepareStatus(miljoerStatus);

        return FoedselsmeldingResponse.builder()
                .ident(persondata.getBarn().getIdent())
                .miljoStatus(miljoerStatus)
                .build();
    }

    private void prepareStatus(Map<String, String> sentStatus) {

        sentStatus.replaceAll((env, status) -> status.matches("^00.*") ? "OK" : ExtractErrorStatus.extract(status));
    }
}
