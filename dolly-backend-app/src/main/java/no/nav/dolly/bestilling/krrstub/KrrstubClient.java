package no.nav.dolly.bestilling.krrstub;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KrrstubClient implements ClientRegister {

    private final KrrstubConsumer krrstubConsumer;
    private final KrrstubResponseHandler krrstubResponseHandler;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Override
    @Timed(name = "gjenopprett", tags={"operation", "gjenopprettKrrStub"})
    public void gjenopprett(RsDollyBestillingRequest bestilling, TpsPerson tpsPerson, BestillingProgress progress) {

        if (bestilling.getKrrstub() == null) {
            progress.setKrrstubStatus(null);
            return;
        }

        try {
            DigitalKontaktdata digitalKontaktdata = mapperFacade.map(bestilling.getKrrstub(), DigitalKontaktdata.class);
            digitalKontaktdata.setPersonident(tpsPerson.getHovedperson());

            deleteIdent(tpsPerson.getHovedperson());

            ResponseEntity<Object> krrstubResponse = krrstubConsumer.createDigitalKontaktdata(digitalKontaktdata);
            progress.setKrrstubStatus(krrstubResponseHandler.extractResponse(krrstubResponse));

        } catch (RuntimeException e) {
            progress.setKrrstubStatus(errorStatusDecoder.decodeRuntimeException(e));
            log.error("Kall til KrrStub feilet: {}", e.getMessage(), e);
        }

    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(this::deleteIdent);
    }

    private void deleteIdent(String ident) {

        try {
            DigitalKontaktdata[] response = krrstubConsumer.readDigitalKontaktdata(ident);

            if (response != null) {
                asList(response).forEach(dkif -> {
                    if (nonNull(dkif.getId())) {
                        krrstubConsumer.deleteDigitalKontaktdata(dkif.getId());
                    }
                });
            }
        } catch (RuntimeException e) {
            log.error("Feilet å slette ident {} fra KRR-Stub", ident, e);
        }
    }
}