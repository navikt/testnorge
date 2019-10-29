package no.nav.dolly.bestilling.krrstub;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.nonNull;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

@Slf4j
@Service
@RequiredArgsConstructor
public class KrrstubClient implements ClientRegister {

    private final KrrstubConsumer krrstubConsumer;
    private final KrrstubResponseHandler krrstubResponseHandler;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Timed(name = "providers", tags={"operation", "gjenopprettKrrStub"})
    @Override public void gjenopprett(RsDollyBestillingRequest bestilling, TpsPerson tpsPerson, BestillingProgress progress) {

        if (nonNull(bestilling.getKrrstub())) {

            try {
                DigitalKontaktdata digitalKontaktdata = mapperFacade.map(bestilling.getKrrstub(), DigitalKontaktdata.class);
                digitalKontaktdata.setPersonident(tpsPerson.getHovedperson());

                deleteIdent(tpsPerson.getHovedperson());

                ResponseEntity krrstubResponse = krrstubConsumer.createDigitalKontaktdata(digitalKontaktdata);
                progress.setKrrstubStatus(krrstubResponseHandler.extractResponse(krrstubResponse));

            } catch (RuntimeException e) {

                progress.setKrrstubStatus(errorStatusDecoder.decodeRuntimeException(e));
                log.error("Kall til KrrStub feilet: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    public void release(List<String> identer) {

        identer.forEach(this::deleteIdent);
    }

    private void deleteIdent(String ident) {

        try {
            ResponseEntity<DigitalKontaktdata[]> response = krrstubConsumer.readDigitalKontaktdata(ident);

            if (response.hasBody()) {
                newArrayList(response.getBody()).forEach(dkif -> {
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
