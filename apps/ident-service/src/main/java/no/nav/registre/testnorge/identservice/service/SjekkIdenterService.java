package no.nav.registre.testnorge.identservice.service;

import lombok.SneakyThrows;
import no.nav.registre.testnorge.identservice.testdata.FiltrerPaaIdenterTilgjengeligIMiljo;
import no.nav.registre.testnorge.identservice.testdata.response.IdentMedStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class SjekkIdenterService {


    @Autowired
    private FiltrerPaaIdenterTilgjengeligIMiljo filtrerPaaIdenterTilgjengeligIMiljo;

    @SneakyThrows
    public ResponseEntity<IdentMedStatus> finnLedigeIdenter(String ident) {

        List<String> identer = new ArrayList<>();
        identer.add(ident);

        String identStatus = filtrerPaaIdenterTilgjengeligIMiljo.filtrerPaaIdenter(identer);
        Map.Entry<String, String> identMedStatus = Map.entry(ident, identStatus);

        return ResponseEntity.ok().body(mapToIdentMedStatusSet(identMedStatus));
    }

    protected IdentMedStatus mapToIdentMedStatusSet(Map.Entry<String, String> ident) {

        return new IdentMedStatus(ident.getKey(), isNotBlank(ident.getValue()) ? ident.getValue() : "Fant person i prod");
    }

}
