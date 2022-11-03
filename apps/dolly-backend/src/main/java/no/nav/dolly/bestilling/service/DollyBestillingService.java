package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.RsOppdaterPersonResponse;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.dolly.service.IdentService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;
import static no.nav.dolly.domain.jpa.Testident.Master.PDLF;
import static no.nav.dolly.util.MdcUtil.MDC_KEY_BESTILLING;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class DollyBestillingService {
    protected static final String SUCCESS = "OK";
    private static final String FEIL_KUNNE_IKKE_UTFORES = "FEIL: Bestilling kunne ikke utføres: %s";

    private final TpsfService tpsfService;
    private final DollyPersonCache dollyPersonCache;
    private final IdentService identService;
    private final BestillingProgressService bestillingProgressService;
    private final BestillingService bestillingService;
    private final MapperFacade mapperFacade;
    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper;
    private final List<ClientRegister> clientRegisters;
    private final CounterCustomRegistry counterCustomRegistry;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final PdlDataConsumer pdlDataConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Async
    public void oppdaterPersonAsync(RsDollyUpdateRequest request, Bestilling bestilling) {

        try {
            log.info("Bestilling med id=#{} med type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));
            MDC.put(MDC_KEY_BESTILLING, bestilling.getId().toString());

            var testident = identService.getTestIdent(bestilling.getIdent());
            var progress = new BestillingProgress(bestilling, bestilling.getIdent(), testident.getMaster());

            var originator = new OriginatorCommand(request, testident, mapperFacade).call();

            DollyPerson dollyPerson;
            if (originator.isTpsf()) {
                var oppdaterPersonResponse = tpsfService.endreLeggTilPaaPerson(bestilling.getIdent(), originator.getTpsfBestilling());

                dollyPerson = dollyPersonCache.prepareTpsPerson(oppdaterPersonResponse.getIdentTupler().stream()
                        .map(RsOppdaterPersonResponse.IdentTuple::getIdent)
                        .findFirst().orElseThrow(() -> new NotFoundException("Ident ikke funnet i TPS: " + testident.getIdent())),
                        progress.getBestilling().getGruppe().getTags());

            } else if (originator.isPdlf()) {
                try {
                    if (nonNull(originator.getPdlBestilling())) {
                        pdlDataConsumer.oppdaterPdl(testident.getIdent(),
                                PersonUpdateRequestDTO.builder()
                                        .person(originator.getPdlBestilling().getPerson())
                                        .build());
                    }

                    var pdlfPersoner = pdlDataConsumer.getPersoner(List.of(testident.getIdent()));
                    dollyPerson = dollyPersonCache.preparePdlfPerson(pdlfPersoner.stream().findFirst().orElse(new FullPersonDTO()),
                            progress.getBestilling().getGruppe().getTags());

                } catch (WebClientResponseException e) {

                    dollyPerson = DollyPerson.builder().hovedperson(bestilling.getIdent()).master(PDLF).build();
                    progress.setPdlDataStatus(errorStatusDecoder.decodeThrowable(e));
                }

            } else {
                PdlPerson pdlPerson = objectMapper.readValue(pdlPersonConsumer.getPdlPerson(progress.getIdent()).toString(), PdlPerson.class);
                dollyPerson = dollyPersonCache.preparePdlPersoner(pdlPerson);
            }

            if ((originator.isPdlf() || originator.isTpsf()) && !bestilling.getIdent().equals(dollyPerson.getHovedperson())) {
                progress.setIdent(dollyPerson.getHovedperson());
                identService.swapIdent(bestilling.getIdent(), dollyPerson.getHovedperson());
                bestillingProgressService.swapIdent(bestilling.getIdent(), dollyPerson.getHovedperson());
                bestillingService.swapIdent(bestilling.getIdent(), dollyPerson.getHovedperson());
            }

            counterCustomRegistry.invoke(request);
            DollyPerson finalDollyPerson = dollyPerson;
            clientRegisters.forEach(clientRegister ->
                    clientRegister.gjenopprett(request, finalDollyPerson, progress, true));

            oppdaterProgress(bestilling, progress);

        } catch (Exception e) {
            log.error("Bestilling med id={} til ident={} ble avsluttet med feil={}", bestilling.getId(), bestilling.getIdent(), e.getMessage(), e);
            bestilling.setFeil(format(FEIL_KUNNE_IKKE_UTFORES, e.getMessage()));

        } finally {
            oppdaterBestillingFerdig(bestilling);
            MDC.remove(MDC_KEY_BESTILLING);
            log.info("Bestilling med id=#{} er ferdig", bestilling.getId());
        }
    }

    protected static Boolean isSyntetisk(String ident) {

        return Integer.parseInt(String.valueOf(ident.charAt(2))) >= 4;
    }

    protected void clearCache() {
        if (nonNull(cacheManager.getCache(CACHE_BESTILLING))) {
            requireNonNull(cacheManager.getCache(CACHE_BESTILLING)).clear();
        }
        if (nonNull(cacheManager.getCache(CACHE_GRUPPE))) {
            requireNonNull(cacheManager.getCache(CACHE_GRUPPE)).clear();
        }
    }

    protected RsDollyBestillingRequest getDollyBestillingRequest(Bestilling bestilling) {

        try {
            RsDollyBestillingRequest bestKriterier = objectMapper.readValue(bestilling.getBestKriterier(), RsDollyBestillingRequest.class);
            if (nonNull(bestilling.getTpsfKriterier())) {
                bestKriterier.setTpsf(objectMapper.readValue(bestilling.getTpsfKriterier(), RsTpsfUtvidetBestilling.class));
            }
            bestKriterier.setNavSyntetiskIdent(bestilling.getNavSyntetiskIdent());
            bestKriterier.setEnvironments(getEnvironments(bestilling.getMiljoer()));
            bestKriterier.setBeskrivelse(bestilling.getBeskrivelse());
            return bestKriterier;

        } catch (JsonProcessingException e) {
            log.error("Feilet å lese JSON {}", e.getMessage(), e);
            return null;
        }
    }

    protected void gjenopprettNonTpsf(DollyPerson dollyPerson, RsDollyBestillingRequest bestKriterier,
                                      BestillingProgress progress, boolean isOpprettEndre) {

        counterCustomRegistry.invoke(bestKriterier);
        clientRegisters.stream()
                .forEach(clientRegister ->
                        clientRegister.gjenopprett(bestKriterier, dollyPerson, progress, isOpprettEndre));
    }

    protected void oppdaterBestillingFerdig(Bestilling bestilling) {
        if (bestillingService.isStoppet(bestilling.getId())) {
            bestilling.setStoppet(true);
        }
        bestilling.setFerdig(true);
        bestilling.setSistOppdatert(now());
        bestillingService.saveBestillingToDB(bestilling);
        clearCache();
    }

    protected void oppdaterProgress(Bestilling bestilling, BestillingProgress progress) {
        bestillingProgressService.save(progress);
        bestilling.setSistOppdatert(now());
        bestillingService.saveBestillingToDB(bestilling);
        clearCache();
    }

    protected Optional<DollyPerson> prepareDollyPerson(BestillingProgress progress) throws JsonProcessingException {

        DollyPerson dollyPerson = null;
        if (progress.isTpsf()) {
            var personer = tpsfService.hentTestpersoner(List.of(progress.getIdent()));
            if (!personer.isEmpty()) {
                dollyPerson = dollyPersonCache.prepareTpsPersoner(personer.get(0),
                        progress.getBestilling().getGruppe().getTags());
            }

        } else if (progress.isPdlf()) {
            var pdlfPerson = pdlDataConsumer.getPersoner(List.of(progress.getIdent()));
            dollyPerson = dollyPersonCache.preparePdlfPerson(pdlfPerson.stream().findFirst().orElse(new FullPersonDTO()),
                    progress.getBestilling().getGruppe().getTags());

        } else if (progress.isPdl()) {
            var pdlPerson = objectMapper.readValue(pdlPersonConsumer.getPdlPerson(progress.getIdent()).toString(), PdlPerson.class);
            dollyPerson = dollyPersonCache.preparePdlPersoner(pdlPerson);
        }

        return nonNull(dollyPerson) ? Optional.of(dollyPerson) : Optional.empty();
    }

    public static List<String> getEnvironments(String miljoer){

        return isNotBlank(miljoer) ? List.of(miljoer.split(",")) : emptyList();
    }

    public static String getBestillingType(Bestilling bestilling) {

        if (bestilling.getOpprettetFraId() != null) {
            return "gjenopprett fra bestilling " + bestilling.getOpprettetFraId();
        }
        if (bestilling.getOpprettetFraGruppeId() != null) {
            return "gjenopprett fra gruppe " + bestilling.getOpprettetFraGruppeId();
        }
        if (StringUtils.isNotBlank(bestilling.getPdlImport())) {
            return "testnorge import (" + bestilling.getPdlImport() + ") ";
        }
        if (StringUtils.isNotBlank(bestilling.getGjenopprettetFraIdent())) {
            return "opprett fra ident " + bestilling.getGjenopprettetFraIdent();
        }
        if (StringUtils.isNotBlank(bestilling.getOpprettFraIdenter())) {
            return "opprett fra eksisterende identer (" + bestilling.getOpprettFraIdenter() + ") ";
        }

        return "ny bestilling";
    }
}
