package no.nav.dolly.bestilling.tpsbackporting.mapping;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsMidlertidigAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class KontaktadresseMappingStrategy implements MappingStrategy {

    private static LocalDateTime getGyldigTom(LocalDateTime timestamp) {

        if (isNull(timestamp)) {
            return LocalDateTime.now().plusYears(1);
        }
        return timestamp.isAfter(LocalDateTime.now().plusYears(1)) ? LocalDateTime.now().plusYears(1) : timestamp;
    }

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(KontaktadresseDTO.class, TpsfBestilling.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(KontaktadresseDTO source, TpsfBestilling target, MappingContext context) {

                        if (nonNull(source.getVegadresse())) {
                            target.setMidlertidigAdresse(RsMidlertidigAdresse.builder()
                                    .adressetype(RsMidlertidigAdresse.Adressetype.GATE)
                                    .norskAdresse(RsMidlertidigAdresse.NorskAdresse.builder()
                                            .gatenavn(source.getVegadresse().getAdressenavn())
                                            .gatekode(source.getVegadresse().getAdressekode())
                                            .husnr(source.getVegadresse().getHusnummer())
                                            .postnr(source.getVegadresse().getPostnummer())
                                            .matrikkelId(source.getAdresseIdentifikatorFraMatrikkelen())
                                            .build())
                                    .gyldigTom(getGyldigTom(source.getGyldigTilOgMed()))
                                    .build());

                        } else if (nonNull(source.getPostboksadresse())) {
                            target.setMidlertidigAdresse(RsMidlertidigAdresse.builder()
                                    .adressetype(RsMidlertidigAdresse.Adressetype.PBOX)
                                    .norskAdresse(RsMidlertidigAdresse.NorskAdresse.builder()
                                            .postboksAnlegg(source.getPostboksadresse().getPostbokseier())
                                            .postboksnr(source.getPostboksadresse().getPostboks())
                                            .postnr(source.getPostboksadresse().getPostnummer())
                                            .build())
                                    .gyldigTom(getGyldigTom(source.getGyldigTilOgMed()))
                                    .build());

                        } else if (nonNull(source.getUtenlandskAdresse())) {
                            target.setMidlertidigAdresse(RsMidlertidigAdresse.builder()
                                    .adressetype(RsMidlertidigAdresse.Adressetype.UTAD)
                                    .utenlandskAdresse(RsPostadresse.builder()
                                            .postLinje1(isNotBlank(source.getUtenlandskAdresse().getAdressenavnNummer()) ?
                                                    source.getUtenlandskAdresse().getAdressenavnNummer() :
                                                    source.getUtenlandskAdresse().getPostboksNummerNavn())
                                            .postLinje2(String.format("%s %s", source.getUtenlandskAdresse().getBySted(),
                                                    source.getUtenlandskAdresse().getPostkode()))
                                            .postLinje3(isNotBlank(source.getUtenlandskAdresse().getRegion()) ?
                                                    source.getUtenlandskAdresse().getRegion() :
                                                    source.getUtenlandskAdresse().getRegionDistriktOmraade())
                                            .postLand(source.getUtenlandskAdresse().getLandkode())
                                            .build())
                                    .gyldigTom(getGyldigTom(source.getGyldigTilOgMed()))
                                    .build());
                        }
                    }
                })
                .register();
    }
}
