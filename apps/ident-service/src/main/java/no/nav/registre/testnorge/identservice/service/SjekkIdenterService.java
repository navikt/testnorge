package no.nav.registre.testnorge.identservice.service;

import lombok.SneakyThrows;
import no.nav.registre.testnorge.identservice.testdata.FiltrerPaaIdenterTilgjengeligIMiljo;
import no.nav.registre.testnorge.identservice.testdata.response.IdentMedStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class SjekkIdenterService {

    @Autowired
    private FiltrerPaaIdenterTilgjengeligIMiljo filtrerPaaIdenterTilgjengeligIMiljo;

    @SneakyThrows
    public ResponseEntity<IdentMedStatus> finnLedigeIdenter(String ident) {

        IdentMedStatus identMedStatus = filtrerPaaIdenterTilgjengeligIMiljo.filtrerPaaIdenter(ident);

        return ResponseEntity.ok().body(mapToIdentMedStatus(identMedStatus));
    }

    protected IdentMedStatus mapToIdentMedStatus(IdentMedStatus ident) {

        return new IdentMedStatus(ident.getIdent(),
                isNotBlank(ident.getStatus()) ? ident.getStatus() : "Fant person i prod",
                ident.getStatusCode());
    }
}
