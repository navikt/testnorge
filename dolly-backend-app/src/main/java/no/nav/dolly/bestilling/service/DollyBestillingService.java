package no.nav.dolly.bestilling.service;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.time.LocalDateTime.now;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.tpsf.TpsfResponseHandler;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyBestillingFraIdenterRequest;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.domain.resultset.tpsf.CheckStatusResponse;
import no.nav.dolly.domain.resultset.tpsf.IdentStatus;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.SendSkdMeldingTilTpsResponse;
import no.nav.dolly.domain.resultset.tpsf.ServiceRoutineResponseStatus;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TestgruppeService;

@Slf4j
@Service
@RequiredArgsConstructor
public class DollyBestillingService {

    private static final String FEIL_KUNNE_IKKE_UTFORES = "FEIL: Bestilling kunne ikke utføres: %s";
    private static final String SUCCESS = "OK";
    private static final String OUT_FMT = "%s: %s";

    private final TpsfResponseHandler tpsfResponseHandler;
    private final TpsfService tpsfService;
    private final TestgruppeService testgruppeService;
    private final IdentService identService;
    private final BestillingProgressRepository bestillingProgressRepository;
    private final BestillingService bestillingService;
    private final MapperFacade mapperFacade;
    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper;
    private final List<ClientRegister> clientRegisters;

    @Async
    public void opprettPersonerByKriterierAsync(Long gruppeId, RsDollyBestillingRequest request, Bestilling bestilling) {

        Testgruppe testgruppe = testgruppeService.fetchTestgruppeById(gruppeId);

        try {
            TpsfBestilling tpsfBestilling = nonNull(request.getTpsf()) ? mapperFacade.map(request.getTpsf(), TpsfBestilling.class) : new TpsfBestilling();
            tpsfBestilling.setEnvironments(request.getEnvironments());
            tpsfBestilling.setAntall(1);

            int loopCount = 0;
            while (!bestillingService.isStoppet(bestilling.getId()) && loopCount < request.getAntall()) {
                preparePerson(request, bestilling, testgruppe, tpsfBestilling);
                clearCache();
                loopCount++;
            }
        } catch (Exception e) {
            log.error("Bestilling med id <" + bestilling.getId() + "> til gruppeId <" + gruppeId + "> feilet grunnet " + e.getMessage(), e);
            bestilling.setFeil(format(FEIL_KUNNE_IKKE_UTFORES, e.getMessage()));
        } finally {
            oppdaterProgressFerdig(bestilling);
            clearCache();
        }
    }

    @Async
    public void opprettPersonerFraIdenterMedKriterierAsync(Long gruppeId, RsDollyBestillingFraIdenterRequest request, Bestilling bestilling) {

        Testgruppe testgruppe = testgruppeService.fetchTestgruppeById(gruppeId);

        try {
            TpsfBestilling tpsfBestilling = nonNull(request.getTpsf()) ? mapperFacade.map(request.getTpsf(), TpsfBestilling.class) : new TpsfBestilling();
            tpsfBestilling.setEnvironments(request.getEnvironments());

            CheckStatusResponse tilgjengeligeIdenter = tpsfService.checkEksisterendeIdenter(request.getOpprettFraIdenter());
            List<String> identer = tilgjengeligeIdenter.getStatuser().stream()
                    .filter(IdentStatus::isAvailable)
                    .map(IdentStatus::getIdent)
                    .collect(toList());
            oppdaterBestilling(bestilling, tilgjengeligeIdenter);

            int loopCount = 0;
            while (!bestillingService.isStoppet(bestilling.getId()) && loopCount < identer.size()) {
                tpsfBestilling.setOpprettFraIdenter(newArrayList(identer.get(loopCount)));
                preparePerson(request, bestilling, testgruppe, tpsfBestilling);
                clearCache();
                loopCount++;
            }
        } catch (Exception e) {
            log.error("Bestilling med id={} til gruppeId={} ble avsluttet med feil={}", bestilling.getId(), gruppeId, e.getMessage(), e);
            bestilling.setFeil(format(FEIL_KUNNE_IKKE_UTFORES, e.getMessage()));
        } finally {
            oppdaterProgressFerdig(bestilling);
            clearCache();
        }
    }

