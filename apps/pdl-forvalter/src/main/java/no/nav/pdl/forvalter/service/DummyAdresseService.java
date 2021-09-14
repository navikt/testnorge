package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.utils.TilfeldigLandService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO.PostboksadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class DummyAdresseService {

    private static final String POSTBOKS_ADRESSE_EIER = "SOT6 Vika";
    private static final String POSTBOKS_ADRESSE_POSTBOKS = "2094";
    private static final String POSTBOKS_ADRESSE_POSTNUMMER = "0125";

    private static final String ADRESSE_NAVN_NUMMER = "1KOLEJOWA 6/5";
    private static final String ADRESSE_BY_STED = "18-500 KOLNO";
    private static final String ADRESSE_3_UTLAND = "CAPITAL WEST";
    private static final String ADRESSE_POSTKODE = "3000";

    private final TilfeldigLandService tilfeldigLandService;

    public static KontaktadresseDTO getStrengtFortroligKontaktadresse() {

        return KontaktadresseDTO.builder()
                .id(1)
                .postboksadresse(PostboksadresseDTO.builder()
                        .postbokseier(POSTBOKS_ADRESSE_EIER)
                        .postboks(POSTBOKS_ADRESSE_POSTBOKS)
                        .postnummer(POSTBOKS_ADRESSE_POSTNUMMER)
                        .build())
                .build();
    }

    public UtenlandskAdresseDTO getUtenlandskAdresse(String landkode) {

        return UtenlandskAdresseDTO.builder()
                .adressenavnNummer(ADRESSE_NAVN_NUMMER)
                .distriktnavn(ADRESSE_BY_STED)
                .region(ADRESSE_3_UTLAND)
                .postkode(ADRESSE_POSTKODE)
                .landkode(isNotBlank(landkode) ? landkode : tilfeldigLandService.getLand())
                .build();
    }
}