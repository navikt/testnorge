package no.nav.organisasjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest.OrganisasjonRequest;
import org.springframework.stereotype.Component;

@Component
public class OrganisasjonMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(OrganisasjonRequest.class, Organisasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(OrganisasjonRequest request, Organisasjon organisasjon, MappingContext context) {

                    }
                })
                .exclude("adresser")
                .byDefault()
                .register();
    }
}
