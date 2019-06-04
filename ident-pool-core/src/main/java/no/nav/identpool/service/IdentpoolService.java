package no.nav.identpool.service;

import static no.nav.identpool.domain.Rekvireringsstatus.I_BRUK;
import static no.nav.identpool.domain.Rekvireringsstatus.LEDIG;
import static no.nav.identpool.util.PersonidentUtil.getIdentType;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.nav.identpool.domain.Ident;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.domain.TpsStatus;
import no.nav.identpool.exception.ForFaaLedigeIdenterException;
import no.nav.identpool.exception.IdentAlleredeIBrukException;
import no.nav.identpool.exception.UgyldigPersonidentifikatorException;
import no.nav.identpool.repository.IdentRepository;
import no.nav.identpool.repository.WhitelistRepository;
import no.nav.identpool.rs.v1.support.HentIdenterRequest;
import no.nav.identpool.rs.v1.support.MarkerBruktRequest;
import no.nav.identpool.util.IdentGeneratorUtil;
import no.nav.identpool.util.IdentPredicateUtil;
import no.nav.identpool.util.PersonidentUtil;

@Service
@RequiredArgsConstructor
public class IdentpoolService {

    private static final int MAKS_ANTALL_MANGLENDE_IDENTER = 80;
    private static final int MAKS_ANTALL_KALL_MOT_TPS = 3;

    private final IdentRepository identRepository;
    private final IdentTpsService identTpsService;
    private final IdentGeneratorService identGeneratorService;
    private final WhitelistRepository whitelistRepository;

    public List<String> rekvirer(HentIdenterRequest request) throws ForFaaLedigeIdenterException {
        Iterable<Ident> identEntities = identRepository.findAll(
                IdentPredicateUtil.lagPredicateFraRequest(request),
                request.getPageable()
        );

        List<String> identList = StreamSupport.stream(identEntities.spliterator(), false)
                .map(Ident::getPersonidentifikator)
                .collect(Collectors.toList());

        int missingIdentCount = request.getAntall() - identList.size();

        if (missingIdentCount > MAKS_ANTALL_MANGLENDE_IDENTER) {
            String errMsg = "Antall etterspurte identer er større enn tilgjengelig antall. Reduser antallet med %s, " +
                    "for å få opprettet identene i TPS.";
            throw new ForFaaLedigeIdenterException(String.format(errMsg, (missingIdentCount - MAKS_ANTALL_MANGLENDE_IDENTER)));
        }

        if (missingIdentCount > 0) {
            List<String> newIdents = hentIdenterFraTps(request);
            if (newIdents.size() >= missingIdentCount) {

                saveIdents(newIdents.subList(0, missingIdentCount), I_BRUK, request.getRekvirertAv());
                identList.addAll(newIdents.subList(0, missingIdentCount));
                if (newIdents.size() > missingIdentCount) {
                    saveIdents(newIdents.subList(missingIdentCount, newIdents.size()), LEDIG, null);
                }
            } else {
                throw new ForFaaLedigeIdenterException("Det er for få ledige identer i TPS - prøv med et annet dato-intervall.");
            }
        }

        //Sier at request har tatt disse i bruk
        identEntities.forEach(i -> {
            i.setRekvireringsstatus(I_BRUK);
            i.setRekvirertAv(request.getRekvirertAv());
        });
        identRepository.saveAll(identEntities);

        return identList;
    }

    public Boolean erLedig(String personidentifikator) {
        Ident ident = identRepository.findTopByPersonidentifikator(personidentifikator);
        if (ident != null) {
            return ident.getRekvireringsstatus().equals(Rekvireringsstatus.LEDIG) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            boolean exists = identTpsService.checkIdentsInTps(Collections.singletonList(personidentifikator)).isEmpty();
            Rekvireringsstatus status = exists ? I_BRUK : LEDIG;
            Ident newIdent = Ident.builder()
                    .identtype(getIdentType(personidentifikator))
                    .personidentifikator(personidentifikator)
                    .rekvireringsstatus(status)
                    .kjoenn(PersonidentUtil.getKjonn(personidentifikator))
                    .finnesHosSkatt(false)
                    .foedselsdato(PersonidentUtil.toBirthdate(personidentifikator))
                    .build();
            identRepository.save(newIdent);
            return !exists;
        }
    }

    public void markerBrukt(MarkerBruktRequest markerBruktRequest) throws IdentAlleredeIBrukException {
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

    public Ident lesInnhold(String personidentifikator) {
        return identRepository.findTopByPersonidentifikator(personidentifikator);
    }

    public void registrerFinnesHosSkatt(String personidentifikator) throws UgyldigPersonidentifikatorException {

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
        identRepository.save(ident);
    }

    public List<String> hentLedigeFNRFoedtMellom(LocalDate from, LocalDate to) {
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

    private List<String> hentIdenterFraTps(HentIdenterRequest request) {
        Set<String> temp = new HashSet<>();
        for (int i = 1; i < MAKS_ANTALL_KALL_MOT_TPS && temp.size() < request.getAntall(); i++) {

            List<String> genererteIdenter = identGeneratorService.genererIdenter(request);

            // filtrer vekk eksisterende
            List<String> finnesIkkeAllerede = genererteIdenter.stream()
                    .filter(ident -> !identRepository.existsByPersonidentifikator(ident))
                    .collect(Collectors.toList());

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

    private void saveIdents(List<String> pins, Rekvireringsstatus status, String rekvirertAv) {
        identRepository.saveAll(pins.stream()
                .map(fnr -> IdentGeneratorUtil.createIdent(fnr, status, rekvirertAv))
                .collect(Collectors.toList()));
    }
}
