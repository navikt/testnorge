package no.nav.identpool.ident.service;

import static no.nav.identpool.ident.domain.Rekvireringsstatus.I_BRUK;
import static no.nav.identpool.ident.domain.Rekvireringsstatus.LEDIG;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.ident.ajourhold.service.IdentDBService;
import no.nav.identpool.ident.ajourhold.tps.generator.IdentGenerator;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Rekvireringsstatus;
import no.nav.identpool.ident.exception.ForFaaLedigeIdenterException;
import no.nav.identpool.ident.exception.IdentAlleredeIBrukException;
import no.nav.identpool.ident.exception.UgyldigPersonidentifikatorException;
import no.nav.identpool.ident.repository.IdentEntity;
import no.nav.identpool.ident.repository.IdentPredicateUtil;
import no.nav.identpool.ident.repository.IdentRepository;
import no.nav.identpool.ident.rest.v1.HentIdenterRequest;
import no.nav.identpool.ident.rest.v1.MarkerBruktRequest;
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

    public List<String> finnIdenter(HentIdenterRequest hentIdenterRequest) throws ForFaaLedigeIdenterException {

        List<String> personidentifikatorList = new ArrayList<>();

        Iterable<IdentEntity> identEntities = identRepository.findAll(identPredicateUtil.lagPredicateFraRequest(hentIdenterRequest), hentIdenterRequest.getPageable());

        identEntities.forEach(i -> personidentifikatorList.add(i.getPersonidentifikator()));

        int antallManglendeIdenter = hentIdenterRequest.getAntall() - personidentifikatorList.size();

        if (antallManglendeIdenter > MAKS_ANTALL_MANGLENDE_IDENTER) {
            throw new ForFaaLedigeIdenterException("Antall etterspurte identer er større enn tilgjengelig antall. Reduser antallet med " +
                    (antallManglendeIdenter - MAKS_ANTALL_MANGLENDE_IDENTER) +
                    ", for å få opprettet identene i TPS, eller vent til ident-pool får generert flere.");
        }

        List<String> temp = new ArrayList<>();
        for (int i = 1; i < MAKS_ANTALL_KALL_MOT_TPS && antallManglendeIdenter != temp.size(); i++) {

            List<String> genererteIdenter = IdentGenerator.genererIdenter(hentIdenterRequest);

            // filtrer vekk eksisterende
            List<String> finnesIkkeAllerede = genererteIdenter.stream().filter(ident -> !identRepository.existsByPersonidentifikator(ident)).collect(Collectors.toList());

            Map<String, Boolean> kontrollerteIdenter = identMQService.finnesITps(finnesIkkeAllerede);

            identDBService.lagreIdenter(kontrollerteIdenter.entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList()), I_BRUK, "TPS");

            temp.addAll(kontrollerteIdenter.entrySet().stream()
                    .filter(x -> !x.getValue())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList())
            );
        }

        if (temp.size() >= antallManglendeIdenter) {

            identDBService.lagreIdenter(temp.subList(0, antallManglendeIdenter), I_BRUK, hentIdenterRequest.getRekvirertAv());
            personidentifikatorList.addAll(temp.subList(0, antallManglendeIdenter));
            if (temp.size() > antallManglendeIdenter) {
                identDBService.lagreIdenter(temp.subList(antallManglendeIdenter, temp.size()), LEDIG, null);
            }
        } else {
            throw new ForFaaLedigeIdenterException("Det er for få ledige identer i TPS - prøv med et annet dato-intervall.");
        }

        identEntities.forEach(i -> i.setRekvireringsstatus(I_BRUK));
        identEntities.forEach(i -> i.setRekvirertAv(hentIdenterRequest.getRekvirertAv()));
        identRepository.saveAll(identEntities);
        return personidentifikatorList;
    }

    public Boolean erLedig(String personidentifikator) {

        IdentEntity ident = identRepository.findTopByPersonidentifikator(personidentifikator);
        if (ident != null) {
            return ident.getRekvireringsstatus().equals(Rekvireringsstatus.LEDIG) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            boolean exists = identMQService.finnesITps(Collections.singletonList(personidentifikator)).get(personidentifikator);
            Rekvireringsstatus status = exists ? I_BRUK : LEDIG;
            IdentEntity newIdentEntity = IdentEntity.builder()
                    .identtype(PersonidentifikatorUtil.getPersonidentifikatorType(personidentifikator))
                    .personidentifikator(personidentifikator)
                    .rekvireringsstatus(status)
                    .kjoenn(PersonidentifikatorUtil.getKjonn(personidentifikator))
                    .finnesHosSkatt("0")
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
                    .finnesHosSkatt("0")
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
        throw new IllegalStateException("Denne feilen skal ikke kunne forekomme.");
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
            identEntity.setFinnesHosSkatt("1");
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
                    .finnesHosSkatt("1")
                    .build();
        }
        identRepository.save(identEntity);
    }
}
