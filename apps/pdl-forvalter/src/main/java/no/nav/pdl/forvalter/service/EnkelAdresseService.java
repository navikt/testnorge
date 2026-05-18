package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO.PostboksadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseIFrittFormatDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master.FREG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master.PDL;
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

    public Mono<UtenlandskAdresseDTO> getUtenlandskAdresse(UtenlandskAdresseDTO utenlandskAdresse, String landkode, DbVersjonDTO.Master master) {

        if (utenlandskAdresse.isEmpty()) {

            return getLandkode(isNotBlank(utenlandskAdresse.getLandkode()) ?
                    utenlandskAdresse.getLandkode() : landkode)
                        .map(kode -> UtenlandskAdresseDTO.builder()
                                .adressenavnNummer(ADRESSE_NAVN_NUMMER)
                                .regionDistriktOmraade(master == PDL ? ADRESSE_BY_STED : null)
                                .bySted(ADRESSE_3_UTLAND)
                                .postkode(ADRESSE_POSTKODE)
                                .landkode(kode)
                                .build());
        } else {

            var oppdatertAdresse = mapperFacade.map(utenlandskAdresse, UtenlandskAdresseDTO.class);

            if (isBlank(oppdatertAdresse.getLandkode())) {
                return getLandkode(landkode)
                        .map(kode -> {
                            oppdatertAdresse.setLandkode(kode);
                            return oppdatertAdresse;
                        });
            }

            return Mono.just(oppdatertAdresse);
        }
    }

    public Mono<UtenlandskAdresseIFrittFormatDTO> getUtenlandskAdresse(UtenlandskAdresseIFrittFormatDTO utenlandskAdresse, String landkode) {

        if (utenlandskAdresse.isEmpty()) {

            return getLandkode(
                    isNotBlank(utenlandskAdresse.getLandkode()) ? utenlandskAdresse.getLandkode() : landkode)
                    .map(kode -> UtenlandskAdresseIFrittFormatDTO.builder()
                            .adresselinjer(List.of(ADRESSE_NAVN_NUMMER, ADRESSE_BY_STED))
                            .postkode(ADRESSE_POSTKODE)
                            .byEllerStedsnavn(ADRESSE_3_UTLAND)
                            .landkode(kode)
                            .build());
        } else {
            var oppdatertAdresse = mapperFacade.map(utenlandskAdresse, UtenlandskAdresseIFrittFormatDTO.class);

            if (isBlank(oppdatertAdresse.getLandkode())) {
                return getLandkode(landkode).map(kode -> {
                    oppdatertAdresse.setLandkode(kode);
                    return oppdatertAdresse;
                });
            }

            return Mono.just(oppdatertAdresse);
        }
    }

    private Mono<String> getLandkode(String landkode) {

        return isNotBlank(landkode) && !"NOR".equals(landkode) ? Mono.just(landkode) :
                kodeverkConsumer.getTilfeldigLand();
    }
}