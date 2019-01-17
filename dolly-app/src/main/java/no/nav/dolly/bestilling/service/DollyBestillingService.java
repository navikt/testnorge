package no.nav.dolly.bestilling.service;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.krrstub.KrrStubResponseHandler;
import no.nav.dolly.bestilling.krrstub.KrrStubService;
import no.nav.dolly.bestilling.sigrunstub.SigrunStubResponseHandler;
import no.nav.dolly.bestilling.sigrunstub.SigrunStubService;
import no.nav.dolly.bestilling.tpsf.TpsfResponseHandler;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestillingsRequest;
import no.nav.dolly.domain.resultset.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultset.SendSkdMeldingTilTpsResponse;
import no.nav.dolly.domain.resultset.ServiceRoutineResponseStatus;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdataRequest;
import no.nav.dolly.domain.resultset.sigrunstub.RsOpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBestilling;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TestgruppeService;

@Slf4j
@Service
public class DollyBestillingService {

    private static final String OUT_FMT = "%s: %s";

    @Autowired
    private TpsfResponseHandler tpsfResponseHandler;

    @Autowired
    private TpsfService tpsfService;

    @Autowired
    private TestgruppeService testgruppeService;

    @Autowired
    private IdentService identService;

    @Autowired
    private SigrunStubService sigrunStubService;

    @Autowired
    private SigrunStubResponseHandler sigrunstubResponseHandler;

    @Autowired
    private KrrStubService krrStubService;

    @Autowired
    private KrrStubResponseHandler krrstubResponseHandler;

    @Autowired
    private BestillingProgressRepository bestillingProgressRepository;

    @Autowired
    private BestillingService bestillingService;

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private CacheManager cacheManager;

    @Async
    @Transactional
    public void opprettPersonerByKriterierAsync(Long gruppeId, RsDollyBestillingsRequest request, Bestilling bestilling) {

        Testgruppe testgruppe = testgruppeService.fetchTestgruppeById(gruppeId);

        try {
            RsTpsfBestilling tpsfBestilling = nonNull(request.getTpsf()) ? request.getTpsf() : new RsTpsfBestilling();
            tpsfBestilling.setEnvironments(request.getEnvironments());
            tpsfBestilling.setAntall(1);

            int loopCount = 0;
            while (!bestillingService.isStoppet(bestilling.getId()) && loopCount < request.getAntall()) {
                List<String> bestilteIdenter = tpsfService.opprettIdenterTpsf(tpsfBestilling);
                String hovedPersonIdent = getHovedpersonAvBestillingsidenter(bestilteIdenter);
                BestillingProgress progress = new BestillingProgress(bestilling.getId(), hovedPersonIdent);

                sendIdenterTilTPS(request.getEnvironments(), bestilteIdenter, testgruppe, progress);

                handleSigrunstub(request, hovedPersonIdent, progress);

                handleKrrstub(request, bestilling.getId(), hovedPersonIdent, progress);

                oppdaterProgress(bestilling, progress);
                clearCache();
                loopCount++;
            }
        } catch (Exception e) {
            log.error("Bestilling med id <" + bestilling.getId() + "> til gruppeId <" + gruppeId + "> feilet grunnet " + e.getMessage(), e);
            bestilling.setFeil(format("FEIL: Bestilling kunne ikke utføres mot TPS: %s", e.getMessage()));
        } finally {
            oppdaterProgressFerdig(bestilling);
            clearCache();
        }
    }

    @Async
    @Transactional
    public void gjenopprettBestillingAsync(Bestilling bestilling) {

        List<BestillingProgress> identerForGjenopprett = bestillingProgressRepository.findBestillingProgressByBestillingIdOrderByBestillingId(bestilling.getOpprettetFraId());

        Iterator<BestillingProgress> identIterator = identerForGjenopprett.iterator();
        while (!bestillingService.isStoppet(bestilling.getId()) && identIterator.hasNext()) {
            BestillingProgress bestillingProgress = identIterator.next();

            List<String> identer = tpsfService.hentTilhoerendeIdenter(singletonList(bestillingProgress.getIdent()));

            String hovedPersonIdent = getHovedpersonAvBestillingsidenter(identer);
            BestillingProgress progress = new BestillingProgress(bestilling.getId(), hovedPersonIdent);

            sendIdenterTilTPS(newArrayList(bestilling.getMiljoer().split(",")), identer, bestilling.getGruppe(), progress);

            oppdaterProgress(bestilling, progress);
            clearCache();
        }
        oppdaterProgressFerdig(bestilling);
        clearCache();
    }

