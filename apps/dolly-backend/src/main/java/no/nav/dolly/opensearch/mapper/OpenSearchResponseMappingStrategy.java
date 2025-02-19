package no.nav.dolly.opensearch.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.opensearch.dto.SearchResponse.RegistreResponseStatus;
import no.nav.dolly.elastic.ElasticTyper;
import no.nav.dolly.elastic.dto.SearchResponse;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenSearchResponseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(SearchResponse.class, RegistreResponseStatus.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SearchResponse kilde, RegistreResponseStatus destinasjon, MappingContext context) {

                        destinasjon.setAntallIdenter(kilde.getIdenter().size());
                        destinasjon.setTotalHitsBestillinger(kilde.getTotalHits());
                        destinasjon.setRegistre((List<ElasticTyper>) context.getProperty("registre"));
                    }
                })
                .byDefault()
                .register();
    }
}
