package no.nav.dolly.bestilling.dokarkiv;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.DOKARKIV;
import static sun.plugin2.util.PojoUtil.toJson;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.dolly.bestilling.dokarkiv.domain.JoarkTransaksjon;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TransaksjonMappingService;

@Slf4j
@Service
@RequiredArgsConstructor
public class DokarkivClient implements ClientRegister {

    private final DokarkivConsumer dokarkivConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final TransaksjonMappingService transaksjonMappingService;

    @Override
    public void gjenopprett(RsDollyUtvidetBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getDokarkiv())) {

            StringBuilder status = new StringBuilder();
            DokarkivRequest dokarkivRequest = mapperFacade.map(bestilling.getDokarkiv(), DokarkivRequest.class);

            dokarkivRequest.getBruker().setId(tpsPerson.getHovedperson());
            bestilling.getEnvironments().forEach(environment -> {

                if (!transaksjonMappingService.existAlready(DOKARKIV, tpsPerson.getHovedperson(), environment) || isOpprettEndre) {
                    try {
                        ResponseEntity<DokarkivResponse> response = dokarkivConsumer.postDokarkiv(environment, dokarkivRequest);
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

                        log.error("Feilet å legge inn person: {} til Dokarkiv miljø: {}",
                                dokarkivRequest.getBruker().getId(), environment, e);
                    }
                }
            });
            progress.setDokarkivStatus(status.length() > 0 ? status.substring(1) : null);
        }
    }

    @Override
    public void release(List<String> identer) {

    }

    private void saveTranskasjonId(DokarkivResponse response, String ident, String miljoe) {

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(ident)
                        .transaksjonId(toJson(JoarkTransaksjon.builder()
                                .journalpostId(response.getJournalpostId())
                                .dokumentInfoId(response.getDokumenter().get(0).getDokumentInfoId())
                                .build()))
                        .datoEndret(LocalDateTime.now())
                        .miljoe(miljoe)
                        .system(DOKARKIV.name())
                        .build());
    }
}
