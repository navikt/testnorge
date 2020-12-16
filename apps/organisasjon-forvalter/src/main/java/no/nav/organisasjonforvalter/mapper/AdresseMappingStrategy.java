package no.nav.organisasjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.organisasjonforvalter.jpa.entity.Adresse;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest;
import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest.AdresseRequest;
import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest.OrganisasjonRequest;
import org.springframework.stereotype.Component;

@Component
public class AdresseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(AdresseRequest.class, Adresse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(AdresseRequest request, Adresse adresse, MappingContext context) {
                        adresse.setAdresse(String.join(",", request.getAdresselinjer()));
                    }
                })
                .byDefault()
                .register();
    }
}
