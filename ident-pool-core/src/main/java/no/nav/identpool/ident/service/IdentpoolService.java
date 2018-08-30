package no.nav.identpool.ident.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Rekvireringsstatus;
import no.nav.identpool.ident.repository.IdentRepository;
import no.nav.identpool.ident.repository.IdentEntity;
import no.nav.identpool.ident.exception.IllegalIdenttypeException;

@Service
@AllArgsConstructor
public class IdentpoolService {
    private IdentRepository identRepository;

    public List<String> findIdents(Integer antall, Identtype identtype) throws IllegalIdenttypeException {
        List<IdentEntity> identEntityList;
        if (identtype.equals(Identtype.DNR)) {
            identEntityList = identRepository.findAllByRekvireringsstatusAndIdenttype(Rekvireringsstatus.LEDIG.getStatus(), Identtype.DNR.getType());
        } else if (identtype.equals(Identtype.FNR)) {
            identEntityList = identRepository.findAllByRekvireringsstatusAndIdenttype(Rekvireringsstatus.LEDIG.getStatus(), Identtype.FNR.getType());
        } else {
            identEntityList = identRepository.findAllByRekvireringsstatus(Rekvireringsstatus.LEDIG.getStatus());
        }
        return identEntityList.subList(0, antall).stream().map(IdentEntity::getPersonidentifikator).collect(Collectors.toList());
    }

    public Boolean erLedig(String personidentifkator) {
        return identRepository.findTopByPersonidentifikator(personidentifkator).getRekvireringsstatus().equals(Rekvireringsstatus.LEDIG) ? Boolean.TRUE : Boolean.FALSE;
    }
}
