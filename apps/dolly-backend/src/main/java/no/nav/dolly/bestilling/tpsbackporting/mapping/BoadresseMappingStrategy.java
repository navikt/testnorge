package no.nav.dolly.bestilling.tpsbackporting.mapping;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsGateadresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsMatrikkeladresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class BoadresseMappingStrategy implements MappingStrategy {

    private static String getStringVal(Integer number) {

        return nonNull(number) ? number.toString() : null;
    }

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(BostedadresseDTO.class, TpsfBestilling.class)
                .customize(new CustomMapper<>() {
                               @Override
                               public void mapAtoB(BostedadresseDTO source, TpsfBestilling target, MappingContext context) {

                                   if (nonNull(source.getVegadresse())) {
                                       target.setBoadresse(RsGateadresse.builder()
                                               .gateadresse(source.getVegadresse().getAdressenavn())
                                               .gatekode(source.getVegadresse().getAdressekode())
                                               .husnummer(source.getVegadresse().getHusnummer())
                                               .kommunenr(source.getVegadresse().getKommunenummer())
                                               .postnr(source.getVegadresse().getPostnummer())
                                               .matrikkelId(source.getAdresseIdentifikatorFraMatrikkelen())
                                               .flyttedato(source.getAngittFlyttedato())
                                               .build());

                                   } else if (nonNull(source.getMatrikkeladresse())) {
                                       target.setBoadresse(RsMatrikkeladresse.builder()
                                               .gardsnr(getStringVal(source.getMatrikkeladresse().getGaardsnummer()))
                                               .bruksnr(getStringVal(source.getMatrikkeladresse().getBruksnummer()))
                                               .mellomnavn(source.getMatrikkeladresse().getTilleggsnavn())
                                               .kommunenr(source.getMatrikkeladresse().getKommunenummer())
                                               .postnr(source.getMatrikkeladresse().getPostnummer())
                                               .matrikkelId(source.getAdresseIdentifikatorFraMatrikkelen())
                                               .flyttedato(source.getAngittFlyttedato())
                                               .build());

                                   } else if (nonNull(source.getUkjentBosted())) {
                                       target.setUtenFastBopel(true);
                                       target.setBoadresse(RsGateadresse.builder()
                                               .kommunenr(source.getUkjentBosted().getBostedskommune())
                                               .build());

                                   } else if (nonNull(source.getUtenlandskAdresse())) {
                                       target.setPostadresse(List.of(RsPostadresse.builder()
                                               .postLinje1(isNotBlank(source.getUtenlandskAdresse().getAdressenavnNummer()) ?
                                                       source.getUtenlandskAdresse().getAdressenavnNummer() :
                                                       source.getUtenlandskAdresse().getPostboksNummerNavn())
                                               .postLinje2(Stream.of(source.getUtenlandskAdresse().getBySted(),
                                                               source.getUtenlandskAdresse().getPostkode())
                                                       .filter(StringUtils::isNotBlank)
                                                       .collect(Collectors.joining(" ")))
                                               .postLinje3(Stream.of(source.getUtenlandskAdresse().getRegion(),
                                                               source.getUtenlandskAdresse().getRegionDistriktOmraade(),
                                                               source.getUtenlandskAdresse().getDistriktsnavn())
                                                       .filter(StringUtils::isNotBlank)
                                                       .collect(Collectors.joining(" ")))
                                               .postLand(source.getUtenlandskAdresse().getLandkode())
                                               .build()));
                                   }
                               }
                           }
                )
                .register();
    }
}
