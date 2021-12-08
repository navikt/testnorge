package no.nav.dolly.bestilling.tpsbackporting.mapping;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsGateadresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsMatrikkeladresse;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class BoadresseMappingStrategy implements MappingStrategy {

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
                                               .gardsnr(source.getMatrikkeladresse().getGaardsnummer().toString())
                                               .bruksnr(source.getMatrikkeladresse().getBruksenhetsnummer())
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
                                   }
                               }
                           }
                )
                .register();
    }
}
