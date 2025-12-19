package no.nav.testnav.apps.syntvedtakshistorikkservice.service.util;

import com.google.common.io.Resources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.exception.ArbeidssoekerException;
import no.nav.testnav.libs.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.testnav.libs.dto.arena.testnorge.brukere.Kvalifiseringsgrupper;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArenaBrukerUtils {

    private static final Map<String, List<KodeMedSannsynlighet>> aktivitestsfaserMedInnsatsARBS;
    private static final Map<String, List<KodeMedSannsynlighet>> aktivitestsfaserMedInnsatsIARBS;
    private static final Map<String, List<KodeMedSannsynlighet>> aktivitestsfaserMedFormidlingsgruppe;

    static {
        aktivitestsfaserMedInnsatsARBS = new HashMap<>();
        aktivitestsfaserMedInnsatsIARBS = new HashMap<>();
        aktivitestsfaserMedFormidlingsgruppe = new HashMap<>();
        ObjectMapper objectMapper = JsonMapper.builder().build();
        try (var formidlingStream = Resources.getResource("files/aktfase_til_formidling.json").openStream();
             var arbsStream = Resources.getResource("files/ARBS_aktfase_til_innsats.json").openStream();
             var iarbsStream = Resources.getResource("files/IARBS_aktfase_til_innsats.json").openStream()) {
            Map<String, List<KodeMedSannsynlighet>> mapFormidling = objectMapper.readValue(formidlingStream, new TypeReference<>() {
            });
            Map<String, List<KodeMedSannsynlighet>> mapARBS = objectMapper.readValue(arbsStream, new TypeReference<>() {
            });
            Map<String, List<KodeMedSannsynlighet>> mapIARBS = objectMapper.readValue(iarbsStream, new TypeReference<>() {
            });

            aktivitestsfaserMedFormidlingsgruppe.putAll(mapFormidling);
            aktivitestsfaserMedInnsatsARBS.putAll(mapARBS);
            aktivitestsfaserMedInnsatsIARBS.putAll(mapIARBS);
        } catch (IOException e) {
            log.error("Kunne ikke laste inn aktivitetsfase fordeling(er).", e);
        }
    }

    private final ServiceUtils serviceUtils;

    public Kvalifiseringsgrupper velgKvalifiseringsgruppeBasertPaaFormidlingsgruppe(String aktivitetsfase, String formidlingsgruppe) {
        if (formidlingsgruppe.equals("IARBS")) {
            return velgKvalifiseringsgruppeBasertPaaAktivitetsfase(aktivitetsfase, aktivitestsfaserMedInnsatsIARBS);
        } else {
            return velgKvalifiseringsgruppeBasertPaaAktivitetsfase(aktivitetsfase, aktivitestsfaserMedInnsatsARBS);
        }
    }

    public String velgFormidlingsgruppeBasertPaaAktivitetsfase(String aktivitetsfase) {
        if (aktivitestsfaserMedFormidlingsgruppe.containsKey(aktivitetsfase)) {
            return serviceUtils.velgKodeBasertPaaSannsynlighet(aktivitestsfaserMedFormidlingsgruppe.get(aktivitetsfase)).getKode();
        } else {
            throw new ArbeidssoekerException("Ukjent aktivitetsfase " + aktivitetsfase);
        }
    }

    public static List<String> hentIdentListe(
            List<Arbeidsoeker> arbeidsoekere
    ) {
        if (arbeidsoekere.isEmpty()) {
            return new ArrayList<>();
        }

        return arbeidsoekere.stream().map(Arbeidsoeker::getPersonident).toList();
    }

    private Kvalifiseringsgrupper velgKvalifiseringsgruppeBasertPaaAktivitetsfase(
            String aktivitetsfase,
            Map<String, List<KodeMedSannsynlighet>> aktivitestsfaserMedInnsats) {
        if (aktivitestsfaserMedInnsats.containsKey(aktivitetsfase)) {
            var innsats = serviceUtils.velgKodeBasertPaaSannsynlighet(aktivitestsfaserMedInnsats.get(aktivitetsfase)).getKode();
            return Kvalifiseringsgrupper.valueOf(innsats);
        } else {
            throw new ArbeidssoekerException("Ukjent aktivitetsfase " + aktivitetsfase);
        }
    }
}
