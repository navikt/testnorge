package no.nav.registre.testnorge.identservice.service;

import no.nav.registre.testnorge.identservice.testdata.FiltrerPaaIdenterTilgjengeligIMiljo;
import no.nav.registre.testnorge.identservice.testdata.response.IdentMedStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSetWithExpectedSize;

@Service
public class SjekkIdenterService {

    private static final String LEDIG = "Ledig ident";

    @Autowired
    private FiltrerPaaIdenterTilgjengeligIMiljo filtrerPaaIdenterTilgjengeligIMiljo;

    public Set<IdentMedStatus> finnLedigeIdenter(List<String> identer) {

        Set<String> filtrerteIdenter = filtrerPaaIdenterTilgjengeligIMiljo.filtrerPaaIdenter(identer);
        Map<String, String> identerMedStatus = new HashMap<>();
        filtrerteIdenter.forEach(filtrertIdent -> identerMedStatus.putIfAbsent(filtrertIdent, LEDIG));

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