    private void oppdaterProgressFerdig(Bestilling bestilling) {
        if (bestillingService.isStoppet(bestilling.getId())) {
            identService.slettTestidenter(bestilling.getId());
            bestilling.setStoppet(true);
        }
        bestilling.setFerdig(true);
        bestillingService.saveBestillingToDB(bestilling);
    }

    private void oppdaterProgress(Bestilling bestilling, BestillingProgress progress) {
        if (!bestillingService.isStoppet(bestilling.getId())) {
            bestillingProgressRepository.save(progress);
            bestilling.setSistOppdatert(LocalDateTime.now());
            bestillingService.saveBestillingToDB(bestilling);
        }
    }

    private void clearCache() {
        if (nonNull(cacheManager.getCache(CACHE_BESTILLING))) {
            cacheManager.getCache(CACHE_BESTILLING).clear();
        }
        if (nonNull(cacheManager.getCache(CACHE_GRUPPE))) {
            cacheManager.getCache(CACHE_GRUPPE).clear();
        }
    }

    private void sendIdenterTilTPS(List<String> environments, List<String> identer, Testgruppe testgruppe, BestillingProgress progress) {
        try {
            RsSkdMeldingResponse response = tpsfService.sendIdenterTilTpsFraTPSF(identer, environments.stream().map(String::toLowerCase).collect(Collectors.toList()));
            String feedbackTps = tpsfResponseHandler.extractTPSFeedback(response.getSendSkdMeldingTilTpsResponsene());
            log.info(feedbackTps);

            String hovedperson = getHovedpersonAvBestillingsidenter(identer);
            List<String> successMiljoer = extraxtSuccessMiljoForHovedperson(hovedperson, response);
            List<String> failureMiljoer = extraxtFailureMiljoForHovedperson(hovedperson, response);

            if (!successMiljoer.isEmpty()) {
                identService.saveIdentTilGruppe(hovedperson, testgruppe);
                progress.setTpsfSuccessEnv(join(",", successMiljoer));
            }
            if (!failureMiljoer.isEmpty()) {
                progress.setFeil(join(",", failureMiljoer));
                log.warn("Person med ident: {} ble ikke opprettet i TPS", hovedperson);
            }
        } catch (TpsfException e) {
            tpsfResponseHandler.setErrorMessageToBestillingsProgress(e, progress);
        }

        bestillingProgressRepository.save(progress);
    }

    private void handleKrrstub(RsDollyBestillingsRequest bestillingRequest, Long bestillingsId, String hovedPersonIdent, BestillingProgress progress) {
        if (nonNull(bestillingRequest.getKrrstub())) {
            DigitalKontaktdataRequest digitalKontaktdataRequest = mapperFacade.map(bestillingRequest, DigitalKontaktdataRequest.class);
            digitalKontaktdataRequest.setPersonident(hovedPersonIdent);
            ResponseEntity krrstubResponse = krrStubService.createDigitalKontaktdata(bestillingsId, digitalKontaktdataRequest);
            progress.setKrrstubStatus(krrstubResponseHandler.extractResponse(krrstubResponse));
        }
    }

    private void handleSigrunstub(RsDollyBestillingsRequest bestillingRequest, String hovedPersonIdent, BestillingProgress progress) {
        if (nonNull(bestillingRequest.getSigrunstub())) {
            for (RsOpprettSkattegrunnlag request : bestillingRequest.getSigrunstub()) {
                request.setPersonidentifikator(hovedPersonIdent);
            }
            ResponseEntity sigrunResponse = sigrunStubService.createSkattegrunnlag(bestillingRequest.getSigrunstub());
            progress.setSigrunstubStatus(sigrunstubResponseHandler.extractResponse(sigrunResponse));
        }
    }

    private String getHovedpersonAvBestillingsidenter(List<String> identer) {
        return identer.get(0); //Rask fix for å hente hoveperson i bestilling. Vet at den er første, men burde gjøre en sikrere sjekk
    }

