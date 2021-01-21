package no.nav.identpool.service;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Objects.nonNull;
import static no.nav.identpool.domain.Rekvireringsstatus.I_BRUK;
import static no.nav.identpool.domain.Rekvireringsstatus.LEDIG;
import static no.nav.identpool.util.PersonidentUtil.getIdentType;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.domain.TpsStatus;
import no.nav.identpool.domain.Ident;
import no.nav.identpool.exception.ForFaaLedigeIdenterException;
import no.nav.identpool.exception.IdentAlleredeIBrukException;
import no.nav.identpool.exception.UgyldigPersonidentifikatorException;
import no.nav.identpool.repository.IdentRepository;
import no.nav.identpool.repository.WhitelistRepository;
import no.nav.identpool.rs.v1.support.HentIdenterRequest;
import no.nav.identpool.rs.v1.support.MarkerBruktRequest;
import no.nav.identpool.util.IdentGeneratorUtil;
import no.nav.identpool.util.PersonidentUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentpoolService {

    private static final int MAKS_ANTALL_MANGLENDE_IDENTER = 80;
    private static final int MAKS_ANTALL_OPPRETTINGER_ONTHEFLY_PER_DAG = 250;
    private static final int MAKS_ANTALL_KALL_MOT_TPS = 3;
    private static final int MAKS_ANTALL_FORSOEK_PAA_LEDIG_DATO = 7;

    private final IdentRepository identRepository;
    private final IdentTpsService identTpsService;
    private final IdentGeneratorService identGeneratorService;
    private final WhitelistRepository whitelistRepository;

    public List<String> rekvirerNaermesteLedigDato(HentIdenterRequest request) {
        LocalDate foedtEtter = request.getFoedtEtter();
        LocalDate foedtFoer = request.getFoedtFoer();

        int i = 0;
        while (i <= MAKS_ANTALL_FORSOEK_PAA_LEDIG_DATO) {
            request.setFoedtEtter(foedtEtter.minusDays(i));
            request.setFoedtFoer(foedtFoer.minusDays(i));

            try {
                return rekvirer(request);
            } catch (ForFaaLedigeIdenterException e) {
                if (i == MAKS_ANTALL_FORSOEK_PAA_LEDIG_DATO) {
                    throw e;
                } else {
                    log.info("Fant ikke ledig ident etter {} forsøk. Prøver med tidligere fødselsdato.", i + 1);
                    i++;
                }
            }
        }

        return new ArrayList<>();
    }

    public List<String> rekvirer(HentIdenterRequest request) {
        Set<Ident> identEntities = hentLedigeIdenterFraDatabase(request);

        List<String> identList = identEntities.stream()
                .map(Ident::getPersonidentifikator)
                .collect(Collectors.toList());

        int missingIdentCount = request.getAntall() - identList.size();
//        if (missingIdentCount > MAKS_ANTALL_MANGLENDE_IDENTER) {
//            String errMsg = "Antall etterspurte identer er større enn tilgjengelig antall. Reduser antallet med %s, " +
//                    "for å få opprettet identene i TPS.";
//            throw new ForFaaLedigeIdenterException(String.format(errMsg, (missingIdentCount - MAKS_ANTALL_MANGLENDE_IDENTER)));
//        }

        if (missingIdentCount > 0) {
            // hent identer som er i bruk i ident-pool-databasen allerede, for ikke å opprette eksisterende identifikasjonsnumre:
            int daysInRange = Math.toIntExact(DAYS.between(request.getFoedtEtter(), request.getFoedtFoer())) + 1;
            Set<String> usedIdents = hentBrukteIdenterFraDatabase(request, daysInRange);
            if (daysInRange == 1 && usedIdents.size() >= MAKS_ANTALL_OPPRETTINGER_ONTHEFLY_PER_DAG * daysInRange) {
                throw new ForFaaLedigeIdenterException(format("Identpool finner ikke ledige identer i hht forespørsel: " +
                                "identType %s, kjønn %s, fødtEtter %s, fødtFør %s, syntetisk %b -- "
                                + "forsøk å bestille med andre kriterier.",
                        nonNull(request.getIdenttype()) ? request.getIdenttype().name() : null,
                        nonNull(request.getKjoenn()) ? request.getKjoenn().name() : null,
                        nonNull(request.getFoedtEtter()) ? request.getFoedtEtter().format(ISO_DATE) : null,
                        nonNull(request.getFoedtFoer()) ? request.getFoedtFoer().format(ISO_DATE) : null,
                        request.getSyntetisk()));
            }

            List<String> newIdents = hentIdenterFraTps(request, usedIdents);
            if (newIdents.size() >= missingIdentCount) {
                saveIdents(newIdents.subList(0, missingIdentCount), I_BRUK, request.getRekvirertAv());
                identList.addAll(newIdents.subList(0, missingIdentCount));
                if (newIdents.size() > missingIdentCount) {
                    saveIdents(newIdents.subList(missingIdentCount, newIdents.size()), LEDIG, null);
                }
            }

            throw new ForFaaLedigeIdenterException(format("Identpool finner ikke ledige identer i hht forespørsel: " +
                            "identType %s, kjønn %s, fødtEtter %s, fødtFør %s, syntetisk %b -- "
                            + "forsøk å bestille med andre kriterier.",
                    nonNull(request.getIdenttype()) ? request.getIdenttype().name() : null,
                    nonNull(request.getKjoenn()) ? request.getKjoenn().name() : null,
                    nonNull(request.getFoedtEtter()) ? request.getFoedtEtter().format(ISO_DATE) : null,
                    nonNull(request.getFoedtFoer()) ? request.getFoedtFoer().format(ISO_DATE) : null,
                    request.getSyntetisk()));
        }

        //Sier at request har tatt disse i bruk
        identEntities.forEach(i -> {
            i.setRekvireringsstatus(I_BRUK);
            i.setRekvirertAv(request.getRekvirertAv());
        });
        identRepository.saveAll(identEntities);

        return identList;
    }

    public Boolean erLedig(
            String personidentifikator,
            List<String> miljoer
    ) {
        Ident ident = identRepository.findTopByPersonidentifikator(personidentifikator);
        if (ident != null) {
            return ident.getRekvireringsstatus().equals(Rekvireringsstatus.LEDIG) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            Set<TpsStatus> tpsStatus = identTpsService.checkIdentsInTps(Collections.singleton(personidentifikator), miljoer);
            if (tpsStatus.size() != 1) {
                throw new HttpServerErrorException(INTERNAL_SERVER_ERROR, "Fikk ikke riktig antall statuser tilbake på metodekall til checkIdentsInTps");
            }
            TpsStatus identStatus = tpsStatus.iterator().next();

            return !identStatus.isInUse();
        }
    }

    public void markerBrukt(MarkerBruktRequest markerBruktRequest) {
        Ident ident = identRepository.findTopByPersonidentifikator(markerBruktRequest.getPersonidentifikator());
        if (ident == null) {
            String personidentifikator = markerBruktRequest.getPersonidentifikator();

            Ident newIdent = Ident.builder()
                    .identtype(getIdentType(personidentifikator))
                    .personidentifikator(personidentifikator)
                    .rekvireringsstatus(I_BRUK)
                    .rekvirertAv(markerBruktRequest.getBruker())
                    .finnesHosSkatt(false)
                    .kjoenn(PersonidentUtil.getKjonn(personidentifikator))
                    .foedselsdato(PersonidentUtil.toBirthdate(personidentifikator))
                    .syntetisk(isSyntetisk(personidentifikator))
                    .build();
            identRepository.save(newIdent);
            return;

        } else if (ident.getRekvireringsstatus().equals(Rekvireringsstatus.LEDIG)) {
            ident.setRekvireringsstatus(I_BRUK);
            ident.setRekvirertAv(markerBruktRequest.getBruker());
            identRepository.save(ident);
            return;
        } else if (ident.getRekvireringsstatus().equals(I_BRUK)) {
            throw new IdentAlleredeIBrukException("Den etterspurte identen er allerede markert som i bruk.");
        }
        throw new IllegalStateException("Den etterspurte identen er ugyldig siden den hverken markert som i bruk eller ledig.");
    }

    public List<String> markerBruktFlere(
            String rekvirertAv,
            List<String> identer
    ) {
        List<String> identerMarkertSomIBruk = new ArrayList<>(identer.size());
        Set<String> identerSomSkalSjekkes = new HashSet<>(identer.size());
        for (String id : identer) {
            Ident ident = identRepository.findTopByPersonidentifikator(id);
            if (ident == null) {
                identerSomSkalSjekkes.add(id);
            } else if (LEDIG.equals(ident.getRekvireringsstatus())) {
                ident.setRekvireringsstatus(I_BRUK);
                ident.setRekvirertAv(rekvirertAv);
                identRepository.save(ident);
                identerMarkertSomIBruk.add(id);
            }
        }

        Set<TpsStatus> tpsStatuses = identTpsService.checkIdentsInTps(identerSomSkalSjekkes, Collections.singletonList("q0"));

        for (TpsStatus tpsStatus : tpsStatuses) {
            if (!tpsStatus.isInUse()) {
                String id = tpsStatus.getIdent();
                identRepository.save(Ident.builder()
                        .identtype(getIdentType(id))
                        .personidentifikator(id)
                        .rekvireringsstatus(I_BRUK)
                        .rekvirertAv(rekvirertAv)
                        .finnesHosSkatt(false)
                        .kjoenn(PersonidentUtil.getKjonn(id))
                        .foedselsdato(PersonidentUtil.toBirthdate(id))
                        .syntetisk(isSyntetisk(id))
                        .build());
                identerMarkertSomIBruk.add(id);
            }
        }

        return identerMarkertSomIBruk;
    }

    public List<String> frigjoerIdenter(
            String rekvirertAv,
            List<String> identer
    ) {
        List<String> ledigeIdenter = new ArrayList<>(identer.size());
        Map<String, Ident> fnrMedIdent = new HashMap<>(identer.size());
        Set<String> identerSomSkalSjekkes = new HashSet<>(identer.size());
        for (String id : identer) {
            Ident ident = identRepository.findTopByPersonidentifikator(id);
            if (ident != null) {
                if (LEDIG.equals(ident.getRekvireringsstatus())) {
                    ledigeIdenter.add(id);
                } else if (!ident.isFinnesHosSkatt() && rekvirertAv.equals(ident.getRekvirertAv())) {
                    fnrMedIdent.put(id, ident);
                    identerSomSkalSjekkes.add(id);
                }
            }
        }

        Set<TpsStatus> tpsStatuses = identTpsService.checkIdentsInTps(identerSomSkalSjekkes, Collections.singletonList("q0"));
        return leggTilLedigeIdenterIMiljoer(ledigeIdenter, fnrMedIdent, tpsStatuses);
    }

    public List<String> frigjoerLedigeIdenter(List<String> identer) {
        List<String> ledigeIdenter = new ArrayList<>(identer.size());
        Map<String, Ident> fnrMedIdent = new HashMap<>(identer.size());
        Set<String> identerSomSkalSjekkes = new HashSet<>(identer.size());
        for (String id : identer) {
            Ident ident = identRepository.findTopByPersonidentifikator(id);
            if (ident != null) {
                if (LEDIG.equals(ident.getRekvireringsstatus())) {
                    ledigeIdenter.add(id);
                } else if (!ident.isFinnesHosSkatt()) {
                    fnrMedIdent.put(id, ident);
                    identerSomSkalSjekkes.add(id);
                }
            }
        }
        Set<TpsStatus> tpsStatuses = identTpsService.checkIdentsInTps(identerSomSkalSjekkes);
        return leggTilLedigeIdenterIMiljoer(ledigeIdenter, fnrMedIdent, tpsStatuses);
    }

    private List<String> leggTilLedigeIdenterIMiljoer(
            List<String> ledigeIdenter,
            Map<String, Ident> fnrMedIdent,
            Set<TpsStatus> tpsStatuses
    ) {
        for (TpsStatus tpsStatus : tpsStatuses) {
            if (!tpsStatus.isInUse()) {
                Ident ident = fnrMedIdent.get(tpsStatus.getIdent());
                ident.setRekvireringsstatus(LEDIG);
                ident.setRekvirertAv(null);
                identRepository.save(ident);
                ledigeIdenter.add(tpsStatus.getIdent());
            }
        }
        return ledigeIdenter;
    }

    public Ident lesInnhold(String personidentifikator) {
        return identRepository.findTopByPersonidentifikator(personidentifikator);
    }

    public void registrerFinnesHosSkatt(String personidentifikator) {

        if (Identtype.FNR.equals(getIdentType(personidentifikator))) {
            throw new UgyldigPersonidentifikatorException("personidentifikatoren er ikke et DNR");
        }

        Ident ident = identRepository.findTopByPersonidentifikator(personidentifikator);
        if (ident != null) {
            ident.setFinnesHosSkatt(true);
            ident.setRekvireringsstatus(I_BRUK);
            ident.setRekvirertAv("DREK");
        } else {
            ident = IdentGeneratorUtil.createIdent(personidentifikator, I_BRUK, "DREK");
            ident.setFinnesHosSkatt(true);
        }
        ident.setSyntetisk(ident.isSyntetisk());
        identRepository.save(ident);
    }

    public List<String> hentLedigeFNRFoedtMellom(
            LocalDate from,
            LocalDate to
    ) {
        List<Ident> identer = identRepository.findByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatus(from, to, Identtype.FNR, LEDIG);
        return identer.stream().
                filter(i -> i.getIdenttype() == Identtype.FNR).
                filter(i -> i.getFoedselsdato().isAfter(from) && i.getFoedselsdato().isBefore(to)).
                map(Ident::getPersonidentifikator).
                collect(Collectors.toList());
    }

    public List<String> hentWhitelist() {
        List<String> whiteFnrs = new ArrayList<>();
        whitelistRepository.findAll().forEach(
                whitelist -> whiteFnrs.add(whitelist.getFnr())
        );
        return whiteFnrs;
    }

    private List<String> hentIdenterFraTps(
            HentIdenterRequest request,
            Set<String> identerIIdentPool
    ) {
        Set<String> temp = new HashSet<>();
        for (int i = 1; i < MAKS_ANTALL_KALL_MOT_TPS && temp.size() < request.getAntall(); i++) {

            Set<String> genererteIdenter;
            if (request.getIdenttype() == Identtype.FDAT) {
                genererteIdenter = identGeneratorService.genererIdenterFdat(request, identerIIdentPool);
            } else {
                genererteIdenter = identGeneratorService.genererIdenter(request, identerIIdentPool);
            }

            // filtrer vekk eksisterende
            Set<String> finnesIkkeAllerede = genererteIdenter.stream()
                    .filter(ident -> !identRepository.existsByPersonidentifikator(ident))
                    .collect(Collectors.toSet());

            Set<TpsStatus> kontrollerteIdenter = identTpsService.checkIdentsInTps(finnesIkkeAllerede);

            saveIdents(kontrollerteIdenter.stream()
                    .filter(TpsStatus::isInUse)
                    .map(TpsStatus::getIdent)
                    .collect(Collectors.toList()), I_BRUK, "TPS");

            temp.addAll(kontrollerteIdenter.stream()
                    .filter(x -> !x.isInUse())
                    .map(TpsStatus::getIdent)
                    .collect(Collectors.toList())
            );
        }
        return new ArrayList<>(temp);
    }

    private Set<Ident> hentLedigeIdenterFraDatabase(HentIdenterRequest request) {
        Set<Ident> identEntities = new HashSet<>();

        HentIdenterRequest availableIdentsRequest = HentIdenterRequest.builder()
                .identtype(request.getIdenttype())
                .foedtEtter(request.getFoedtEtter().minusDays(1))
                .foedtFoer(request.getFoedtFoer().plusDays(1))
                .kjoenn(request.getKjoenn())
                .antall(request.getAntall())
                .build();

        Page<Ident> firstPage = findPage(availableIdentsRequest, LEDIG, 0);
        Map<Integer, Page<Ident>> pageCache = new HashMap<>();
        pageCache.put(0, firstPage);

        int totalPages = firstPage.getTotalPages();
        if (totalPages > 0) {
            List<String> usedIdents = new ArrayList<>();
            SecureRandom rand = new SecureRandom();
            for (int i = 0; i < request.getAntall(); i++) {
                int randomPageNumber = rand.nextInt(totalPages);
                if (!pageCache.containsKey(randomPageNumber)) {
                    pageCache.put(randomPageNumber, findPage(availableIdentsRequest, LEDIG, randomPageNumber));
                }

                List<Ident> content = pageCache.get(randomPageNumber).getContent();
                for (Ident ident : content) {
                    if (!usedIdents.contains(ident.getPersonidentifikator())) {
                        usedIdents.add(ident.getPersonidentifikator());
                        identEntities.add(ident);
                        break;
                    }
                }
            }
        }
        return identEntities;
    }

    private Set<String> hentBrukteIdenterFraDatabase(
            HentIdenterRequest request,
            int daysInRange) {

        HentIdenterRequest usedIdentsRequest = HentIdenterRequest.builder()
                .identtype(request.getIdenttype())
                .foedtEtter(request.getFoedtEtter().minusDays(1))
                .foedtFoer(request.getFoedtFoer().plusDays(1))
                .kjoenn(request.getKjoenn())
                .antall(MAKS_ANTALL_OPPRETTINGER_ONTHEFLY_PER_DAG * daysInRange)
                .build();

        return StreamSupport.stream(
                findPage(usedIdentsRequest, I_BRUK, 0).spliterator(), false)
                .map(Ident::getPersonidentifikator)
                .collect(Collectors.toSet());
    }

    private void saveIdents(
            List<String> pins,
            Rekvireringsstatus status,
            String rekvirertAv) {

        pins.forEach(fnr ->
                identRepository.save(
                        IdentGeneratorUtil.createIdent(fnr, status, rekvirertAv)));
    }

    private Page<Ident> findPage(HentIdenterRequest request, Rekvireringsstatus rekvireringsstatus, int page) {
        return identRepository.findAll(
                rekvireringsstatus, request.getIdenttype(), request.getKjoenn(), request.getFoedtFoer(),
                request.getFoedtEtter(), isTrue(request.getSyntetisk()), PageRequest.of(page, request.getAntall()));
    }

    private static boolean isSyntetisk(String ident) {
        return ident.charAt(2) > '3';
    }
}
