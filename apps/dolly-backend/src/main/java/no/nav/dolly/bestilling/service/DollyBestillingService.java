package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.aareg.AaregClient;
import no.nav.dolly.bestilling.inntektstub.InntektstubClient;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.pdldata.dto.PdlResponse;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterClient;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.bestilling.tagshendelseslager.TagsHendelseslagerClient;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.MDC;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;
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

    protected final DollyPersonCache dollyPersonCache;
    protected final IdentService identService;
    protected final BestillingProgressService bestillingProgressService;
    protected final BestillingService bestillingService;
    protected final MapperFacade mapperFacade;
    protected final CacheManager cacheManager;
    protected final ObjectMapper objectMapper;
    protected final List<ClientRegister> clientRegisters;
    protected final CounterCustomRegistry counterCustomRegistry;
    protected final PdlPersonConsumer pdlPersonConsumer;
    protected final PdlDataConsumer pdlDataConsumer;
    protected final ErrorStatusDecoder errorStatusDecoder;
    protected final TransactionHelperService transactionHelperService;
    protected final PersonServiceClient personServiceClient;

    protected static Boolean isSyntetisk(String ident) {

        return Integer.parseInt(String.valueOf(ident.charAt(2))) >= 4;
    }

    public static List<String> getEnvironments(String miljoer) {

        return isNotBlank(miljoer) ? List.of(miljoer.split(",")) : emptyList();
    }

    public static String getBestillingType(Bestilling bestilling) {

        if (nonNull(bestilling.getOpprettetFraId())) {
            return "gjenopprett bestilling " + bestilling.getOpprettetFraId();

        } else if (nonNull(bestilling.getOpprettetFraGruppeId())) {
            return "gjenopprett gruppe " + bestilling.getOpprettetFraGruppeId();

        } else if (isNotBlank(bestilling.getPdlImport())) {
            return "testnorge import (" + bestilling.getPdlImport() + ") ";

        } else if (isNotBlank(bestilling.getGjenopprettetFraIdent())) {
            return "gjenopprett ident " + bestilling.getGjenopprettetFraIdent();

        } else if (isNotBlank(bestilling.getOpprettFraIdenter())) {
            return "opprett fra eksisterende identer (" + bestilling.getOpprettFraIdenter() + ") ";

        } else if (isNotBlank(bestilling.getIdent())) {
            return "legg-til-endre";

        } else {
            return "opprett fra kriterier";
        }
    }

    @Async
    public void oppdaterPersonAsync(RsDollyUpdateRequest request, Bestilling bestilling) {

        try {
            log.info("Bestilling med id=#{} med type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));
            MDC.put(MDC_KEY_BESTILLING, bestilling.getId().toString());

            var testident = identService.getTestIdent(bestilling.getIdent());
            var progress = new BestillingProgress(bestilling, bestilling.getIdent(), testident.getMaster());

            var originator = new OriginatorCommand(request, testident, mapperFacade).call();

            DollyPerson dollyPerson;
            if (originator.isPdlf()) {
                try {
                    if (nonNull(originator.getPdlBestilling())) {
                        pdlDataConsumer.oppdaterPdl(testident.getIdent(),
                                PersonUpdateRequestDTO.builder()
                                        .person(originator.getPdlBestilling().getPerson())
                                        .build());
                    }

                    var pdlfPersoner = pdlDataConsumer.getPersoner(List.of(testident.getIdent())).block();
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

            bestKriterier.setNavSyntetiskIdent(bestilling.getNavSyntetiskIdent());
            bestKriterier.setEnvironments(getEnvironments(bestilling.getMiljoer()));
            bestKriterier.setBeskrivelse(bestilling.getBeskrivelse());
            return bestKriterier;

        } catch (JsonProcessingException e) {
            log.error("Feilet å lese JSON {}", e.getMessage(), e);
            return null;
        }
    }

    public GjenopprettSteg fase1Klienter() {

        return TagsHendelseslagerClient.class::isInstance;
    }

    public GjenopprettSteg fase2Klienter() {

        var klienter = List.of(
                PensjonforvalterClient.class,
                AaregClient.class,
                InntektstubClient.class);

        return register -> !fase1Klienter().apply(register) &&
                klienter.stream()
                        .anyMatch(client -> client.isInstance(register));
    }

    public GjenopprettSteg fase3Klienter() {

        return register -> !fase1Klienter().apply(register) &&
                !fase2Klienter().apply(register);
    }

    protected Flux<BestillingProgress> gjenopprettKlienter(DollyPerson dollyPerson, RsDollyBestillingRequest bestKriterier,
                                                           GjenopprettSteg steg,
                                                           BestillingProgress progress, boolean isOpprettEndre) {

        counterCustomRegistry.invoke(bestKriterier);
        return Flux.from(Flux.fromIterable(clientRegisters)
                .filter(steg::apply)
                .flatMap(clientRegister ->
                        clientRegister.gjenopprett(bestKriterier, dollyPerson, progress, isOpprettEndre))
                .filter(Objects::nonNull)
                .map(ClientFuture::get));
    }

    protected Flux<List<BestillingProgress>> gjenopprettAlleKlienter(DollyPerson dollyPerson, RsDollyBestillingRequest bestKriterier,
                                                                     BestillingProgress progress, boolean isOpprettEndre) {

        counterCustomRegistry.invoke(bestKriterier);
        return Flux.from(Flux.fromIterable(clientRegisters)
                .flatMap(clientRegister ->
                        clientRegister.gjenopprett(bestKriterier, dollyPerson, progress, isOpprettEndre))
                .filter(Objects::nonNull)
                .map(ClientFuture::get)
                .collectList());
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
        if (progress.isPdlf()) {
            var pdlfPerson = pdlDataConsumer.getPersoner(List.of(progress.getIdent())).block();
            dollyPerson = dollyPersonCache.preparePdlfPerson(pdlfPerson.stream().findFirst().orElse(new FullPersonDTO()),
                    progress.getBestilling().getGruppe().getTags());

        } else if (progress.isPdl()) {
            var pdlPerson = objectMapper.readValue(pdlPersonConsumer.getPdlPerson(progress.getIdent()).toString(), PdlPerson.class);
            dollyPerson = dollyPersonCache.preparePdlPersoner(pdlPerson);
        }

        return nonNull(dollyPerson) ? Optional.of(dollyPerson) : Optional.empty();
    }

    protected Flux<DollyPerson> leggIdentTilGruppe(String ident, BestillingProgress progress, String beskrivelse) {

        identService.saveIdentTilGruppe(ident, progress.getBestilling().getGruppe(), progress.getMaster(), beskrivelse);
        log.info("Ident {} lagt til gruppe {}", ident, progress.getBestilling().getGruppe().getId());

        return Flux.just(DollyPerson.builder()
                .hovedperson(ident)
                .master(progress.getMaster())
                .tags(progress.getBestilling().getGruppe().getTags())
                .build());
    }

    protected void doFerdig(Bestilling bestilling) {

        transactionHelperService.oppdaterBestillingFerdig(bestilling);
        MDC.remove(MDC_KEY_BESTILLING);
        log.info("Bestilling med id=#{} er ferdig", bestilling.getId());
    }

    protected Flux<BestillingProgress> opprettProgress(Bestilling bestilling, Testident.Master master) {

        return opprettProgress(bestilling, master, null);
    }

    protected Flux<BestillingProgress> opprettProgress(Bestilling bestilling, Testident.Master master, String ident) {

        return Flux.just(transactionHelperService.oppdaterProgress(BestillingProgress.builder()
                .bestilling(bestilling)
                .ident(ident)
                .master(master)
                .build()));
    }

    protected Flux<PdlResponse> opprettPerson(OriginatorCommand.Originator originator) {

        return pdlDataConsumer.opprettPdl(originator.getPdlBestilling())
                .doOnNext(response -> log.info("Opprettet person med ident {} ", response));
    }

    protected Flux<String> sendOrdrePerson(BestillingProgress progress, PdlResponse response) {

        if (response.getStatus().is2xxSuccessful()) {

            progress.setIdent(Strings.isNotBlank(response.getIdent()) ? response.getIdent() : "?");
            return pdlDataConsumer.sendOrdre(response.getIdent(), false)
                    .map(resultat -> {
                        progress.setPdlDataStatus(resultat.getStatus().is2xxSuccessful() ?
                                resultat.getJsonNode() :
                                errorStatusDecoder.getErrorText(resultat.getStatus(), resultat.getFeilmelding()));
                        transactionHelperService.persister(progress);
                        log.info("Sendt ordre til PDL for ident {} ", response.getIdent());
                        return response.getIdent();
                    });

        } else {
            progress.setPdlDataStatus(errorStatusDecoder.getErrorText(response.getStatus(), response.getFeilmelding()));
            transactionHelperService.persister(progress);
            return Flux.empty();
        }
    }
}
