package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.tpsf.TpsfResponseHandler;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.RsDollyRelasjonRequest;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.RsOppdaterPersonResponse;
import no.nav.dolly.domain.resultset.tpsf.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.SendSkdMeldingTilTpsResponse;
import no.nav.dolly.domain.resultset.tpsf.ServiceRoutineResponseStatus;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsfRelasjonRequest;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.dolly.service.IdentService;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;
import static no.nav.dolly.domain.jpa.Testident.Master.TPSF;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class DollyBestillingService {

    protected static final String SUCCESS = "OK";
    private static final String FEIL_KUNNE_IKKE_UTFORES = "FEIL: Bestilling kunne ikke utføres: %s";
    private static final String OUT_FMT = "%s: %s";

    private final TpsfResponseHandler tpsfResponseHandler;
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

    private static String getHovedpersonAvBestillingsidenter(List<String> identer) {
        return identer.get(0); //Rask fix for å hente hoveperson i bestilling. Vet at den er første, men burde gjøre en sikrere sjekk
    }

    private static List<String> extraxtSuccessMiljoForHovedperson(String hovedperson, RsSkdMeldingResponse response) {
        Set<String> successMiljoer = new TreeSet<>();

        // Add successful messages
        addSuccessfulMessages(hovedperson, response, successMiljoer);

        // Remove unsuccessful messages
        removeUnsuccessfulMessages(hovedperson, response, successMiljoer);

        return new ArrayList<>(successMiljoer);
    }

    private static void removeUnsuccessfulMessages(String hovedperson, RsSkdMeldingResponse response, Set<String> successMiljoer) {
        for (ServiceRoutineResponseStatus sendSkdMldResponse : response.getServiceRoutineStatusResponsene()) {
            if (hovedperson.equals(sendSkdMldResponse.getPersonId())) {
                for (Map.Entry<String, String> entry : sendSkdMldResponse.getStatus().entrySet()) {
                    if (!entry.getValue().contains(SUCCESS)) {
                        successMiljoer.remove(entry.getKey());
                    }
                }
            }
        }
    }

    private static void addSuccessfulMessages(String hovedperson, RsSkdMeldingResponse response, Set<String> successMiljoer) {
        for (SendSkdMeldingTilTpsResponse sendSkdMldResponse : response.getSendSkdMeldingTilTpsResponsene()) {
            if (hovedperson.equals(sendSkdMldResponse.getPersonId())) {
                for (Map.Entry<String, String> entry : sendSkdMldResponse.getStatus().entrySet()) {
                    if (entry.getValue().contains(SUCCESS)) {
                        successMiljoer.add(entry.getKey());
                    }
                }
            }
        }
    }

    private static List<String> extraxtFailureMiljoForHovedperson(String hovedperson, RsSkdMeldingResponse response) {
        Map<String, List<String>> failures = new TreeMap<>();

        addFeilmeldingSkdMeldinger(hovedperson, response.getSendSkdMeldingTilTpsResponsene(), failures);

        addFeilmeldingServicerutiner(hovedperson, response.getServiceRoutineStatusResponsene(), failures);

        List<String> errors = new ArrayList<>();
        failures.keySet().forEach(miljoe -> errors.add(format(OUT_FMT, miljoe, join(" + ", failures.get(miljoe)))));

        return errors;
    }

    private static void addFeilmeldingSkdMeldinger(String hovedperson, List<SendSkdMeldingTilTpsResponse> responseStatus, Map<String, List<String>> failures) {
        for (SendSkdMeldingTilTpsResponse response : responseStatus) {
            if (hovedperson.equals(response.getPersonId())) {
                for (Map.Entry<String, String> entry : response.getStatus().entrySet()) {
                    if (isFaulty(entry.getValue()) && failures.containsKey(entry.getKey())) {
                        failures.get(entry.getKey()).add(format(OUT_FMT, response.getSkdmeldingstype(), encodeStatus(entry.getValue())));
                    } else if (isFaulty(entry.getValue())) {
                        failures.put(entry.getKey(), new ArrayList<>(List.of(format(OUT_FMT, response.getSkdmeldingstype(),
                                encodeStatus(entry.getValue())))));
                    }
                }
            }
        }
    }

    private static void addFeilmeldingServicerutiner(String hovedperson, List<ServiceRoutineResponseStatus> responseStatus, Map<String, List<String>> failures) {
        for (ServiceRoutineResponseStatus response : responseStatus) {
            if (hovedperson.equals(response.getPersonId())) {
                for (Map.Entry<String, String> entry : response.getStatus().entrySet()) {
                    if (isFaulty(entry.getValue()) && failures.containsKey(entry.getKey())) {
                        failures.get(entry.getKey()).add(format(OUT_FMT, response.getServiceRutinenavn(), encodeStatus(entry.getValue())));
                    } else if (isFaulty(entry.getValue())) {
                        failures.put(entry.getKey(), new ArrayList<>(List.of(format(OUT_FMT, response.getServiceRutinenavn(),
                                encodeStatus(entry.getValue())))));
                    }
                }
            }
        }
    }

    private static boolean isFaulty(String value) {
        return isNotBlank(value) && !SUCCESS.equals(value);
    }

    protected static Boolean isSyntetisk(String ident) {

        return Integer.parseInt(String.valueOf(ident.charAt(2))) >= 4;
    }

    @Async
    public void oppdaterPersonAsync(RsDollyUpdateRequest request, Bestilling bestilling) {

        try {
            Testident testident = identService.getTestIdent(bestilling.getIdent());
            if (testident.isPdl() && nonNull(request.getTpsf())) {
                throw new DollyFunctionalException("Importert person fra TESTNORGE kan ikke endres.");
            }
            BestillingProgress progress = new BestillingProgress(bestilling, bestilling.getIdent(), testident.getMaster());
            TpsfBestilling tpsfBestilling = nonNull(request.getTpsf()) ? mapperFacade.map(request.getTpsf(), TpsfBestilling.class) : new TpsfBestilling();
            tpsfBestilling.setAntall(1);
            tpsfBestilling.setNavSyntetiskIdent(isSyntetisk(bestilling.getIdent()));
            request.setNavSyntetiskIdent(tpsfBestilling.getNavSyntetiskIdent());

            AtomicReference<DollyPerson> dollyPerson = new AtomicReference<>(null);
            if (testident.isTpsf()) {

                RsOppdaterPersonResponse oppdaterPersonResponse = tpsfService.endreLeggTilPaaPerson(bestilling.getIdent(), tpsfBestilling);
                sendIdenterTilTPS(request.getEnvironments(),
                        oppdaterPersonResponse.getIdentTupler().stream()
                                .map(RsOppdaterPersonResponse.IdentTuple::getIdent).collect(toList()), null,
                        progress, request.getBeskrivelse());

                dollyPerson.set(dollyPersonCache.prepareTpsPerson(oppdaterPersonResponse.getIdentTupler().stream()
                        .map(RsOppdaterPersonResponse.IdentTuple::getIdent)
                        .findFirst().get()));

                if (!bestilling.getIdent().equals(dollyPerson.get().getHovedperson())) {
                    progress.setIdent(dollyPerson.get().getHovedperson());
                    identService.swapIdent(bestilling.getIdent(), dollyPerson.get().getHovedperson());
                    bestillingProgressService.swapIdent(bestilling.getIdent(), dollyPerson.get().getHovedperson());
                    bestillingService.swapIdent(bestilling.getIdent(), dollyPerson.get().getHovedperson());
                }

            } else {
                PdlPerson pdlPerson = objectMapper.readValue(pdlPersonConsumer.getPdlPerson(progress.getIdent()).toString(), PdlPerson.class);
                dollyPerson.set(dollyPersonCache.preparePdlPersoner(pdlPerson));

            }
            counterCustomRegistry.invoke(request);
            clientRegisters.forEach(clientRegister ->
                    clientRegister.gjenopprett(request, dollyPerson.get(), progress, true));

            oppdaterProgress(bestilling, progress);

        } catch (Exception e) {
            log.error("Bestilling med id={} til ident={} ble avsluttet med feil={}", bestilling.getId(), bestilling.getIdent(), e.getMessage(), e);
            bestilling.setFeil(format(FEIL_KUNNE_IKKE_UTFORES, e.getMessage()));

        } finally {
            oppdaterBestillingFerdig(bestilling);
        }
    }

    @Async
    public void relasjonPersonAsync(String ident, RsDollyRelasjonRequest request, Bestilling bestilling) {

        try {
            Testident testident = identService.getTestIdent(bestilling.getIdent());
            if (testident.isPdl()) {
                throw new DollyFunctionalException("Importert person fra TESTNORGE kan ikke endres.");
            }
            BestillingProgress progress = new BestillingProgress(bestilling, ident, TPSF);
            TpsfRelasjonRequest tpsfBestilling = mapperFacade.map(request.getTpsf(), TpsfRelasjonRequest.class);
            List<String> identer = tpsfService.relasjonPerson(ident, tpsfBestilling);

            RsDollyBestillingRequest utvidetBestilling = getDollyBestillingRequest(bestilling);
            sendIdenterTilTPS(request.getEnvironments(), identer, null, progress, utvidetBestilling.getBeskrivelse());

            DollyPerson dollyPerson = dollyPersonCache.prepareTpsPerson(bestilling.getIdent());
            gjenopprettNonTpsf(dollyPerson, utvidetBestilling, progress, true);

            oppdaterProgress(bestilling, progress);

        } catch (WebClientResponseException e) {
            try {
                String message = (String) objectMapper.readValue(e.getResponseBodyAsString(), Map.class).get("message");
                log.warn("Bestilling med id={} på ident={} ble avsluttet med feil: {}", bestilling.getId(), ident, message);
                bestilling.setFeil(format(FEIL_KUNNE_IKKE_UTFORES, message));

            } catch (JsonProcessingException jme) {
                log.error("Json kunne ikke hentes ut.", jme);
            }

        } catch (Exception e) {
            log.error("Bestilling med id={} på ident={} ble avsluttet med feil: {}", bestilling.getId(), ident, e.getMessage(), e);
            bestilling.setFeil(format(FEIL_KUNNE_IKKE_UTFORES, e.getMessage()));

        } finally {
            oppdaterBestillingFerdig(bestilling);
        }
    }

    protected RsDollyBestillingRequest getDollyBestillingRequest(Bestilling bestilling) {

        try {
            RsDollyBestillingRequest bestKriterier = objectMapper.readValue(bestilling.getBestKriterier(), RsDollyBestillingRequest.class);
            if (nonNull(bestilling.getTpsfKriterier())) {
                bestKriterier.setTpsf(objectMapper.readValue(bestilling.getTpsfKriterier(), RsTpsfUtvidetBestilling.class));
            }
            bestKriterier.setNavSyntetiskIdent(bestilling.getNavSyntetiskIdent());
            bestKriterier.setEnvironments(new ArrayList<>(List.of(bestilling.getMiljoer().split(","))));
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

    private void clearCache() {
        if (nonNull(cacheManager.getCache(CACHE_BESTILLING))) {
            requireNonNull(cacheManager.getCache(CACHE_BESTILLING)).clear();
        }
        if (nonNull(cacheManager.getCache(CACHE_GRUPPE))) {
            requireNonNull(cacheManager.getCache(CACHE_GRUPPE)).clear();
        }
    }

    protected void sendIdenterTilTPS(List<String> environments, List<String> identer, Testgruppe testgruppe,
                                     BestillingProgress progress, String beskrivelse) {
        try {
            RsSkdMeldingResponse response = null;
            if (!environments.isEmpty()) {
                response = tpsfService.sendIdenterTilTpsFraTPSF(identer, environments.stream().map(String::toLowerCase).collect(toList()));
                String feedbackTps = tpsfResponseHandler.extractTPSFeedback(response.getSendSkdMeldingTilTpsResponsene());
                log.info(feedbackTps);
            }

            String hovedperson = getHovedpersonAvBestillingsidenter(identer);

            List<String> successMiljoer = new ArrayList<>();
            List<String> failureMiljoer = new ArrayList<>();

            if (!environments.isEmpty()) {
                successMiljoer.addAll(extraxtSuccessMiljoForHovedperson(hovedperson, response));
                failureMiljoer.addAll(extraxtFailureMiljoForHovedperson(hovedperson, response));
            }

            if (nonNull(testgruppe)) {
                identService.saveIdentTilGruppe(hovedperson, testgruppe, TPSF, beskrivelse);
            }
            if (!successMiljoer.isEmpty()) {
                progress.setTpsfSuccessEnv(join(",", successMiljoer));
            }
            if (!failureMiljoer.isEmpty()) {
                progress.setFeil(join(",", failureMiljoer));
                log.warn("Person med ident: {} ble ikke opprettet i TPS", hovedperson);
            }
        } catch (TpsfException e) {
            tpsfResponseHandler.setErrorMessageToBestillingsProgress(e, progress);
        }
    }

    protected Optional<DollyPerson> prepareDollyPersonTpsf(Bestilling bestilling,
                                                           BestillingProgress progress) throws JsonProcessingException {

        DollyPerson dollyPerson = null;
        if (progress.isTpsf()) {
            List<Person> personer = tpsfService.hentTestpersoner(List.of(progress.getIdent()));
            if (!personer.isEmpty()) {
                dollyPerson = dollyPersonCache.prepareTpsPersoner(personer.get(0));
                sendIdenterTilTPS(asList(bestilling.getMiljoer().split(",")),
                        Stream.of(List.of(dollyPerson.getHovedperson()), dollyPerson.getPartnere(), dollyPerson.getBarn())
                                .flatMap(Collection::stream)
                                .collect(toList()),
                        bestilling.getGruppe(), progress, bestilling.getBeskrivelse());
            }

        } else if (progress.isPdl()) {
            PdlPerson pdlPerson = objectMapper.readValue(pdlPersonConsumer.getPdlPerson(progress.getIdent()).toString(), PdlPerson.class);
            dollyPerson = dollyPersonCache.preparePdlPersoner(pdlPerson);
        }

        return nonNull(dollyPerson) ? Optional.of(dollyPerson) : Optional.empty();
    }
}