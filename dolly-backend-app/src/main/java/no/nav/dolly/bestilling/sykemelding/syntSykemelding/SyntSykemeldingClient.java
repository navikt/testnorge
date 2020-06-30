package no.nav.dolly.bestilling.sykemelding.syntSykemelding;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.SYNT_SYKEMELDING;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.sykemelding.syntSykemelding.domain.SyntSykemeldingRequest;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TransaksjonMappingService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyntSykemeldingClient implements ClientRegister {

    private final SyntSykemeldingConsumer syntSykemeldingConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransaksjonMappingService transaksjonMappingService;
    private final MapperFacade mapperFacade;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getSyntSykemelding())) {

            StringBuilder status = new StringBuilder();
            SyntSykemeldingRequest syntSykemeldingRequest = mapperFacade.map(bestilling.getSyntSykemelding(), SyntSykemeldingRequest.class);

            bestilling.getEnvironments().forEach(environment -> {

                if (!transaksjonMappingService.existAlready(SYNT_SYKEMELDING, tpsPerson.getHovedperson(), environment) || isOpprettEndre) {
                    try {
                        ResponseEntity<String> response = syntSykemeldingConsumer.postSyntSykemelding(syntSykemeldingRequest);
                        if (response.hasBody()) {
                            status.append(',')
                                    .append(environment)
                                    .append(":OK");

                            saveTranskasjonId(response.getBody(), tpsPerson.getHovedperson(), environment);
                        }

                    } catch (RuntimeException e) {

                        status.append(',')
                                .append(environment)
                                .append(':')
                                .append(errorStatusDecoder.decodeRuntimeException(e));

                        log.error("Feilet å legge inn person: {} til Synt Sykemelding miljø: {}",
                                syntSykemeldingRequest.getIdent(), environment, e);
                    }
                }
            });
            progress.setDokarkivStatus(status.length() > 0 ? status.substring(1) : null);
        }
    }

    @Override
    public void release(List<String> identer) {

    }

    private void saveTranskasjonId(String response, String ident, String miljoe) {

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(ident)
                        .transaksjonId(response) // Hva skal vi bruke her?
                        .datoEndret(LocalDateTime.now())
                        .miljoe(miljoe)
                        .system(SYNT_SYKEMELDING.name())
                        .build());
    }
}
