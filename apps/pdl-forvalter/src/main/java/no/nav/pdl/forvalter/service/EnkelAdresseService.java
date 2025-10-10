package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.testnav.libs.data.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.KontaktadresseDTO.PostboksadresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.UtenlandskAdresseIFrittFormatDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static no.nav.testnav.libs.data.pdlforvalter.v1.DbVersjonDTO.Master.FREG;
import static no.nav.testnav.libs.data.pdlforvalter.v1.DbVersjonDTO.Master.PDL;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class EnkelAdresseService {

    private static final String POSTBOKS_ADRESSE_EIER = "SOT6 Vika";
    private static final String POSTBOKS_ADRESSE_POSTBOKS = "2094";
    private static final String POSTBOKS_ADRESSE_POSTNUMMER = "0125";

    private static final String ADRESSE_NAVN_NUMMER = "1KOLEJOWA 6/5";
    private static final String ADRESSE_BY_STED = "18-500 KOLNO";
    private static final String ADRESSE_3_UTLAND = "CAPITAL WEST";
    private static final String ADRESSE_POSTKODE = "3000";

    private final KodeverkConsumer kodeverkConsumer;

    private final MapperFacade mapperFacade;

    public static KontaktadresseDTO getStrengtFortroligKontaktadresse() {

        return KontaktadresseDTO.builder()
                .id(1)
                .postboksadresse(PostboksadresseDTO.builder()
                        .postbokseier(POSTBOKS_ADRESSE_EIER)
                        .postboks(POSTBOKS_ADRESSE_POSTBOKS)
                        .postnummer(POSTBOKS_ADRESSE_POSTNUMMER)
                        .build())
                .master(FREG)
                .kilde("Dolly")
                .build();
    }

    public UtenlandskAdresseDTO getUtenlandskAdresse(UtenlandskAdresseDTO utenlandskAdresse, String landkode, DbVersjonDTO.Master master) {

        if (utenlandskAdresse.isEmpty()) {

            return UtenlandskAdresseDTO.builder()
                    .adressenavnNummer(ADRESSE_NAVN_NUMMER)
                    .regionDistriktOmraade(master == PDL ? ADRESSE_BY_STED : null)
                    .bySted(ADRESSE_3_UTLAND)
                    .postkode(ADRESSE_POSTKODE)
                    .landkode(getLandkode(isNotBlank(utenlandskAdresse.getLandkode()) ?
                            utenlandskAdresse.getLandkode() : landkode))
                    .build();

        } else {

            var oppdatertAdresse = mapperFacade.map(utenlandskAdresse, UtenlandskAdresseDTO.class);

            if (isBlank(oppdatertAdresse.getLandkode())) {
                oppdatertAdresse.setLandkode(getLandkode(landkode));
            }

            return oppdatertAdresse;
        }
    }

    public UtenlandskAdresseIFrittFormatDTO getUtenlandskAdresse(UtenlandskAdresseIFrittFormatDTO utenlandskAdresse, String landkode) {

        if (utenlandskAdresse.isEmpty()) {

            return UtenlandskAdresseIFrittFormatDTO.builder()
                    .adresselinjer(List.of(ADRESSE_NAVN_NUMMER, ADRESSE_BY_STED))
                    .postkode(ADRESSE_POSTKODE)
                    .byEllerStedsnavn(ADRESSE_3_UTLAND)
                    .landkode(getLandkode(isNotBlank(utenlandskAdresse.getLandkode()) ?
                            utenlandskAdresse.getLandkode() : landkode))
                    .build();

        } else {
            var oppdatertAdresse = mapperFacade.map(utenlandskAdresse, UtenlandskAdresseIFrittFormatDTO.class);

            if (isBlank(oppdatertAdresse.getLandkode())) {
                oppdatertAdresse.setLandkode(getLandkode(landkode));
            }

            return oppdatertAdresse;
        }
    }

    private String getLandkode(String landkode) {

        return isNotBlank(landkode) && !"NOR".equals(landkode) ? landkode :
                kodeverkConsumer.getTilfeldigLand();
    }
}