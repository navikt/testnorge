package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.aareg.AaregClient;
import no.nav.dolly.bestilling.arenaforvalter.ArenaForvalterClient;
import no.nav.dolly.bestilling.inntektstub.InntektstubClient;
import no.nav.dolly.bestilling.kontoregisterservice.KontoregisterClient;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.pdldata.dto.PdlResponse;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterClient;
import no.nav.dolly.bestilling.tagshendelseslager.TagsHendelseslagerClient;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.projection.GruppeBestillingIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.elastic.ElasticBestilling;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TransactionHelperService;
import no.nav.dolly.util.ClearCacheUtil;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonUpdateRequestDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.jpa.Testident.Master.PDL;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class DollyBestillingService {

    protected final BestillingElasticRepository bestillingElasticRepository;
    protected final BestillingProgressRepository bestillingProgressRepository;
    protected final BestillingRepository bestillingRepository;
    protected final BestillingService bestillingService;
    protected final CounterCustomRegistry counterCustomRegistry;
    protected final ErrorStatusDecoder errorStatusDecoder;
    protected final IdentService identService;
    protected final List<ClientRegister> clientRegisters;
    protected final MapperFacade mapperFacade;
    protected final ObjectMapper objectMapper;
    protected final PdlDataConsumer pdlDataConsumer;
    protected final TestgruppeRepository testgruppeRepository;
    protected final TransactionHelperService transactionHelperService;
    protected final CacheManager cacheManager;

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

    protected RsDollyBestillingRequest getDollyBestillingRequest(Bestilling bestilling) {

        try {
            var bestKriterier = objectMapper.readValue(bestilling.getBestKriterier(), RsDollyBestillingRequest.class);

            bestKriterier.setId(bestilling.getId());
            bestKriterier.setNavSyntetiskIdent(bestilling.getNavSyntetiskIdent());
            bestKriterier.setEnvironments(getEnvironments(bestilling.getMiljoer()));
            bestKriterier.setBeskrivelse(bestilling.getBeskrivelse());

            return bestKriterier;

        } catch (JsonProcessingException e) {
            log.error("Feilet å lese JSON {}", e.getMessage(), e);

            var bestKriterier = new RsDollyBestillingRequest();
            bestKriterier.setFeil("Feil: Kunne ikke mappe bestillingskriterier, se logg!");
            bestilling.setFeil(bestKriterier.getFeil());
            return bestKriterier;
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

        return ArenaForvalterClient.class::isInstance;
    }

    private GjenopprettSteg fase4Klienter() {

        return register ->
                !fase1Klienter().apply(register) &&
                        !fase2Klienter().apply(register) &&
                        !fase3Klienter().apply(register);
    }

    private List<GjenopprettSteg> remainingFaser() {

        return List.of(fase2Klienter(),
                fase3Klienter(),
                fase4Klienter());
    }

    protected Mono<BestillingProgress> gjenopprettKlienterStart(DollyPerson dollyPerson, RsDollyUtvidetBestilling bestKriterier,
                                                                BestillingProgress progress, boolean isOpprettEndre) {

        return gjenopprettKlienter(dollyPerson, bestKriterier, fase1Klienter(), progress, isOpprettEndre)
                .then(Mono.just(progress));
    }

    protected Mono<BestillingProgress> gjenopprettKlienterFerdigstill(DollyPerson dollyPerson, RsDollyUtvidetBestilling bestKriterier,
                                                                      BestillingProgress progress, boolean isOpprettEndre) {

        return Flux.fromIterable(remainingFaser())
                .concatMap(steg -> gjenopprettKlienter(dollyPerson, bestKriterier, steg, progress, isOpprettEndre))
                .then(Mono.just(progress));
    }

    private Mono<BestillingProgress> gjenopprettKlienter(DollyPerson dollyPerson, RsDollyUtvidetBestilling bestKriterier,
                                                         GjenopprettSteg steg,
                                                         BestillingProgress progress, boolean isOpprettEndre) {

        return Flux.fromIterable(clientRegisters)
                .filter(steg::apply)
                .flatMap(clientRegister ->
                        clientRegister.gjenopprett(bestKriterier, dollyPerson, progress, isOpprettEndre))
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

    protected Mono<Testident> leggIdentTilGruppe(BestillingProgress progress, String beskrivelse) {

        return leggIdentTilGruppe(null, progress, beskrivelse);
    }

    protected Mono<Testident> leggIdentTilGruppe(String ident, BestillingProgress progress, String beskrivelse) {

        return bestillingRepository.findById(progress.getBestillingId())
                .flatMap(bestilling -> identService.saveIdentTilGruppe(isNotBlank(ident) ? ident : progress.getIdent(),
                                bestilling.getGruppeId(), progress.getMaster(), beskrivelse)
                        .doOnNext(testident -> log.info("Ident {} lagt til gruppe {}", testident.getIdent(), bestilling.getGruppeId())));
    }

    protected Mono<DollyPerson> opprettDollyPerson(BestillingProgress progress, Bruker bruker) {

        return bestillingRepository.findById(progress.getBestillingId())
                .flatMap(bestilling -> testgruppeRepository.findById(bestilling.getGruppeId()))
                .flatMap(testgruppe -> Mono.just(DollyPerson.builder()
                        .ident(progress.getIdent())
                        .master(progress.getMaster())
                        .tags(Stream.concat(testgruppe.getTags().stream(),
                                        Stream.of(Tags.DOLLY)
                                                .filter(tag -> progress.getMaster() == PDL))
                                .toList())
                        .bruker(bruker)
                        .build()));
    }

    protected Mono<Bestilling> doFerdig(Bestilling bestilling) {

        return transactionHelperService.oppdaterBestillingFerdig(bestilling.getId(), bestilling.getFeil())
                .doOnNext(bestilling1 -> log.info("Bestilling med id=#{} er ferdig", bestilling1.getId()))
                .doOnNext(ignore -> new ClearCacheUtil(cacheManager).run());
    }

    protected Mono<Void> saveBestillingToElasticServer(RsDollyBestilling bestillingRequest, Bestilling bestilling) {

        if (isBlank(bestilling.getFeil()) &&
                isNull(bestilling.getOpprettetFraId()) &&
                isBlank(bestilling.getGjenopprettetFraIdent()) &&
                isNull(bestilling.getOpprettetFraGruppeId())) {

            var request = mapperFacade.map(bestillingRequest, ElasticBestilling.class);
            request.setId(bestilling.getId());
            return bestillingProgressRepository.findAllByBestillingId(bestilling.getId())
                    .filter(BestillingProgress::isIdentGyldig)
                    .map(BestillingProgress::getIdent)
                    .collectList()
                    .map(identer -> {
                        request.setIdenter(identer);
                        return request;
                    })
                    .doOnNext(bestillingElasticRepository::save)
                    .then();
        } else {
            return Mono.empty();
        }
    }

    protected Mono<BestillingProgress> opprettProgress(Bestilling bestilling, Testident.Master master) {

        return opprettProgress(bestilling, master, null);
    }

    protected Mono<BestillingProgress> opprettProgress(Bestilling bestilling, Testident.Master master, String ident) {

        return Mono.just(BestillingProgress.builder()
                        .bestillingId(bestilling.getId())
                        .ident(ident)
                        .master(master)
                        .build())
                .flatMap(bestillingProgressRepository::save);
    }

    protected Mono<BestillingProgress> opprettPerson(OriginatorUtility.Originator originator, BestillingProgress bestillingProgress) {

        bestillingProgress.setPdlForvalterStatus("Info: Oppretting av person startet ...");
        return endrePerson(() -> pdlDataConsumer.opprettPdl(originator.getPdlBestilling()), bestillingProgress)
                .doOnNext(response -> log.info("Opprettet person med ident ... {}", response));
    }

    protected Mono<BestillingProgress> oppdaterPerson(OriginatorUtility.Originator originator, BestillingProgress progress) {

        if (nonNull(originator.getPdlBestilling()) && nonNull(originator.getPdlBestilling().getPerson())) {

            progress.setPdlForvalterStatus("Info: Oppdatering av person startet ...");
            return endrePerson(() -> pdlDataConsumer.oppdaterPdl(originator.getIdent(),
                                    PersonUpdateRequestDTO.builder()
                                            .person(originator.getPdlBestilling().getPerson())
                                            .build()), progress)
                            .doOnNext(response -> log.info("Oppdatert person til PDL-forvalter med response {}", response));

        } else {
            return Mono.just(progress);
        }
    }

    private Mono<BestillingProgress> endrePerson(Supplier<Mono<PdlResponse>> operasjon, BestillingProgress bestillingProgress) {

        return transactionHelperService.persister(bestillingProgress, BestillingProgress::setPdlForvalterStatus,
                        bestillingProgress.getPdlForvalterStatus())
                .flatMap(progress1 -> operasjon.get())
                .map(response -> {

                    String status;
                    if (nonNull(response.getStatus()) && response.getStatus().is2xxSuccessful()) {
                        status = "OK";
                    } else if (nonNull(response.getStatus())) {
                        status = errorStatusDecoder.getErrorText(response.getStatus(), response.getFeilmelding());
                    } else {
                        status = "Feil= Ingen respons fra PDL-forvalter";
                    }

                    bestillingProgress.setPdlForvalterStatus(status);
                    bestillingProgress.setIdent(response.getIdent());
                    return status;
                })
                .flatMap(status -> transactionHelperService.persister(bestillingProgress,
                                BestillingProgress::setPdlForvalterStatus, status)
                        .thenReturn(bestillingProgress));
    }

    protected Mono<BestillingProgress> sendOrdrePerson(BestillingProgress bestillingProgress) {

        return Mono.just(bestillingProgress)
                .flatMap(progress -> progress.getMaster() == PDL ?
                        transactionHelperService.persister(progress, BestillingProgress::setPdlImportStatus, "OK") :
                        Mono.just(progress))
                .flatMap(progress -> isNotBlank(progress.getIdent()) ?
                        transactionHelperService.persister(progress, BestillingProgress::setIdent, progress.getIdent()) :
                        Mono.just(progress))
                .flatMap(progress -> {

                    if ("OK".equals(progress.getPdlForvalterStatus()) ||
                            isBlank(progress.getPdlForvalterStatus()) && isNotBlank(progress.getIdent())) {

                        return transactionHelperService.persister(progress, BestillingProgress::setPdlOrdreStatus,
                                        "Info: Ordre til PDL startet ...")
                                .then(pdlDataConsumer.sendOrdre(progress.getIdent(), false)
                                        .flatMap(resultat -> Mono.just(resultat.getStatus().is2xxSuccessful() ?
                                                        resultat.getJsonNode() :
                                                        errorStatusDecoder.getErrorText(resultat.getStatus(), resultat.getFeilmelding()))
                                                .flatMap(status -> transactionHelperService.persister(progress, BestillingProgress::setPdlOrdreStatus,
                                                        !resultat.isFinnesIkke() ? status : null))
                                                .doOnNext(progress2 -> log.info("Sendt ordre til PDL for ident {} ", progress2.getIdent()))
                                                .thenReturn(resultat)
                                        )
                                        .map(resultat -> {
                                            if (!resultat.getStatus().is2xxSuccessful() && !resultat.isFinnesIkke()) {
                                                progress.setIdent("");
                                            }
                                            return progress;
                                        }));
                    } else {
                        return Mono.just(progress);
                    }
                });
    }

    protected RsDollyBestillingRequest createBestilling(Bestilling bestilling, GruppeBestillingIdent coBestilling) {

        return getDollyBestillingRequest(
                Bestilling.builder()
                        .id(coBestilling.getId())
                        .bestKriterier(coBestilling.getBestkriterier())
                        .miljoer(StringUtils.isNotBlank(bestilling.getMiljoer()) ?
                                bestilling.getMiljoer() :
                                coBestilling.getMiljoer())
                        .build());
    }


    protected Mono<String> updateIdent(DollyPerson dollyPerson, BestillingProgress progress) {

        return transactionHelperService.persister(progress, BestillingProgress::setIdent, dollyPerson.getIdent())
                .then(bestillingRepository.findById(progress.getBestillingId()))
                .map(Bestilling::getIdent)
                .flatMap(gammelIdent -> identService.swapIdent(gammelIdent, dollyPerson.getIdent())
                        .then(bestillingProgressRepository.swapIdent(gammelIdent, dollyPerson.getIdent()))
                        .then(bestillingService.swapIdent(gammelIdent, dollyPerson.getIdent())))
                .thenReturn(dollyPerson.getIdent());
    }
}