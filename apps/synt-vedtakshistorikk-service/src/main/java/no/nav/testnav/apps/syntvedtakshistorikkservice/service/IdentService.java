package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.KontoinfoResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {

    public List<String> getUtvalgteIdenterIAldersgruppe(
            int antallNyeIdenter,
            int minimumAlder,
            int maksimumAlder,
            LocalDate tidligsteDatoBosatt

    ) {
        return Collections.emptyList();
    }

    public List<String> getUtvalgteIdenterIAldersgruppeMedBarnUnder18(
            int antallNyeIdenter,
            int minimumAlder,
            int maksimumAlder,
            LocalDate tidligsteDatoBosatt,
            LocalDate tidligsteDatoBarnetillegg
    ) {
        return Collections.emptyList();
    }

    public List<KontoinfoResponse> getIdenterMedKontoinformasjon(
            int antall
    ) {
        return Collections.emptyList();
    }
}
