package no.nav.identpool.ident.service;

import static no.nav.identpool.ident.domain.Rekvireringsstatus.I_BRUK;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.ident.ajourhold.service.IdentMQService;
import no.nav.identpool.ident.ajourhold.util.PersonIdentifikatorUtil;
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

@Service
@RequiredArgsConstructor
public class IdentpoolService {
    private final IdentRepository identRepository;
    private final IdentPredicateUtil identPredicateUtil;
    private final IdentMQService identMQService;

    public List<String> finnIdenter(HentIdenterRequest hentIdenterRequest) throws ForFaaLedigeIdenterException{

        List<String> personidentifikatorList = new ArrayList<>();

        Iterable<IdentEntity> identEntities = identRepository.findAll(identPredicateUtil.lagPredicateFraRequest(hentIdenterRequest));
        identEntities.forEach(i -> personidentifikatorList.add(i.getPersonidentifikator()));
        identEntities.forEach(i -> i.setRekvireringsstatus(I_BRUK));

        //todo: this
        int antallManglendeIdenter = hentIdenterRequest.getAntall()-personidentifikatorList.size();
        if (antallManglendeIdenter <= 80 && antallManglendeIdenter > 0 ) {
            // GÅ TIL TPS

        } else {
            throw new ForFaaLedigeIdenterException("Antall etterspurte identer er større enn tilgjengelig antall. Reduser antallet med " +
                    (antallManglendeIdenter - 80) +
                    ", for å få opprettet identene i TPS, eller vent til ident-pool får generert flere.");
        }

        identRepository.saveAll(identEntities);

        return personidentifikatorList;
    }

    public Boolean erLedig(String personidentifikator) {

        IdentEntity ident = identRepository.findTopByPersonidentifikator(personidentifikator);
        if (ident != null) {
            return ident.getRekvireringsstatus().equals(Rekvireringsstatus.LEDIG) ? Boolean.TRUE : Boolean.FALSE;
        } else if (!identMQService.fnrsExists(Collections.singletonList(personidentifikator)).get(personidentifikator)) {
            IdentEntity newIdentEntity = IdentEntity.builder()
                    .identtype(getPersonidentifikatorType(personidentifikator))
                    .personidentifikator(personidentifikator)
                    .rekvireringsstatus(Rekvireringsstatus.LEDIG)
                    .finnesHosSkatt("0")
                    .foedselsdato(PersonIdentifikatorUtil.toBirthdate(personidentifikator))
                    .build();
            identRepository.save(newIdentEntity);
            return true;
        } else {
            IdentEntity newIdentEntity = IdentEntity.builder()
                    .identtype(getPersonidentifikatorType(personidentifikator))
                    .personidentifikator(personidentifikator)
                    .rekvireringsstatus(I_BRUK)
                    .finnesHosSkatt("0")
                    .foedselsdato(PersonIdentifikatorUtil.toBirthdate(personidentifikator))
                    .build();
            identRepository.save(newIdentEntity);
            return false;
        }
    }

    public void markerBrukt(MarkerBruktRequest markerBruktRequest) throws IdentAlleredeIBrukException {
        IdentEntity identEntity = identRepository.findTopByPersonidentifikator(markerBruktRequest.getPersonidentifikator());
        if (identEntity == null) {
            String personidentifikator = markerBruktRequest.getPersonidentifikator();

            IdentEntity newIdentEntity = IdentEntity.builder()
                    .identtype(getPersonidentifikatorType(personidentifikator))
                    .personidentifikator(personidentifikator)
                    .rekvireringsstatus(I_BRUK)
                    .rekvirertAv(markerBruktRequest.getBruker())
                    .finnesHosSkatt("0")
                    .foedselsdato(PersonIdentifikatorUtil.toBirthdate(personidentifikator))
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

        if (getPersonidentifikatorType(personidentifikator).equals(Identtype.DNR)) {
            throw new UgyldigPersonidentifikatorException("personidentifikatoren er ikke et DNR");
        }

        IdentEntity identEntity = identRepository.findTopByPersonidentifikator(personidentifikator);
        if (identEntity != null) {
            identEntity.setFinnesHosSkatt("1");
            identEntity.setRekvireringsstatus(I_BRUK);
        } else {
            identEntity = IdentEntity.builder()
                    .identtype(Identtype.DNR)
                    .personidentifikator(personidentifikator)
                    .foedselsdato(PersonIdentifikatorUtil.toBirthdate(personidentifikator))
                    .rekvireringsstatus(I_BRUK)
                    .finnesHosSkatt("1")
                    .build();
        }
        identRepository.save(identEntity);
    }

    private Identtype getPersonidentifikatorType(String personidentifikator) {
        return Integer.parseInt(personidentifikator.substring(0, 1)) > 3 ? Identtype.DNR : Identtype.FNR;
    }
}
