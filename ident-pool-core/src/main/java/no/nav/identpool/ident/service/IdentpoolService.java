package no.nav.identpool.ident.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Rekvireringsstatus;
import no.nav.identpool.ident.exception.IdentAlleredeIBrukException;
import no.nav.identpool.ident.repository.IdentEntity;
import no.nav.identpool.ident.repository.IdentPredicateUtil;
import no.nav.identpool.ident.repository.IdentRepository;
import no.nav.identpool.ident.rest.v1.FinnesHosSkattRequest;
import no.nav.identpool.ident.rest.v1.HentIdenterRequest;
import no.nav.identpool.ident.rest.v1.MarkerBruktRequest;

@Service
@RequiredArgsConstructor
public class IdentpoolService {
    private final IdentRepository identRepository;
    private final IdentPredicateUtil identPredicateUtil;

    public List<String> findIdents(HentIdenterRequest hentIdenterRequest) {

        List<String> personidentifikatorList = new ArrayList<>();

        Iterable<IdentEntity> identEntities = identRepository.findAll(identPredicateUtil.lagPredicateFraRequest(hentIdenterRequest));
        identEntities.forEach(i -> personidentifikatorList.add(i.getPersonidentifikator()));
        identEntities.forEach(i -> i.setRekvireringsstatus(Rekvireringsstatus.I_BRUK));
        identRepository.saveAll(identEntities);

        return personidentifikatorList;
    }

    public Boolean erLedig(String personidentifkator) {
        return identRepository.findTopByPersonidentifikator(personidentifkator).getRekvireringsstatus().equals(Rekvireringsstatus.LEDIG) ? Boolean.TRUE : Boolean.FALSE;
    }

    public String markerBrukt(MarkerBruktRequest markerBruktRequest) throws IdentAlleredeIBrukException {
        IdentEntity identEntity = identRepository.findTopByPersonidentifikator(markerBruktRequest.getPersonidentifikator());
        if (identEntity == null) {
            //implement generering av ident og sett identen til brukt.
        } else if (identEntity.getRekvireringsstatus().equals(Rekvireringsstatus.LEDIG)) {
            identEntity.setRekvireringsstatus(Rekvireringsstatus.I_BRUK);
            identEntity.setRekvirertAv(markerBruktRequest.getBruker());
            identRepository.save(identEntity);
            return "ok";
        } else if (identEntity.getRekvireringsstatus().equals(Rekvireringsstatus.I_BRUK)) {
            throw new IdentAlleredeIBrukException("Den etterspurte identen er allerede markert som brukt.");
        }
        throw new IllegalStateException("Denne feilen skal ikke kunne forekomme.");
    }

    public IdentEntity lesInnhold(String personidentifikator) {
        return identRepository.findTopByPersonidentifikator(personidentifikator);
    }

    public void registrerFinnesHosSkatt(FinnesHosSkattRequest finnesHosSkattRequest) {
        IdentEntity identEntity = identRepository.findTopByPersonidentifikator(finnesHosSkattRequest.getPersonidentifikator());
        if (identEntity != null) {
            identEntity.setFinnesHosSkatt("1");
        } else {
            identEntity = IdentEntity.builder()
                    .identtype(Identtype.DNR)
                    .personidentifikator(finnesHosSkattRequest.getPersonidentifikator())
                    .foedselsdato(finnesHosSkattRequest.getFoedselsdato())
                    .rekvireringsstatus(Rekvireringsstatus.I_BRUK)
                    .finnesHosSkatt("1")
                    .build();
        }
        identRepository.save(identEntity);
    }
}
