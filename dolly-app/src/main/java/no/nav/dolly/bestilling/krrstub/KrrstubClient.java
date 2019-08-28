package no.nav.dolly.bestilling.krrstub;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KrrstubClient implements ClientRegister {

    private final KrrstubConsumer krrstubConsumer;
    private final KrrstubResponseHandler krrstubResponseHandler;
    private final MapperFacade mapperFacade;

    @Override
    public void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress) {

        if (bestilling.getKrrstub() == null) {
            progress.setKrrstubStatus(null);
            return;
        }

        try {
            DigitalKontaktdata digitalKontaktdata = mapperFacade.map(bestilling.getKrrstub(), DigitalKontaktdata.class);
            digitalKontaktdata.setPersonident(norskIdent.getIdent());

            DigitalKontaktdata[] response = krrstubConsumer.readDigitalKontaktdata(norskIdent.getIdent());

            if (response != null) {
                asList(response).forEach(dkif -> {
                    if (nonNull(dkif.getId())) {
                        krrstubConsumer.deleteDigitalKontaktdata(dkif.getId());
                    }
                });
            }

            ResponseEntity<Object> krrstubResponse = krrstubConsumer.createDigitalKontaktdata(digitalKontaktdata);
            progress.setKrrstubStatus(krrstubResponseHandler.extractResponse(krrstubResponse));

        } catch (RuntimeException e) {
            progress.setKrrstubStatus("Feil: " + e.getMessage());
            log.error("Kall til KrrStub feilet: {}", e.getMessage(), e);
        }

    }
}