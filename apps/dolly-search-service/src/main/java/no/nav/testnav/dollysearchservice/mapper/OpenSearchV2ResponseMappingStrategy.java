package no.nav.testnav.dollysearchservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.dollysearchservice.dto.SearchInternalResponse;
import no.nav.testnav.libs.data.dollysearchservice.v2.SearchResponse;
import org.springframework.stereotype.Component;

@Component
public class OpenSearchV2ResponseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(SearchInternalResponse.class, SearchResponse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SearchInternalResponse searchInternalResponse, SearchResponse searchResponse, MappingContext context) {

                        searchResponse.setTotalHits(searchInternalResponse.getTotalHits());
                        searchResponse.setTook(searchInternalResponse.getTook());
                        searchResponse.setSide(searchInternalResponse.getSide());
                        searchResponse.setAntall(searchInternalResponse.getAntall());
                        searchResponse.setSeed(searchInternalResponse.getSeed());
                        searchResponse.setPersoner(searchInternalResponse.getPersoner());
                        searchResponse.setError(searchInternalResponse.getError());
                    }
                })
                .register();
    }
}
