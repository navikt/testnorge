package no.nav.testnav.dollysearchservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.libs.dto.dollysearchservice.v1.ElasticTyper;
import no.nav.testnav.libs.dto.dollysearchservice.v1.SearchRequest;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

@Component
public class OpenSearchRequestMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(SearchRequest.class, no.nav.testnav.dollysearchservice.dto.SearchRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SearchRequest kilde, no.nav.testnav.dollysearchservice.dto.SearchRequest destinasjon, MappingContext context) {

                        var registreRequest = (List<ElasticTyper>) context.getProperty("registreRequest");
                        destinasjon.setRegistreRequest(registreRequest);
                        destinasjon.setMiljoer(getBetingedeMiljoer(kilde.getMiljoer(), registreRequest));
                    }
                })
                .exclude("miljoer")
                .byDefault()
                .register();
    }

    private static List<String> getBetingedeMiljoer(List<String> miljoer, List<ElasticTyper> registreRequest) {

        return nonNull(registreRequest) && registreRequest.stream()
                .anyMatch(ElasticTyper::isMiljoAvhengig) ? miljoer : emptyList();
    }
}
