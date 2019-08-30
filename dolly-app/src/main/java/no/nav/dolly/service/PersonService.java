package no.nav.dolly.service;

import static java.util.Collections.singletonList;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

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

        try {
            tpsfService.deletePersoner(identer);
        } catch (HttpClientErrorException error) {
            if (HttpStatus.NOT_FOUND.value() != error.getStatusCode().value()) {
                throw error;
            }
        }
    }

    public void recyclePerson(String ident) {

        recyclePersoner(singletonList(ident));
    }

    public void recyclePersonerIGruppe(Long gruppeId) {

        Testgruppe testgruppe = testgruppeService.fetchTestgruppeById(gruppeId);
        recyclePersoner(testgruppe.getTestidenter().stream().map(Testident::getIdent).collect(Collectors.toList()));
    }
}