    @Async
    public void oppdaterPersonAsync(String ident, RsDollyUpdateRequest request, Bestilling bestilling) {

        try {
            BestillingProgress progress = new BestillingProgress(bestilling.getId(), ident);

            tpsfService.updatePerson(request.getTpsfPerson());
            sendIdenterTilTPS(request.getEnvironments(), singletonList(ident), null, progress);
            gjenopprettNonTpsf(TpsPerson.builder().hovedperson(ident).build(), bestilling, progress);

        } catch (Exception e) {
            log.error("Bestilling med id={} til ident={} ble avsluttet med feil={}", bestilling.getId(), ident, e.getMessage(), e);
            bestilling.setFeil(format(FEIL_KUNNE_IKKE_UTFORES, e.getMessage()));

        } finally {
            oppdaterProgressFerdig(bestilling);
            clearCache();

        }
    }

    @Async
    public void gjenopprettBestillingAsync(Bestilling bestilling) {

        List<BestillingProgress> identerForGjenopprett = bestillingProgressRepository.findBestillingProgressByBestillingIdOrderByBestillingId(bestilling.getOpprettetFraId());

        Iterator<BestillingProgress> identIterator = identerForGjenopprett.iterator();
        while (!bestillingService.isStoppet(bestilling.getId()) && identIterator.hasNext()) {
            BestillingProgress bestillingProgress = identIterator.next();

            List<Person> personer = tpsfService.hentTestpersoner(singletonList(bestillingProgress.getIdent()));
            if (personer.isEmpty()) {
                bestilling.setFeil(format("Gjenopprett feilet fordi ident %s har blitt slettet fra database", bestillingProgress.getIdent()));
                bestilling.setStoppet(true);
                break;
            }

            TpsPerson tpsPerson = buildTpsPerson(bestilling, extractIdenter(personer), personer);

            BestillingProgress progress = new BestillingProgress(bestilling.getId(), tpsPerson.getHovedperson());

            sendIdenterTilTPS(newArrayList(bestilling.getMiljoer().split(",")), extractIdenter(personer), bestilling.getGruppe(), progress);

            gjenopprettNonTpsf(tpsPerson, bestilling, progress);

            oppdaterProgress(bestilling, progress);
            clearCache();
        }
        oppdaterProgressFerdig(bestilling);
        clearCache();
    }

    private void gjenopprettNonTpsf(TpsPerson tpsPerson, Bestilling bestilling, BestillingProgress progress) {

        if (nonNull(bestilling.getBestKriterier())) {
            try {
                RsDollyBestillingRequest bestKriterier = objectMapper.readValue(bestilling.getBestKriterier(), RsDollyBestillingRequest.class);
                if (nonNull(bestilling.getTpsfKriterier())) {
                    bestKriterier.setTpsf(objectMapper.readValue(bestilling.getTpsfKriterier(), RsTpsfUtvidetBestilling.class));
                }
                bestKriterier.setEnvironments(newArrayList(bestilling.getMiljoer().split(",")));

                clientRegisters.forEach(clientRegister ->
                        clientRegister.gjenopprett(bestKriterier, tpsPerson, progress));

                oppdaterProgress(bestilling, progress);

            } catch (IOException e) {
                log.error("Feilet å lese bestillingskriterier", e);
            }
        }
    }

    private void oppdaterBestilling(Bestilling bestilling, CheckStatusResponse tilgjengeligeIdenter) {
        bestilling.setAntallIdenter((int) tilgjengeligeIdenter.getStatuser().stream().filter(IdentStatus::isAvailable).count());
        tilgjengeligeIdenter.getStatuser().forEach(identStatus -> {
            if (!identStatus.isAvailable()) {
                oppdaterProgress(bestilling, BestillingProgress.builder()
                        .bestillingId(bestilling.getId())
                        .ident(identStatus.getIdent().length() <= 11 ? identStatus.getIdent() :
                                format("%s*%s", identStatus.getIdent().substring(0, 6),
                                        identStatus.getIdent().substring(identStatus.getIdent().length() - 4, identStatus.getIdent().length())))
                        .feil(format("Miljø: %s", identStatus.getStatus()))
                        .build());
                clearCache();
            }
        });
    }

