package no.nav.dolly.service;

import static java.util.Collections.singletonList;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;

@Slf4j
@Service
public class PersonService {

    @Autowired
    private TpsfService tpsfService;

    @Autowired
    private TestgruppeService testgruppeService;

    @Autowired
    private List<ClientRegister> clientRegister;

    public void recyclePersoner(List<String> identer) {

        if (!identer.isEmpty()) {
            tpsfService.deletePersones(identer);
        }
    }

    public void recyclePerson(String ident) {

        recyclePersoner(singletonList(ident));
    }

    public void recyclePersonerIGruppe(Long gruppeId) {

        Testgruppe testgruppe = testgruppeService.fetchTestgruppeById(gruppeId);
        recyclePersoner(testgruppe.getTestidenter().stream().map(Testident::getIdent).collect(Collectors.toList()));
    }

    @Async
    public void releaseArtifacts(Long gruppeId) {

        Testgruppe testgruppe = testgruppeService.fetchTestgruppeById(gruppeId);
        releaseArtifactsInternal(testgruppe.getTestidenter().stream().map(Testident::getIdent).collect(Collectors.toList()));
    }

    @Async
    public void releaseArtifacts(List<String> identer) {

        releaseArtifactsInternal(identer);
    }

    private void releaseArtifactsInternal(List<String> identer) {

        clientRegister.forEach(register -> {

            try {
                register.release(identer);

            } catch (RuntimeException e) {
                log.error("Feilet å slette fra register, {}", e.getMessage(), e);
            }
        });
    }
}