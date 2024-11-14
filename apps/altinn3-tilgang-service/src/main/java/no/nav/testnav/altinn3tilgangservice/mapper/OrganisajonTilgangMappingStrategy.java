package no.nav.testnav.altinn3tilgangservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.altinn3tilgangservice.database.entity.OrganisasjonTilgang;
import no.nav.testnav.altinn3tilgangservice.domain.Organisasjon;
import no.nav.testnav.altinn3tilgangservice.domain.OrganisasjonResponse;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class OrganisajonTilgangMappingStrategy implements MappingStrategy {
    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Organisasjon.class, OrganisasjonResponse.class)
                .customize(new CustomMapper<>() {

                    @Override
                    public void mapAtoB(Organisasjon organisasjon, OrganisasjonResponse response, MappingContext context) {

                        var organisasjonTilgang = (OrganisasjonTilgang) context.getProperty("tilgang");
                        response.setMiljoe(nonNull(organisasjonTilgang) && isNotBlank(organisasjonTilgang.getMiljoe()) ?
                                organisasjonTilgang.getMiljoe() : "q1");
                    }
                })
                .byDefault()
                .register();
    }
}
