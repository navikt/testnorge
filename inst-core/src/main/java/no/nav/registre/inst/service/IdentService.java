package no.nav.registre.inst.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.inst.Institusjonsforholdsmelding;
import no.nav.registre.inst.consumer.rs.Inst2Consumer;
import no.nav.registre.inst.provider.rs.responses.SletteOppholdResponse;

@Service
public class IdentService {

    @Autowired
    private Inst2Consumer inst2Consumer;

    public SletteOppholdResponse slettInstitusjonsforholdTilIdenter(List<String> identer) {
        Map<String, Object> tokenObject = inst2Consumer.hentTokenTilInst2();

        SletteOppholdResponse sletteOppholdResponse = SletteOppholdResponse.builder()
                .identerMedOppholdIdSomIkkeKunneSlettes(new HashMap<>())
                .identerMedOppholdIdSomBleSlettet(new HashMap<>())
                .build();

        for (String ident : identer) {
            List<Institusjonsforholdsmelding> institusjonsforholdsmeldinger = hentForhold(tokenObject, ident);
            for (Institusjonsforholdsmelding melding : institusjonsforholdsmeldinger) {
                ResponseEntity response = inst2Consumer.slettInstitusjonsoppholdFraInst2(tokenObject, melding.getOppholdId());
                if (response.getStatusCode().is2xxSuccessful()) {
                    leggTilIdentMedOppholdIResponse(sletteOppholdResponse.getIdenterMedOppholdIdSomBleSlettet(), ident, melding.getOppholdId());
                } else {
                    leggTilIdentMedOppholdIResponse(sletteOppholdResponse.getIdenterMedOppholdIdSomIkkeKunneSlettes(), ident, melding.getOppholdId());
                }
            }
        }

        return sletteOppholdResponse;
    }

    public Map<String, List<Institusjonsforholdsmelding>> hentInstitusjonsoppholdFraInst2(List<String> identer) {
        Map<String, Object> tokenObject = inst2Consumer.hentTokenTilInst2();
       return identer.parallelStream()
                .collect(Collectors.toMap(fnr -> fnr, fnr -> hentForhold(tokenObject, fnr)));
    }

    private List<Institusjonsforholdsmelding> hentForhold(Map<String, Object> tokenObject, String ident) {
        List<Institusjonsforholdsmelding> institusjonsforholdsmeldinger = inst2Consumer.hentInstitusjonsoppholdFraInst2(tokenObject, ident);
        if (institusjonsforholdsmeldinger != null) {
            for (Institusjonsforholdsmelding melding : institusjonsforholdsmeldinger) {
                melding.setPersonident(ident);
            }
            return new ArrayList<>(institusjonsforholdsmeldinger);
        } else {
            return new ArrayList<>();
        }
    }

    private void leggTilIdentMedOppholdIResponse(Map<String, List<String>> identMedOpphold, String ident, String oppholdId) {
        if (identMedOpphold.containsKey(ident)) {
            identMedOpphold.get(ident).add(oppholdId);
        } else {
            identMedOpphold.put(ident, new ArrayList<>(Collections.singletonList(oppholdId)));
        }
    }
}
