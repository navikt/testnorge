package no.nav.registre.testnorge.identservice.service;

import no.nav.registre.testnorge.identservice.testdata.FiltrerPaaIdenterTilgjengeligIMiljo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SjekkIdenterService {

    @Autowired
    private FiltrerPaaIdenterTilgjengeligIMiljo filtrerPaaIdenterTilgjengeligIMiljo;

    public Set<String> finnLedigeIdenter(List<String> identListe) {
        Set<String> ukjenteIdenter = new HashSet<>(identListe);

        return filtrerPaaIdenterTilgjengeligIMiljo.filtrerPaaIdenter(ukjenteIdenter);
    }
}
