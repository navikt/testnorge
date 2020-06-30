package no.nav.dolly.bestilling.sykemelding.syntSykemelding;

import static java.util.Objects.nonNull;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.sykemelding.syntSykemelding.domain.SyntSykemeldingRequest;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyntSykemeldingClient implements ClientRegister {

    private final SyntSykemeldingConsumer syntSykemeldingConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getSyntSykemelding())) {

            StringBuilder status = new StringBuilder();
            SyntSykemeldingRequest syntSykemeldingRequest = mapperFacade.map(bestilling.getSyntSykemelding(), SyntSykemeldingRequest.class);

            bestilling.getEnvironments().forEach(environment -> {

                try {
                    ResponseEntity<String> response = syntSykemeldingConsumer.postSyntSykemelding(syntSykemeldingRequest);
                    if (response.hasBody()) {
                        status.append(',')
                                .append(environment)
                                .append(":OK");
                    }
                } catch (RuntimeException e) {

                    status.append(',')
                            .append(environment)
                            .append(':')
                            .append(errorStatusDecoder.decodeRuntimeException(e));

                    log.error("Feilet å legge inn person: {} til syntSykemelding miljø: {}",
                            syntSykemeldingRequest.getIdent(), environment, e);
                }
            });
            progress.setSyntSykemeldingStatus(status.substring(1));
        }
    }

    @Override
    public void release(List<String> identer) {

    }
}
