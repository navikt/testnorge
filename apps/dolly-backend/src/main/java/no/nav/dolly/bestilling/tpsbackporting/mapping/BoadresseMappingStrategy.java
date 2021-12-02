package no.nav.dolly.bestilling.tpsbackporting.mapping;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsGateadresse;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
public class BoadresseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(BostedadresseDTO.class, RsGateadresse.class)
                .customize(new CustomMapper<>() {
                               @Override
                               public void mapAtoB(BostedadresseDTO source, RsGateadresse target, MappingContext context) {


                                   target.setGateadresse(source.getVegadresse().getAdressenavn());
                                   target.setGatekode(source.getVegadresse().getAdressekode());
                                   target.setHusnummer(source.getVegadresse().getHusnummer());
                                   target.setBolignr(source.getVegadresse().getBruksenhetsnummer());
                               }
                           }
                )
                .register();
    }
}
