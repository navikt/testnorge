package no.nav.identpool.service;

import static no.nav.identpool.domain.Rekvireringsstatus.I_BRUK;
import static no.nav.identpool.domain.Rekvireringsstatus.LEDIG;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.ajourhold.service.IdentDBService;
import no.nav.identpool.ajourhold.tps.generator.IdentGenerator;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.exception.ForFaaLedigeIdenterException;
import no.nav.identpool.exception.IdentAlleredeIBrukException;
import no.nav.identpool.exception.UgyldigPersonidentifikatorException;
import no.nav.identpool.repository.IdentEntity;
import no.nav.identpool.repository.IdentPredicateUtil;
import no.nav.identpool.repository.IdentRepository;
import no.nav.identpool.rs.v1.support.HentIdenterRequest;
import no.nav.identpool.rs.v1.support.MarkerBruktRequest;
import no.nav.identpool.util.PersonidentifikatorUtil;

@Service
@RequiredArgsConstructor
public class IdentpoolService {
    private static final int MAKS_ANTALL_MANGLENDE_IDENTER = 80;
    private static final int MAKS_ANTALL_KALL_MOT_TPS = 3;
    private final IdentRepository identRepository;
    private final IdentPredicateUtil identPredicateUtil;
    private final IdentMQService identMQService;
    private final IdentDBService identDBService;

    //TODO Rydd og del opp litt mer
    public List<String> rekvirer(HentIdenterRequest hentIdenterRequest) throws ForFaaLedigeIdenterException {

        List<String> personidentifikatorList = new ArrayList<>();

        Iterable<IdentEntity> identEntities = identRepository.findAll(
                identPredicateUtil.lagPredicateFraRequest(hentIdenterRequest),
                hentIdenterRequest.getPageable()
        );

        identEntities.forEach(i -> personidentifikatorList.add(i.getPersonidentifikator()));

        int antallManglendeIdenter = hentIdenterRequest.getAntall() - personidentifikatorList.size();

        if (antallManglendeIdenter > MAKS_ANTALL_MANGLENDE_IDENTER) {
            String errMsg = "Antall etterspurte identer er større enn tilgjengelig antall. Reduser antallet med %s, " +
                    "for å få opprettet identene i TPS, eller vent til ident-pool får generert flere.";
            throw new ForFaaLedigeIdenterException(String.format(errMsg, (antallManglendeIdenter - MAKS_ANTALL_MANGLENDE_IDENTER)));
        }

        List<String> temp = hentIdenterFraTps(hentIdenterRequest, antallManglendeIdenter);

        if (temp.size() >= antallManglendeIdenter) {

            identDBService.saveIdents(temp.subList(0, antallManglendeIdenter), I_BRUK, hentIdenterRequest.getRekvirertAv());
            personidentifikatorList.addAll(temp.subList(0, antallManglendeIdenter));
            if (temp.size() > antallManglendeIdenter) {
                identDBService.saveIdents(temp.subList(antallManglendeIdenter, temp.size()), LEDIG, null);
            }
        } else {
            throw new ForFaaLedigeIdenterException("Det er for få ledige identer i TPS - prøv med et annet dato-intervall.");
        }

        identEntities.forEach(i -> i.setRekvireringsstatus(I_BRUK));
        identEntities.forEach(i -> i.setRekvirertAv(hentIdenterRequest.getRekvirertAv()));
        identRepository.saveAll(identEntities);
        return personidentifikatorList;
    }

