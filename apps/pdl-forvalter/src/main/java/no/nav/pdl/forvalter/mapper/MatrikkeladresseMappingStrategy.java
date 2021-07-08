package no.nav.pdl.forvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import org.springframework.stereotype.Component;

@Component
public class MatrikkeladresseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(no.nav.registre.testnorge.libs.dto.adresseservice.v1.MatrikkeladresseDTO.class, MatrikkeladresseDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(no.nav.registre.testnorge.libs.dto.adresseservice.v1.MatrikkeladresseDTO kildeAdresse,
                                        MatrikkeladresseDTO matrikkeladresse, MappingContext context) {

                        matrikkeladresse.setAdressetilleggsnavn(kildeAdresse.getTilleggsnavn());
                    }
                })
                .byDefault()
                .register();
    }
}