package no.nav.dolly.service;

import static java.util.Collections.singletonList;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;

@Service
public class PersonService {

    @Autowired
    private TpsfService tpsfService;

    @Autowired
    private TestgruppeService testgruppeService;

    public void recyclePersoner(List<String> identer) {

        tpsfService.deletePersoner(identer);
    }

    public void recyclePerson(String ident) {

        recyclePersoner(singletonList(ident));
    }

    public void recyclePersonerIGruppe(Long gruppeId) {

        Testgruppe testgruppe = testgruppeService.fetchTestgruppeById(gruppeId);
        recyclePersoner(testgruppe.getTestidenter().stream().map(Testident::getIdent).collect(Collectors.toList()));
    }
}
