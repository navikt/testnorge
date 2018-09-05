package no.nav.identpool.ident.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Rekvireringsstatus;
import no.nav.identpool.ident.exception.IdentAlleredeIBrukException;
import no.nav.identpool.ident.repository.IdentEntity;
import no.nav.identpool.ident.repository.IdentRepository;

@Service
@AllArgsConstructor
public class IdentpoolService {
    private IdentRepository identRepository;

    public List<String> findIdents(Identtype identtype, Pageable pageable) {
        List<IdentEntity> identEntityList;
        if (identtype.equals(Identtype.DNR)) {
            identEntityList = identRepository.findByRekvireringsstatusAndIdenttype(Rekvireringsstatus.LEDIG, Identtype.DNR, pageable);
        } else if (identtype.equals(Identtype.FNR)) {
            identEntityList = identRepository.findByRekvireringsstatusAndIdenttype(Rekvireringsstatus.LEDIG, Identtype.FNR, pageable);
        } else {
            identEntityList = identRepository.findByRekvireringsstatus(Rekvireringsstatus.LEDIG, pageable);
        }
        return identEntityList.stream().map(IdentEntity::getPersonidentifikator).collect(Collectors.toList());
    }

    public Boolean erLedig(String personidentifkator) {
        return identRepository.findTopByPersonidentifikator(personidentifkator).getRekvireringsstatus().equals(Rekvireringsstatus.LEDIG) ? Boolean.TRUE : Boolean.FALSE;
    }

    public String markerBrukt(String personidentifikator) throws IdentAlleredeIBrukException {
        IdentEntity identEntity = identRepository.findTopByPersonidentifikator(personidentifikator);
        if (identEntity == null) {
            //implement generering av ident og sett identen til brukt.
        } else if (identEntity.getRekvireringsstatus().equals(Rekvireringsstatus.LEDIG)) {
            identEntity.setRekvireringsstatus(Rekvireringsstatus.I_BRUK);
            identRepository.save(identEntity);
            return "ok";
        } else if (identEntity.getRekvireringsstatus().equals(Rekvireringsstatus.I_BRUK)) {
            throw new IdentAlleredeIBrukException("Den etterspurte identen er allerede markert som brukt.");
        }
        throw new IllegalStateException("Denne feilen skal ikke kunne forekomme.");
    }
}
