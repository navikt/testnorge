package no.nav.registre.inst.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import no.nav.registre.inst.Institusjonsforholdsmelding;
import no.nav.registre.inst.consumer.rs.Inst2Consumer;

@Service
public class IdentService {

    @Autowired
    private Inst2Consumer inst2Consumer;

    public List<String> slettInstitusjonsforholdTilIdenter(List<String> identer) {
        Map<String, Object> tokenObject = inst2Consumer.hentTokenTilInst2();
        List<String> slettedeOppholdIder = new ArrayList<>();

        for (String ident : identer) {
            List<Institusjonsforholdsmelding> institusjonsforholdsmeldinger = hentInstitusjonsoppholdFraInst2(tokenObject, ident);
            for (Institusjonsforholdsmelding melding : institusjonsforholdsmeldinger) {
                ResponseEntity response = inst2Consumer.slettInstitusjonsoppholdFraInst2(tokenObject, melding.getOppholdId());
                if (response.getStatusCode().is2xxSuccessful()) {
                    slettedeOppholdIder.add(melding.getOppholdId());
                }
            }
        }

        return slettedeOppholdIder;
    }

    public List<Institusjonsforholdsmelding> hentInstitusjonsoppholdFraInst2(Map<String, Object> tokenObject, String ident) {
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
}