    private void preparePerson(RsDollyBestilling request, Bestilling bestilling, Testgruppe testgruppe, TpsfBestilling tpsfBestilling) {

        try {
            List<String> leverteIdenter = tpsfService.opprettIdenterTpsf(tpsfBestilling);

            TpsPerson tpsPerson = buildTpsPerson(bestilling, leverteIdenter, null);

            BestillingProgress progress = new BestillingProgress(bestilling.getId(), tpsPerson.getHovedperson());

            sendIdenterTilTPS(request.getEnvironments(), leverteIdenter, testgruppe, progress);

            RsDollyBestillingRequest bestKriterier = objectMapper.readValue(bestilling.getBestKriterier(), RsDollyBestillingRequest.class);
            bestKriterier.setEnvironments(request.getEnvironments());
            if (nonNull(bestilling.getTpsfKriterier())) {
                bestKriterier.setTpsf(objectMapper.readValue(bestilling.getTpsfKriterier(), RsTpsfUtvidetBestilling.class));
            }

            clientRegisters.forEach(clientRegister -> clientRegister.gjenopprett(bestKriterier, tpsPerson, progress));

            oppdaterProgress(bestilling, progress);

        } catch (IOException e) {
            log.error("Feilet å lese bestillingskriterier", e);
        }
    }

    private TpsPerson buildTpsPerson(Bestilling bestilling, List<String> leverteIdenter, List<Person> personer) {

        TpsPerson tpsPerson = TpsPerson.builder()
                .hovedperson(leverteIdenter.get(0))
                .persondetalj(nonNull(personer) ? personer.get(0) : null)
                .build();

        if (nonNull(bestilling.getTpsfKriterier())) {
            try {
                TpsfBestilling tpsfBestilling = objectMapper.readValue(bestilling.getTpsfKriterier(), TpsfBestilling.class);

                if (nonNull(tpsfBestilling.getRelasjoner())) {
                    tpsPerson.setPartner(nonNull(tpsfBestilling.getRelasjoner().getPartner()) ? leverteIdenter.get(1) : null);
                    tpsPerson.setBarn(nonNull(tpsfBestilling.getRelasjoner().getBarn()) ?
                            harPartner(tpsfBestilling, leverteIdenter) : null);
                }
            } catch (IOException e) {
                log.error("Feilet å hente tpsfKriterier", e);
            }
        }

        return tpsPerson;
    }

    private static List<String> harPartner(TpsfBestilling bestilling, List<String> leverteIdenter) {
        return nonNull(bestilling.getRelasjoner().getPartner()) ?
                leverteIdenter.subList(2, leverteIdenter.size()) :
                leverteIdenter.subList(1, leverteIdenter.size());
    }

    private void oppdaterProgressFerdig(Bestilling bestilling) {
        if (bestillingService.isStoppet(bestilling.getId())) {
            bestilling.setStoppet(true);
        }
        bestilling.setFerdig(true);
        bestillingService.saveBestillingToDB(bestilling);
    }

    private void oppdaterProgress(Bestilling bestilling, BestillingProgress progress) {
        if (!bestillingService.isStoppet(bestilling.getId())) {
            bestillingProgressRepository.save(progress);
        }
        bestilling.setSistOppdatert(now());
        bestillingService.saveBestillingToDB(bestilling);
    }

    private void clearCache() {
        if (nonNull(cacheManager.getCache(CACHE_BESTILLING))) {
            requireNonNull(cacheManager.getCache(CACHE_BESTILLING)).clear();
        }
        if (nonNull(cacheManager.getCache(CACHE_GRUPPE))) {
            requireNonNull(cacheManager.getCache(CACHE_GRUPPE)).clear();
        }
    }

