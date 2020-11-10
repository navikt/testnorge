package no.nav.registre.testnorge.identservice.service;

import lombok.SneakyThrows;
import no.nav.registre.testnorge.identservice.testdata.FiltrerPaaIdenterTilgjengeligIMiljo;
import no.nav.registre.testnorge.identservice.testdata.response.IdentMedStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSetWithExpectedSize;

@Service
public class SjekkIdenterService {


    @Autowired
    private FiltrerPaaIdenterTilgjengeligIMiljo filtrerPaaIdenterTilgjengeligIMiljo;

    @SneakyThrows
    public Set<IdentMedStatus> finnLedigeIdenter(String ident) {

        List<String> identer = new ArrayList<>();
        identer.add(ident);

        String identStatus = filtrerPaaIdenterTilgjengeligIMiljo.filtrerPaaIdenter(identer);
        Map<String, String> identerMedStatus = new HashMap<>();

        identerMedStatus.put(ident, identStatus);

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
