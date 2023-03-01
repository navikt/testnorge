package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.aareg.AaregClient;
import no.nav.dolly.bestilling.inntektstub.InntektstubClient;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.pdldata.dto.PdlResponse;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterClient;
import no.nav.dolly.bestilling.tagshendelseslager.TagsHendelseslagerClient;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.TransactionHelperService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.jpa.Testident.Master.PDL;
import static no.nav.dolly.domain.jpa.Testident.Master.PDLF;
import static no.nav.dolly.util.MdcUtil.MDC_KEY_BESTILLING;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class DollyBestillingService {

    protected final IdentService identService;
    protected final BestillingService bestillingService;
    protected final ObjectMapper objectMapper;
    protected final List<ClientRegister> clientRegisters;
    protected final CounterCustomRegistry counterCustomRegistry;
    protected final PdlDataConsumer pdlDataConsumer;
    protected final ErrorStatusDecoder errorStatusDecoder;
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

    protected Flux<BestillingProgress> gjenopprettKlienter(DollyPerson dollyPerson, RsDollyUtvidetBestilling bestKriterier,
                                                           GjenopprettSteg steg,
                                                           BestillingProgress progress, boolean isOpprettEndre) {

        return Flux.from(Flux.fromIterable(clientRegisters)
                .parallel()
                .filter(steg::apply)
                .flatMap(clientRegister ->
                        clientRegister.gjenopprett(bestKriterier, dollyPerson, progress, isOpprettEndre))
                .filter(Objects::nonNull)
                .map(ClientFuture::get));
    }

    protected void leggIdentTilGruppe(String ident, BestillingProgress progress, String beskrivelse) {

        identService.saveIdentTilGruppe(ident, progress.getBestilling().getGruppe(), progress.getMaster(), beskrivelse);
        log.info("Ident {} lagt til gruppe {}", ident, progress.getBestilling().getGruppe().getId());
    }

    protected Flux<DollyPerson> opprettDollyPerson(String ident, BestillingProgress progress, Bruker bruker) {

        return Flux.just(DollyPerson.builder()
                .ident(ident)
                .master(progress.getMaster())
                .tags(Stream.concat(progress.getBestilling().getGruppe().getTags().stream(),
                                Stream.of(Tags.DOLLY)
                                        .filter(tag -> progress.getMaster() == PDL))
                        .toList())
                .bruker(bruker)
                .build());
    }

    protected void doFerdig(Bestilling bestilling) {

        transactionHelperService.oppdaterBestillingFerdig(bestilling.getId(), bestillingService.cleanBestilling());
        MDC.remove(MDC_KEY_BESTILLING);
        log.info("Bestilling med id=#{} er ferdig", bestilling.getId());
    }

    protected Flux<BestillingProgress> opprettProgress(Bestilling bestilling, Testident.Master master, PdlResponse pdlResponse) {

        return opprettProgress(bestilling, master, pdlResponse.getStatus().is2xxSuccessful() ? pdlResponse.getIdent() : "?");
    }

    protected Flux<BestillingProgress> opprettProgress(Bestilling bestilling, Testident.Master master, String ident) {

        return Flux.just(transactionHelperService.oppdaterProgress(BestillingProgress.builder()
                .bestilling(bestilling)
                .ident(ident)
                .master(master)
                .build()));
    }

    protected Flux<PdlResponse> opprettPerson(OriginatorUtility.Originator originator) {

        return pdlDataConsumer.opprettPdl(originator.getPdlBestilling())
                .doOnNext(response -> log.info("Opprettet person med ident {} ", response));
    }

    protected Flux<String> sendOrdrePerson(BestillingProgress progress, PdlResponse forvalterStatus) {

        if (nonNull(forvalterStatus.getStatus())) {
            transactionHelperService.persister(progress, BestillingProgress::setPdlForvalterStatus,
                    forvalterStatus.getStatus().is2xxSuccessful() ? "OK" :
                            errorStatusDecoder.getErrorText(forvalterStatus.getStatus(), forvalterStatus.getFeilmelding())
            );
        }

        if (isNull(forvalterStatus.getStatus()) || forvalterStatus.getStatus().is2xxSuccessful()) {

            return pdlDataConsumer.sendOrdre(forvalterStatus.getIdent(), false)
                    .doOnNext(resultat -> {
                        var status = resultat.getStatus().is2xxSuccessful() ?
                                resultat.getJsonNode() :
                                errorStatusDecoder.getErrorText(resultat.getStatus(), resultat.getFeilmelding());
                        transactionHelperService.persister(progress, BestillingProgress::setPdlOrdreStatus, status);
                        log.info("Sendt ordre til PDL for ident {} ", forvalterStatus.getIdent());
                    })
                    .map(resultat -> resultat.getStatus().is2xxSuccessful() ? forvalterStatus.getIdent() : "");

        } else {

            return Flux.just("");
        }
    }

    protected Flux<String> sendOrdrePerson(BestillingProgress progress, String ident) {

        if (progress.getMaster() == PDLF) {
            return pdlDataConsumer.sendOrdre(ident, true)
                    .map(resultat -> {
                        var status = resultat.getStatus().is2xxSuccessful() ?
                                resultat.getJsonNode() :
                                errorStatusDecoder.getErrorText(resultat.getStatus(), resultat.getFeilmelding());
                        transactionHelperService.persister(progress, BestillingProgress::setPdlOrdreStatus, status);
                        log.info("Sendt ordre til PDL for ident {} ", ident);
                        return ident;
                    });

        } else {
            transactionHelperService.persister(progress, BestillingProgress::setPdlImportStatus, "OK");
            return Flux.just(ident);
        }
    }

    protected Flux<RsDollyBestillingRequest> createBestilling(Bestilling bestilling, IdentRepository.GruppeBestillingIdent coBestilling) {

        return Flux.just(getDollyBestillingRequest(
                Bestilling.builder()
                        .bestKriterier(coBestilling.getBestkriterier())
                        .miljoer(StringUtils.isNotBlank(bestilling.getMiljoer()) ?
                                bestilling.getMiljoer() :
                                coBestilling.getMiljoer())
                        .build()));
    }
}