    private List<String> extractIdenter(List<Person> personer) {

        List<String> identerMedFamilie = new ArrayList();
        personer.forEach(person -> {
            identerMedFamilie.add(person.getIdent());
            person.getRelasjoner().forEach(relasjon -> identerMedFamilie.add(relasjon.getPersonRelasjonMed().getIdent()));

        });

        return identerMedFamilie;
    }

    private void sendIdenterTilTPS(List<String> environments, List<String> identer, Testgruppe testgruppe, BestillingProgress progress) {
        try {
            RsSkdMeldingResponse response = tpsfService.sendIdenterTilTpsFraTPSF(identer, environments.stream().map(String::toLowerCase).collect(toList()));
            String feedbackTps = tpsfResponseHandler.extractTPSFeedback(response.getSendSkdMeldingTilTpsResponsene());
            log.info(feedbackTps);

            String hovedperson = getHovedpersonAvBestillingsidenter(identer);
            List<String> successMiljoer = extraxtSuccessMiljoForHovedperson(hovedperson, response);
            List<String> failureMiljoer = extraxtFailureMiljoForHovedperson(hovedperson, response);

            if (nonNull(testgruppe)) {
                identService.saveIdentTilGruppe(hovedperson, testgruppe);
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

        bestillingProgressRepository.save(progress);
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
                    if (!entry.getValue().contains(SUCCESS)) {
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
                    if (entry.getValue().contains(SUCCESS)) {
                        successMiljoer.add(entry.getKey());
                    }
                }
            }
        }
    }

    private List<String> extraxtFailureMiljoForHovedperson(String hovedperson, RsSkdMeldingResponse response) {
        Map<String, List<String>> failures = new TreeMap();

        addFeilmeldingSkdMeldinger(hovedperson, response.getSendSkdMeldingTilTpsResponsene(), failures);

        addFeilmeldingServicerutiner(hovedperson, response.getServiceRoutineStatusResponsene(), failures);

        List<String> errors = newArrayList();
        failures.keySet().forEach(miljoe -> errors.add(format(OUT_FMT, miljoe, join(" + ", failures.get(miljoe)))));

        return errors;
    }

    private void addFeilmeldingSkdMeldinger(String hovedperson, List<SendSkdMeldingTilTpsResponse> responseStatus, Map<String, List<String>> failures) {
        for (SendSkdMeldingTilTpsResponse response : responseStatus) {
            if (hovedperson.equals(response.getPersonId())) {
                for (Map.Entry<String, String> entry : response.getStatus().entrySet()) {
                    if (!entry.getValue().contains(SUCCESS) && !failures.containsKey(entry.getKey())) {
                        failures.put(entry.getKey(), newArrayList(format(OUT_FMT, response.getSkdmeldingstype(), entry.getValue())));
                    } else if (!entry.getValue().contains(SUCCESS)) {
                        failures.get(entry.getKey()).add(format(OUT_FMT, response.getSkdmeldingstype(), entry.getValue()));
                    }
                }
            }
        }
    }

    private void addFeilmeldingServicerutiner(String hovedperson, List<ServiceRoutineResponseStatus> responseStatus, Map<String, List<String>> failures) {
        for (ServiceRoutineResponseStatus response : responseStatus) {
            if (hovedperson.equals(response.getPersonId())) {
                for (Map.Entry<String, String> entry : response.getStatus().entrySet()) {
                    if (!SUCCESS.equals(entry.getValue()) && !failures.containsKey(entry.getKey())) {
                        failures.put(entry.getKey(), newArrayList(format(OUT_FMT, response.getServiceRutinenavn(), entry.getValue())));
                    } else if (!SUCCESS.equals(entry.getValue())) {
                        failures.get(entry.getKey()).add(format(OUT_FMT, response.getServiceRutinenavn(), entry.getValue()));
                    }
                }
            }
        }
    }
}