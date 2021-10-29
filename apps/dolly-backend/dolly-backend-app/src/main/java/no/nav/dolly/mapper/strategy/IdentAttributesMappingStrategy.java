package no.nav.dolly.mapper.strategy;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.testperson.IdentAttributesResponse;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class IdentAttributesMappingStrategy implements MappingStrategy {

    @Override public void register(MapperFactory factory) {
        factory.classMap(Testident.class, IdentAttributesResponse.class)
                .customize(new CustomMapper<Testident, IdentAttributesResponse>() {
                    @Override
                    public void mapAtoB(Testident testident, IdentAttributesResponse attributesResponse, MappingContext context) {

                        attributesResponse.setGruppeId(testident.getTestgruppe().getId());
                        attributesResponse.setIbruk(isTrue(testident.getIBruk()));
                    }
                })
                .byDefault()
                .register();
    }
}
