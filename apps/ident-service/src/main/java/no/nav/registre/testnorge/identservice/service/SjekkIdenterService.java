package no.nav.registre.testnorge.identservice.service;

import no.nav.registre.testnorge.identservice.testdata.FiltrerPaaIdenterTilgjengeligIMiljo;
import no.nav.registre.testnorge.identservice.testdata.response.IdentMedStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSetWithExpectedSize;

@Service
public class SjekkIdenterService {

    private static final String GYLDIG_OG_LEDIG = "Gyldig og ledig";

    @Autowired
    private FiltrerPaaIdenterTilgjengeligIMiljo filtrerPaaIdenterTilgjengeligIMiljo;

    public Set<IdentMedStatus> finnLedigeIdenter(String ident) {
        Set<String> ukjenteIdenter = new HashSet<>();

        ukjenteIdenter.add(ident);
        Set<String> filtrerteIdenter = filtrerPaaIdenterTilgjengeligIMiljo.filtrerPaaIdenter(ukjenteIdenter);
        Map<String, String> identerMedStatus = new HashMap<>();
        filtrerteIdenter.forEach(filtrertIdent -> identerMedStatus.put(filtrertIdent, GYLDIG_OG_LEDIG));

        return mapToIdentMedStatusSet(identerMedStatus);
    }

    protected Set<IdentMedStatus> mapToIdentMedStatusSet(Map<String, String> identer) {

        Set<IdentMedStatus> identerMedStatus = newHashSetWithExpectedSize(identer.size());

        for (Map.Entry<String, String> entry : identer.entrySet()) {
            identerMedStatus.add(new IdentMedStatus(entry.getKey(), entry.getValue()));
        }
        return identerMedStatus;
    }
}
