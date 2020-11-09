package no.nav.registre.testnorge.identservice.service;

import no.nav.registre.testnorge.identservice.testdata.FiltrerPaaIdenterTilgjengeligIMiljo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class SjekkIdenterService {

    @Autowired
    private FiltrerPaaIdenterTilgjengeligIMiljo filtrerPaaIdenterTilgjengeligIMiljo;

    public ResponseEntity<Set<String>> finnLedigeIdenter(String ident) {
        Set<String> ukjenteIdenter = new HashSet<>();
        ukjenteIdenter.add(ident);

        return ResponseEntity.ok(filtrerPaaIdenterTilgjengeligIMiljo.filtrerPaaIdenter(ukjenteIdenter));
    }
}