    private List<String> hentIdenterFraTps(HentIdenterRequest hentIdenterRequest, int antallManglendeIdenter) {
        List<String> temp = new ArrayList<>();
        for (int i = 1; i < MAKS_ANTALL_KALL_MOT_TPS && antallManglendeIdenter != temp.size(); i++) {

            List<String> genererteIdenter = IdentGenerator.genererIdenter(hentIdenterRequest);

            // filtrer vekk eksisterende
            List<String> finnesIkkeAllerede = genererteIdenter.stream().filter(ident -> !identRepository.existsByPersonidentifikator(ident)).collect(Collectors.toList());

            Map<String, Boolean> kontrollerteIdenter = identMQService.checkInTps(finnesIkkeAllerede);

            identDBService.saveIdents(kontrollerteIdenter.entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList()), I_BRUK, "TPS");

            temp.addAll(kontrollerteIdenter.entrySet().stream()
                    .filter(x -> !x.getValue())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList())
            );
        }
        return temp;
    }

    //TODO Denne gjøre mer enn metodenavnet tilsier
    public Boolean erLedig(String personidentifikator) {
        IdentEntity ident = identRepository.findTopByPersonidentifikator(personidentifikator);
        if (ident != null) {
            return ident.getRekvireringsstatus().equals(Rekvireringsstatus.LEDIG) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            boolean exists = identMQService.checkInTps(Collections.singletonList(personidentifikator)).get(personidentifikator);
            Rekvireringsstatus status = exists ? I_BRUK : LEDIG;
            IdentEntity newIdentEntity = IdentEntity.builder()
                    .identtype(PersonidentifikatorUtil.getPersonidentifikatorType(personidentifikator))
                    .personidentifikator(personidentifikator)
                    .rekvireringsstatus(status)
                    .kjoenn(PersonidentifikatorUtil.getKjonn(personidentifikator))
                    .finnesHosSkatt(false)
                    .foedselsdato(PersonidentifikatorUtil.toBirthdate(personidentifikator))
                    .build();
            identRepository.save(newIdentEntity);
            return !exists;
        }
    }

    public void markerBrukt(MarkerBruktRequest markerBruktRequest) throws IdentAlleredeIBrukException {
        IdentEntity identEntity = identRepository.findTopByPersonidentifikator(markerBruktRequest.getPersonidentifikator());
        if (identEntity == null) {
            String personidentifikator = markerBruktRequest.getPersonidentifikator();

            IdentEntity newIdentEntity = IdentEntity.builder()
                    .identtype(PersonidentifikatorUtil.getPersonidentifikatorType(personidentifikator))
                    .personidentifikator(personidentifikator)
                    .rekvireringsstatus(I_BRUK)
                    .rekvirertAv(markerBruktRequest.getBruker())
                    .finnesHosSkatt(false)
                    .kjoenn(PersonidentifikatorUtil.getKjonn(personidentifikator))
                    .foedselsdato(PersonidentifikatorUtil.toBirthdate(personidentifikator))
                    .build();
            identRepository.save(newIdentEntity);
            return;

        } else if (identEntity.getRekvireringsstatus().equals(Rekvireringsstatus.LEDIG)) {
            identEntity.setRekvireringsstatus(I_BRUK);
            identEntity.setRekvirertAv(markerBruktRequest.getBruker());
            identRepository.save(identEntity);
            return;
        } else if (identEntity.getRekvireringsstatus().equals(I_BRUK)) {
            throw new IdentAlleredeIBrukException("Den etterspurte identen er allerede markert som brukt.");
        }
        throw new IllegalStateException("Den etterspurte identen er ugyldig siden den hverken markert som i bruk eller ledig.");
    }

    public IdentEntity lesInnhold(String personidentifikator) {
        return identRepository.findTopByPersonidentifikator(personidentifikator);
    }

    public void registrerFinnesHosSkatt(String personidentifikator) throws UgyldigPersonidentifikatorException {

        if (!PersonidentifikatorUtil.getPersonidentifikatorType(personidentifikator).equals(Identtype.DNR)) {
            throw new UgyldigPersonidentifikatorException("personidentifikatoren er ikke et DNR");
        }

        IdentEntity identEntity = identRepository.findTopByPersonidentifikator(personidentifikator);
        if (identEntity != null) {
            identEntity.setFinnesHosSkatt(true);
            identEntity.setRekvireringsstatus(I_BRUK);
            identEntity.setRekvirertAv("DREK");
        } else {
            identEntity = IdentEntity.builder()
                    .identtype(Identtype.DNR)
                    .kjoenn(PersonidentifikatorUtil.getKjonn(personidentifikator))
                    .personidentifikator(personidentifikator)
                    .foedselsdato(PersonidentifikatorUtil.toBirthdate(personidentifikator))
                    .rekvireringsstatus(I_BRUK)
                    .rekvirertAv("DREK")
                    .finnesHosSkatt(true)
                    .build();
        }
        identRepository.save(identEntity);
    }
}
