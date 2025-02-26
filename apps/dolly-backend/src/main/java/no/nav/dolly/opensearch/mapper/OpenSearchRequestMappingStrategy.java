package no.nav.dolly.opensearch.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.dolly.opensearch.dto.SearchRequest;
import org.springframework.stereotype.Component;

@Component
public class OpenSearchRequestMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(SearchRequest.class, no.nav.testnav.libs.data.dollysearchservice.v1.SearchRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SearchRequest kilde, no.nav.testnav.libs.data.dollysearchservice.v1.SearchRequest destinasjon, MappingContext context) {

                        destinasjon.setAntall(kilde.getPagineringPersonRequest().getAntall());
                        destinasjon.setSide(kilde.getPagineringPersonRequest().getSide());
                        destinasjon.setSeed(kilde.getPagineringPersonRequest().getSeed());
                    }
                })
                .byDefault()
                .register();
    }
}