    private List<String> extraxtSuccessMiljoForHovedperson(String hovedperson, RsSkdMeldingResponse response) {
        Set<String> successMiljoer = new TreeSet();

        // Add successful messages
        addSuccessfulMessages(hovedperson, response, successMiljoer);

        // Remove unsuccessful messages
        removeUnsuccessfulMessages(hovedperson, response, successMiljoer);

        return newArrayList(successMiljoer);
    }

    private void removeUnsuccessfulMessages(String hovedperson, RsSkdMeldingResponse response, Set<String> successMiljoer) {
        for (SendSkdMeldingTilTpsResponse sendSkdMldResponse : response.getSendSkdMeldingTilTpsResponsene()) {
            if (hovedperson.equals(sendSkdMldResponse.getPersonId())) {
                for (Map.Entry<String, String> entry : sendSkdMldResponse.getStatus().entrySet()) {
                    if (!entry.getValue().contains("OK")) {
                        successMiljoer.remove(entry.getKey());
                    }
                }
            }
        }
    }

    private void addSuccessfulMessages(String hovedperson, RsSkdMeldingResponse response, Set<String> successMiljoer) {
        for (SendSkdMeldingTilTpsResponse sendSkdMldResponse : response.getSendSkdMeldingTilTpsResponsene()) {
            if (hovedperson.equals(sendSkdMldResponse.getPersonId())) {
                for (Map.Entry<String, String> entry : sendSkdMldResponse.getStatus().entrySet()) {
                    if (entry.getValue().contains("OK")) {
                        successMiljoer.add(entry.getKey());
                    }
                }
            }
        }
    }

    private List<String> extraxtFailureMiljoForHovedperson(String hovedperson, RsSkdMeldingResponse response) {
        Map<String, List<String>> failures = new TreeMap();

        addFeilmeldingSkdMeldinger(hovedperson, response, failures);

        addFeilmeldingServicerutiner(hovedperson, response, failures);

        List<String> errors = newArrayList();
        failures.keySet().forEach(miljoe -> errors.add(format(OUT_FMT, miljoe, join(" + ", failures.get(miljoe)))));

        return errors;
    }

    private void addFeilmeldingSkdMeldinger(String hovedperson, RsSkdMeldingResponse response, Map<String, List<String>> failures) {
        for (SendSkdMeldingTilTpsResponse sendSkdMldResponse : response.getSendSkdMeldingTilTpsResponsene()) {
            if (hovedperson.equals(sendSkdMldResponse.getPersonId())) {
                for (Map.Entry<String, String> entry : sendSkdMldResponse.getStatus().entrySet()) {
                    if (!entry.getValue().contains("OK") && !failures.containsKey(entry.getKey())) {
                        failures.put(entry.getKey(), newArrayList(trimFeilmelding(entry.getValue())));
                    } else if (!entry.getValue().contains("OK")) {
                        failures.get(entry.getKey()).add(trimFeilmelding(entry.getValue()));
                    }
                }
            }
        }
    }

    private void addFeilmeldingServicerutiner(String hovedperson, RsSkdMeldingResponse response, Map<String, List<String>> failures) {
        for (ServiceRoutineResponseStatus responseStatus : response.getServiceRoutineStatusResponsene()) {
            if (hovedperson.equals(responseStatus.getPersonId())) {
                if (!"OK".equals(responseStatus.getStatus().getKode()) && !failures.containsKey(responseStatus.getEnvironment())) {
                    failures.put(responseStatus.getEnvironment(), newArrayList(formatFeilmelding(responseStatus)));
                } else if (!"OK".equals(responseStatus.getStatus().getKode())) {
                    failures.get(responseStatus.getEnvironment()).add(formatFeilmelding(responseStatus));
                }
            }
        }
    }

    private String trimFeilmelding(String melding) {
        return melding.replaceAll("08%", "").replaceAll("%;", "").trim();
    }

    private String formatFeilmelding(ServiceRoutineResponseStatus responseStatus) {
        return format(OUT_FMT, responseStatus.getServiceRutinenavn(), responseStatus.getStatus().getUtfyllendeMelding());
    }
}