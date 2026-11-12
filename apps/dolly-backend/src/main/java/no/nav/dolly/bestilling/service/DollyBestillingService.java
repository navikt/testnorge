package no.nav.dolly.bestilling.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.aareg.AaregClient;
import no.nav.dolly.bestilling.inntektstub.InntektstubClient;
import no.nav.dolly.bestilling.kontoregisterservice.KontoregisterClient;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.pdldata.dto.PdlResponse;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterClient;
import no.nav.dolly.bestilling.tagshendelseslager.TagsHendelseslagerClient;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.projection.GruppeBestillingIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.opensearch.BestillingDokument;
import no.nav.dolly.opensearch.service.OpenSearchService;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TransactionHelperService;
import no.nav.dolly.util.ClearCacheUtil;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.IdentRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.jpa.Testident.Master.PDL;
import static no.nav.dolly.domain.jpa.Testident.Master.PDLF;
import static no.nav.dolly.util.TestnorgeIdentUtility.isTestnorgeIdent;
import static org.apache.commons.lang3.ObjectUtils.getIfNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class DollyBestillingService {

    protected final BestillingProgressRepository bestillingProgressRepository;
    protected final BestillingRepository bestillingRepository;
    protected final BestillingService bestillingService;
    protected final CacheManager cacheManager;
    protected final CounterCustomRegistry counterCustomRegistry;
    protected final ErrorStatusDecoder errorStatusDecoder;
    protected final IdentService identService;
    protected final JsonMapper jsonMapper;
    protected final List<ClientRegister> clientRegisters;
    protected final MapperFacade mapperFacade;
    protected final OpenSearchService openSearchService;
    protected final PdlDataConsumer pdlDataConsumer;
    protected final TestgruppeRepository testgruppeRepository;
    protected final TransactionHelperService transactionHelperService;

    @Value("${dolly.client.oppretting.timeout}")
    protected Long opprettPersonTimeout;

    public static Set<String> getEnvironments(String miljoer) {
        return isNotBlank(miljoer) ? Set.of(miljoer.split(",")) : emptySet();
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

    private GjenopprettSteg fase1Klienter() {

        return TagsHendelseslagerClient.class::isInstance;
    }

    private GjenopprettSteg fase2Klienter() {

        var klienter = List.of(
                KontoregisterClient.class,
                PensjonforvalterClient.class,
                AaregClient.class,
                InntektstubClient.class);

        return register -> klienter.stream()
                .anyMatch(client -> client.isInstance(register));
    }

    private GjenopprettSteg fase3Klienter() {

        return register ->
                !fase1Klienter().apply(register) &&
                !fase2Klienter().apply(register);
    }

    private List<GjenopprettSteg> remainingFaser() {

        return List.of(fase2Klienter(),
                fase3Klienter());
    }

    private Mono<BestillingProgress> gjenopprettKlienter(DollyPerson dollyPerson, RsDollyUtvidetBestilling bestKriterier,
                                                         GjenopprettSteg steg,
                                                         BestillingProgress progress) {

        return Flux.fromIterable(clientRegisters)
                .filter(steg::apply)
                .flatMap(clientRegister ->
                        clientRegister.gjenopprett(bestKriterier, dollyPerson, progress))
                .onErrorResume(throwable -> {
                    var description = WebClientError.describe(throwable);
                    var error = errorStatusDecoder.getErrorText(description.getStatus(), description.getMessage());
                    log.error("Feil oppsto ved utføring av bestilling, progressId {} {}",
                            progress.getId(), error, throwable);
                    return transactionHelperService.persister(progress, BestillingProgress::setFeil, error);
                })
                .collectList()
                .then(Mono.just(progress));
    }

    private Mono<BestillingProgress> endrePerson(Supplier<Mono<PdlResponse>> operasjon, BestillingProgress bestillingProgress) {

        return transactionHelperService.persister(bestillingProgress, BestillingProgress::setPdlForvalterStatus,
                        "Info: Oppretting av person startet ...")
                .flatMap(_ -> operasjon.get())
                .flatMap(response -> {

                    String status;
                    if (nonNull(response.getStatus()) && response.getStatus().is2xxSuccessful()) {
                        status = "OK";
                    } else if (nonNull(response.getStatus())) {
                        status = errorStatusDecoder.getErrorText(response.getStatus(), response.getFeilmelding());
                    } else {
                        status = "Feil= Ingen respons fra PDL-forvalter";
                    }
                    return transactionHelperService.persister(bestillingProgress,
                                    BestillingProgress::setPdlForvalterStatus, status)
                            .thenReturn(response);
                })
                .flatMap(response -> transactionHelperService.persister(bestillingProgress,
                        BestillingProgress::setIdent, isNotBlank(response.getIdent()) ?
                                response.getIdent() : bestillingProgress.getIdent()));
    }

    protected Mono<RsDollyBestillingRequest> getDollyBestillingRequest(Bestilling bestilling) {

        try {
            var bestKriterier = jsonMapper.readValue(bestilling.getBestKriterier(), RsDollyBestillingRequest.class);

            bestKriterier.setId(bestilling.getId());
            bestKriterier.setNavSyntetiskIdent(bestilling.getNavSyntetiskIdent());
            bestKriterier.setEnvironments(getEnvironments(bestilling.getMiljoer()));
            bestKriterier.setBeskrivelse(bestilling.getBeskrivelse());

            return Mono.just(bestKriterier);

        } catch (JacksonException e) {
            log.error("Feilet å lese JSON {}", e.getMessage(), e);

            var bestKriterier = new RsDollyBestillingRequest();
            bestKriterier.setFeil("Feil: Kunne ikke mappe bestillingskriterier, se logg!");
            bestilling.setFeil(bestKriterier.getFeil());
            return Mono.just(bestKriterier);
        }
    }

    protected Mono<BestillingProgress> gjenopprettKlienterStart(DollyPerson dollyPerson, RsDollyUtvidetBestilling bestKriterier,
                                                                BestillingProgress progress) {

        return gjenopprettKlienter(dollyPerson, bestKriterier, fase1Klienter(), progress)
                .then(Mono.just(progress));
    }

    protected Mono<BestillingProgress> gjenopprettKlienterFerdigstill(DollyPerson dollyPerson, RsDollyUtvidetBestilling bestKriterier,
                                                                      BestillingProgress progress) {

        return Flux.fromIterable(remainingFaser())
                .concatMap(steg -> gjenopprettKlienter(dollyPerson, bestKriterier, steg, progress))
                .then(Mono.just(progress));
    }

    protected Mono<BestillingProgress> leggIdentTilGruppe(BestillingProgress progress, String beskrivelse) {

        return bestillingRepository.findById(progress.getBestillingId())
                .flatMap(bestilling -> identService.saveIdentTilGruppe(progress.getIdent(),
                                bestilling.getGruppeId(), progress.getMaster(), beskrivelse)
                        .doOnNext(testident -> log.info("Ident {} lagt til gruppe {}", testident.getIdent(), bestilling.getGruppeId())))
                .thenReturn(progress);
    }

    protected Mono<DollyPerson> opprettDollyPersonLeggTil(BestillingProgress progress, RsDollyUtvidetBestilling bestkriterier) {

        return opprettDollyPerson(progress, bestkriterier)
                .doOnNext(dollyPerson -> dollyPerson.setLeggTil(true));
    }

    protected Mono<DollyPerson> opprettDollyPersonNyoppretting(BestillingProgress progress, RsDollyUtvidetBestilling bestkriterier) {

        return opprettDollyPerson(progress, bestkriterier)
                .doOnNext(dollyPerson -> dollyPerson.setOpprett(true));
    }

    protected Mono<DollyPerson> opprettDollyPersonForGjenopprett(BestillingProgress progress) {

        return opprettDollyPerson(progress, null);
    }

    private Mono<DollyPerson> opprettDollyPerson(BestillingProgress progress, RsDollyUtvidetBestilling bestkriterier) {

        var oppdatertIdent = progress.getOppdatertIdent();

        List<String> eksisterendeIdenter = nonNull(bestkriterier) &&
                                           nonNull(bestkriterier.getPdldata()) &&
                                           nonNull(bestkriterier.getPdldata().getPerson()) ?
                bestkriterier.getPdldata().getPerson().getNyident().stream()
                        .map(IdentRequestDTO::getEksisterendeIdent)
                        .filter(Objects::nonNull)
                        .toList() : emptyList();

        return Mono.defer(() -> {
                    if ("OK".equals(progress.getPdlForvalterStatus()) && !eksisterendeIdenter.isEmpty()) {
                        return Flux.fromIterable(eksisterendeIdenter)
                                .concatMap(nyIdent -> bestillingProgressRepository.swapIdent(oppdatertIdent, nyIdent)
                                        .then(bestillingService.swapIdent(oppdatertIdent, nyIdent))
                                        .then(identService.deleteIdent(oppdatertIdent)))
                                .then();

                    } else if ("OK".equals(progress.getPdlForvalterStatus()) &&
                               isNotBlank(oppdatertIdent) &&
                               !oppdatertIdent.equals(progress.getIdent())) {
                        return identService.swapIdent(oppdatertIdent, progress.getIdent())
                                .then(bestillingProgressRepository.swapIdent(oppdatertIdent, progress.getIdent()))
                                .then(bestillingService.swapIdent(oppdatertIdent, progress.getIdent()));

                    } else {
                        return Mono.empty();
                    }
                })
                .then(bestillingRepository.findById(progress.getBestillingId())
                            .flatMap(bestilling -> testgruppeRepository.findById(bestilling.getGruppeId()))
                            .map(testgruppe -> DollyPerson.builder()
                                    .ident(progress.getIdent())
                                    .master(progress.getMaster())
                                    .tags(Stream.concat(testgruppe.getTags().stream(),
                                                    Stream.of(Tags.DOLLY)
                                                            .filter(_ -> progress.isPdl()))
                                            .toList())
                                    .build()));
    }

    protected Mono<Bestilling> doFerdig(Bestilling bestilling) {

        return transactionHelperService.oppdaterBestillingFerdig(bestilling.getId(), bestilling.getFeil())
                .doOnNext(bestilling1 -> log.info("Bestilling med id=#{} er ferdig", bestilling1.getId()))
                .doOnNext(ignore -> new ClearCacheUtil(cacheManager).run());
    }

    protected Mono<Void> saveBestillingToOpenSearchServer(RsDollyBestilling bestillingRequest, Bestilling
            bestilling) {

        if (isBlank(bestilling.getFeil()) &&
            isNull(bestilling.getOpprettetFraId()) &&
            isBlank(bestilling.getGjenopprettetFraIdent()) &&
            isNull(bestilling.getOpprettetFraGruppeId())) {

            var request = mapperFacade.map(bestillingRequest, BestillingDokument.class);
            request.setId(bestilling.getId());
            return bestillingProgressRepository.findAllByBestillingId(bestilling.getId())
                    .filter(BestillingProgress::isIdentGyldig)
                    .map(BestillingProgress::getIdent)
                    .collectList()
                    .map(identer -> {
                        request.setIdenter(identer);
                        return request;
                    })
                    .flatMap(_ -> openSearchService.save(request, bestilling.getBruker()))
                    .then();
        } else {
            return Mono.empty();
        }
    }

    protected Mono<BestillingProgress> opprettProgress(Long bestillingId, Testident.Master master) {

        return bestillingProgressRepository.save(BestillingProgress.builder()
                .bestillingId(bestillingId)
                .master(master)
                .pdlImportStatus(PDL.equals(master) ? "OK" : null)
                .build());
    }

    protected Mono<BestillingProgress> opprettProgress(Long bestillingId, String ident) {

        Testident.Master master;
        if (isBlank(ident)) {
            master = null;
        } else {
            master = isTestnorgeIdent(ident) ? PDL : PDLF;
        }

        return bestillingProgressRepository.save(BestillingProgress.builder()
                .bestillingId(bestillingId)
                .ident(ident)
                .master(master)
                .pdlImportStatus(isNotBlank(ident) && isTestnorgeIdent(ident) ? "OK" : null)
                .build());
    }

    protected Mono<BestillingProgress> opprettPersonFraIdent(RsDollyUtvidetBestilling request, BestillingProgress progress) {

        return Mono.just(PdlRequestBuilderUtility.prepRequest(request, progress.getIdent(), mapperFacade))
                .flatMap(pdlRequest -> opprettPerson(pdlRequest, progress));
    }

    protected Mono<BestillingProgress> opprettPerson(RsDollyUtvidetBestilling request, BestillingProgress progress) {

        return Mono.just(PdlRequestBuilderUtility.prepRequest(request, mapperFacade))
                .flatMap(pdlRequest -> opprettPerson(pdlRequest, progress));
    }

    private Mono<BestillingProgress> opprettPerson(BestillingRequestDTO pdlRequest, BestillingProgress progress) {

           return endrePerson(() -> pdlDataConsumer.opprettPdl(pdlRequest), progress)
                .doOnNext(response -> log.info("Opprettet person med ident ... {}", response.getIdent()));
    }

    protected Mono<BestillingProgress> oppdaterPerson(RsDollyUtvidetBestilling request, BestillingProgress
            progress, String ident) {

        return Mono.just(PdlRequestBuilderUtility.prepRequest(request, ident, mapperFacade))
                .flatMap(pdlRequest -> {
                    if (nonNull(pdlRequest.getPerson())) {

                        return endrePerson(() -> pdlDataConsumer.oppdaterPdl(ident,
                                PersonUpdateRequestDTO.builder()
                                        .person(pdlRequest.getPerson())
                                        .build()), progress)
                                .doOnNext(response -> response.setOppdatertIdent(ident))
                                .doOnNext(response -> log.info("Oppdatert person til PDL-forvalter med response {}", response));

                    } else {
                        return Mono.just(progress);
                    }
                });
    }

    protected Mono<BestillingProgress> sendOrdrePerson(BestillingProgress bestillingProgress) {

        var oppdatertIdent = bestillingProgress.getOppdatertIdent();
        return Mono.just(bestillingProgress)
                .flatMap(progress -> {

                    if ("OK".equals(progress.getPdlForvalterStatus()) ||
                        isBlank(progress.getPdlForvalterStatus()) && isNotBlank(progress.getIdent())) {

                        return transactionHelperService.persister(progress, BestillingProgress::setPdlOrdreStatus,
                                        "Info: Ordre til PDL startet ...")
                                .flatMap(progress2 -> pdlDataConsumer.sendOrdre(progress.getIdent(), false)
                                        .flatMap(resultat -> {
                                            if (resultat.getStatus().is2xxSuccessful()) {
                                                return transactionHelperService.persister(progress2, BestillingProgress::setPdlOrdreStatus,
                                                        resultat.getJsonNode());
                                            } else {
                                                return transactionHelperService.persister(progress2, BestillingProgress::setPdlOrdreStatus,
                                                        resultat.isFinnesIkke() ? null :
                                                                errorStatusDecoder.getErrorText(resultat.getStatus(), resultat.getFeilmelding()));
                                            }
                                        }))
                                .doOnNext(progress2 -> progress2.setOppdatertIdent(oppdatertIdent))
                                .doOnNext(progress2 -> log.info("Sendt ordre til PDL for ident {} ", progress2.getIdent()));

                    } else {
                        return Mono.just(progress);
                    }
                });
    }

    protected Mono<RsDollyBestillingRequest> createBestilling(Bestilling bestilling, GruppeBestillingIdent
            coBestilling) {

        return getDollyBestillingRequest(
                Bestilling.builder()
                        .id(coBestilling.getId())
                        .bestKriterier(coBestilling.getBestkriterier())
                        .miljoer(isNotBlank(bestilling.getMiljoer()) ?
                                bestilling.getMiljoer() :
                                coBestilling.getMiljoer())
                        .build());
    }

    protected Mono<BestillingProgress> setOpprettIdentFeilet(Long id, BestillingProgress progress, String
            ident, Throwable error) {

        var errText = getIfNull(error.getMessage(), error.getClass().getSimpleName());
        log.error("Feil ved oppretting av ident: {} {}", ident, errText, error);
        var feilmelding = "Feil: " + errText;

        return nonNull(progress) ?
                transactionHelperService.persister(progress, BestillingProgress::setFeil, feilmelding)
                        .switchIfEmpty(Mono.fromSupplier(() -> {
                            log.warn("Fant ikke progress for ident {} ved lagring av feil: {}", ident, feilmelding);
                            return progress;
                        })) :
                bestillingProgressRepository.save(BestillingProgress.builder()
                        .bestillingId(id)
                        .ident(ident)
                        .feil(feilmelding)
                        .build());
    }
}