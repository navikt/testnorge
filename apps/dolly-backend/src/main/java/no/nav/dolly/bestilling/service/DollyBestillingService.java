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
import no.nav.dolly.bestilling.kontoregisterservice.KontoregisterClient;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.pdldata.dto.PdlResponse;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterClient;
import no.nav.dolly.bestilling.tagshendelseslager.TagsHendelseslagerClient;
import no.nav.dolly.bestilling.tpsmessagingservice.TpsMessagingClient;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testident;
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
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonUpdateRequestDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Set;
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
            RsDollyBestillingRequest bestKriterier = objectMapper.readValue(bestilling.getBestKriterier(), RsDollyBestillingRequest.class);

            bestKriterier.setId(bestilling.getId());
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
                KontoregisterClient.class,
                TpsMessagingClient.class,
                PensjonforvalterClient.class,
                AaregClient.class,
                InntektstubClient.class);

        return register -> klienter.stream()
                .anyMatch(client -> client.isInstance(register));
    }

    public GjenopprettSteg fase3Klienter() {

        return register -> !fase1Klienter().apply(register) &&
                !fase2Klienter().apply(register);
    }

    protected Flux<BestillingProgress> gjenopprettKlienter(DollyPerson dollyPerson, RsDollyUtvidetBestilling bestKriterier,
                                                           GjenopprettSteg steg,
                                                           BestillingProgress progress, boolean isOpprettEndre) {

        return Flux.fromIterable(clientRegisters)
                .filter(steg::apply)
                .flatMap(clientRegister ->
                        clientRegister.gjenopprett(bestKriterier, dollyPerson, progress, isOpprettEndre))
                .filter(Objects::nonNull)
                .flatMap(ClientFuture::get);
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

        return opprettDollyPerson(null, progress, bruker);
    }

    protected Mono<DollyPerson> opprettDollyPerson(String ident, BestillingProgress progress, Bruker bruker) {

        return bestillingRepository.findById(progress.getBestillingId())
                .flatMap(bestilling -> testgruppeRepository.findById(bestilling.getGruppeId()))
                .flatMap(testgruppe -> Mono.just(DollyPerson.builder()
                        .ident(isNotBlank(ident) ? ident : progress.getIdent())
                        .master(progress.getMaster())
                        .tags(Stream.concat(testgruppe.getTags().stream(),
                                        Stream.of(Tags.DOLLY)
                                                .filter(tag -> progress.getMaster() == PDL))
                                .toList())
                        .bruker(bruker)
                        .build()));
    }

    protected Mono<Bestilling> doFerdig(Bestilling bestilling) {

        return transactionHelperService.oppdaterBestillingFerdig(bestilling.getId(), bestillingService.cleanBestilling())
                .doFinally(signal -> log.info("Bestilling med id=#{} er ferdig", bestilling.getId()));
    }

    protected void clearCache() {

        transactionHelperService.clearCache();
    }

    protected Mono<BestillingProgress> saveFeil(BestillingProgress progress, String error) {

        return transactionHelperService.persister(progress, BestillingProgress::setFeil, error);
    }

    protected Mono<Void> saveBestillingToElasticServer(RsDollyBestilling bestillingRequest, Bestilling bestilling) {

        if (isBlank(bestilling.getFeil()) &&
                isNull(bestilling.getOpprettetFraId()) &&
                isBlank(bestilling.getGjenopprettetFraIdent()) &&
                isNull(bestilling.getOpprettetFraGruppeId())) {

            var request = mapperFacade.map(bestillingRequest, ElasticBestilling.class);
            request.setId(bestilling.getId());
            return bestillingProgressRepository.findByBestillingId(bestilling.getId())
                    .filter(BestillingProgress::isIdentGyldig)
                    .map(BestillingProgress::getIdent)
                    .collectList()
                    .map(identer -> {
                        request.setIdenter(identer);
                        return request;
                    })
                    .flatMap(bestillingElasticRepository::save)
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
                .flatMap(transactionHelperService::opprettProgress);
    }

    protected Flux<PdlResponse> opprettPerson(OriginatorUtility.Originator originator, BestillingProgress progress) {

        return transactionHelperService.persister(progress, BestillingProgress::setPdlForvalterStatus,
                        "Info: Oppretting av person startet ...")
                .flatMapMany(progress1 -> pdlDataConsumer.opprettPdl(originator.getPdlBestilling())
                        .doOnNext(response -> log.info("Opprettet person med ident ... {}", response)));
    }

    protected Mono<String> sendOrdrePerson(BestillingProgress progress, PdlResponse forvalterStatus) {

        return Mono.just("status")
                .flatMap(status -> {
                    if (progress.getMaster() == PDL) {

                        return transactionHelperService.persister(progress, BestillingProgress::setPdlImportStatus, "OK");
                    }

                    if (nonNull(forvalterStatus.getStatus())) {

                        return transactionHelperService.persister(progress, BestillingProgress::setPdlForvalterStatus,
                                        forvalterStatus.getStatus().is2xxSuccessful() ? "OK" :
                                                errorStatusDecoder.getErrorText(forvalterStatus.getStatus(), forvalterStatus.getFeilmelding()))
                                .flatMap(bestProgress -> transactionHelperService.persister(progress, BestillingProgress::setIdent,
                                        (forvalterStatus.getStatus().is2xxSuccessful() ?
                                                forvalterStatus.getIdent() : "?")));
                    }
                    return Mono.just(progress);
                })
                .flatMap(progress1 -> {

                    if (isNull(forvalterStatus.getStatus()) || forvalterStatus.getStatus().is2xxSuccessful()) {

                        return transactionHelperService.persister(progress, BestillingProgress::setPdlOrdreStatus,
                                        "Info: Ordre til PDL startet ...")
                                .then(pdlDataConsumer.sendOrdre(forvalterStatus.getIdent(), false)
                                        .flatMap(resultat -> Mono.just(resultat.getStatus().is2xxSuccessful() ?
                                                        resultat.getJsonNode() :
                                                        errorStatusDecoder.getErrorText(resultat.getStatus(), resultat.getFeilmelding()))
                                                .flatMap(status -> transactionHelperService.persister(progress1, BestillingProgress::setPdlOrdreStatus,
                                                        !resultat.isFinnesIkke() ? status : null))
                                                .doOnNext(progress2 -> log.info("Sendt ordre til PDL for ident {} ", forvalterStatus.getIdent()))
                                                .thenReturn(resultat)
                                        )
                                        .map(resultat -> resultat.getStatus().is2xxSuccessful() || resultat.isFinnesIkke()
                                                ? forvalterStatus.getIdent() : ""));
                    } else {
                        return Mono.just("");
                    }
                });
    }

    protected Flux<RsDollyBestillingRequest> createBestilling(Bestilling
                                                                      bestilling, IdentRepository.GruppeBestillingIdent coBestilling) {

        return Flux.just(getDollyBestillingRequest(
                Bestilling.builder()
                        .id(coBestilling.getBestillingId())
                        .bestKriterier(coBestilling.getBestkriterier())
                        .miljoer(StringUtils.isNotBlank(bestilling.getMiljoer()) ?
                                bestilling.getMiljoer() :
                                coBestilling.getMiljoer())
                        .build()));
    }

    protected Flux<RsDollyBestillingRequest> createBestilling(Bestilling bestilling, Long coBestillingId) {

        return bestillingRepository.findById(coBestillingId)
                .map(coBestilling -> getDollyBestillingRequest(
                        Bestilling.builder()
                                .id(coBestilling.getId())
                                .bestKriterier(coBestilling.getBestKriterier())
                                .miljoer(StringUtils.isNotBlank(bestilling.getMiljoer()) ?
                                        bestilling.getMiljoer() :
                                        coBestilling.getMiljoer())
                                .build())).flux()
    }

    protected Flux<PdlResponse> oppdaterPdlPerson(OriginatorUtility.Originator originator, BestillingProgress
            progress) {

        if (nonNull(originator.getPdlBestilling()) && nonNull(originator.getPdlBestilling().getPerson())) {

            transactionHelperService.persister(progress, BestillingProgress::setPdlForvalterStatus,
                    "Info: Oppdatering av person startet ...");
            return pdlDataConsumer.oppdaterPdl(originator.getIdent(),
                            PersonUpdateRequestDTO.builder()
                                    .person(originator.getPdlBestilling().getPerson())
                                    .build())
                    .doOnNext(response -> log.info("Oppdatert person til PDL-forvalter med response {}", response));

        } else {
            return Flux.just(PdlResponse.builder()
                    .ident(originator.getIdent())
                    .build());
        }
    }
}