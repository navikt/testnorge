package no.nav.dolly.bestilling.dokarkiv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.dolly.bestilling.dokarkiv.domain.JoarkTransaksjon;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.dolly.service.TransaksjonMappingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.DOKARKIV;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVarsel;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.substring;

@Slf4j
@Service
@RequiredArgsConstructor
public class DokarkivClient implements ClientRegister {

    private final DokarkivConsumer dokarkivConsumer;
    private final MapperFacade mapperFacade;
    private final TransaksjonMappingService transaksjonMappingService;
    private final ObjectMapper objectMapper;
    private final DollyPersonCache dollyPersonCache;

    @Override
    public Flux<Void> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getDokarkiv())) {

            StringBuilder status = new StringBuilder();

            if (!dollyPerson.isOpprettetIPDL()) {
                progress.setDokarkivStatus(bestilling.getEnvironments().stream()
                        .map(miljo -> String.format("%s:%s", miljo, encodeStatus(getVarsel("JOARK"))))
                        .collect(Collectors.joining(",")));
                return Flux.just();
            }

            DokarkivRequest dokarkivRequest = mapperFacade.map(bestilling.getDokarkiv(), DokarkivRequest.class);

            dollyPersonCache.fetchIfEmpty(dollyPerson);
            dokarkivRequest.getBruker().setId(dollyPerson.getHovedperson());
            Person avsender = dollyPerson.getPerson(dollyPerson.getHovedperson());
            if (isBlank(dokarkivRequest.getAvsenderMottaker().getId())) {
                dokarkivRequest.getAvsenderMottaker().setId(dollyPerson.getHovedperson());
            }
            if (isBlank(dokarkivRequest.getAvsenderMottaker().getNavn())) {
                dokarkivRequest.getAvsenderMottaker().setNavn(String.format("%s, %s%s", avsender.getFornavn(), avsender.getEtternavn(), isNull(avsender.getMellomnavn()) ? "" : ", " + avsender.getMellomnavn()));
            }

            bestilling.getEnvironments().stream()
                    .filter(StringUtils::isNotBlank)
                    .forEach(environment -> {

                        if (!transaksjonMappingService.existAlready(DOKARKIV, dollyPerson.getHovedperson(), environment) || isOpprettEndre) {

                            var response = dokarkivConsumer.postDokarkiv(environment, dokarkivRequest).block();
                            if (nonNull(response) && isBlank(response.getFeilmelding())) {
                                status.append(',')
                                        .append(environment)
                                        .append(":OK");

                                saveTransaksjonId(response, dollyPerson.getHovedperson(),
                                        progress.getBestilling().getId(), environment);
                            } else {

                                status.append(',')
                                        .append(environment)
                                        .append(":FEIL=Teknisk feil se logg! ")
                                        .append(nonNull(response) ?
                                                ErrorStatusDecoder.encodeStatus(response.getFeilmelding()) :
                                                "UKJENT");
                            }
                        }
                    });

            progress.setDokarkivStatus(substring(status.toString(), 1));
        }
        return Flux.just();
    }

    @Override
    public void release(List<String> identer) {

        // Sletting er ikke støttet
    }

    @Override
    public boolean isDone(RsDollyBestilling kriterier, Bestilling bestilling) {

        return isNull(kriterier.getDokarkiv()) ||
                bestilling.getProgresser().stream()
                        .allMatch(entry -> isNotBlank(entry.getDokarkivStatus()));
    }

    private void saveTransaksjonId(DokarkivResponse response, String ident, Long bestillingId, String miljoe) {

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(ident)
                        .bestillingId(bestillingId)
                        .transaksjonId(toJson(JoarkTransaksjon.builder()
                                .journalpostId(response.getJournalpostId())
                                .dokumentInfoId(response.getDokumenter().get(0).getDokumentInfoId())
                                .build()))
                        .datoEndret(LocalDateTime.now())
                        .miljoe(miljoe)
                        .system(DOKARKIV.name())
                        .build());
    }

    private String toJson(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere transaksjonsId for dokarkiv", e);
        }
        return null;
    }
}
