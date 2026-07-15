package no.nav.testnav.dollysearchservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.libs.dto.dollysearchservice.v1.ElasticTyper;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

@Component
public class OpenSearchRequestMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory
                .classMap(
                        no.nav.testnav.libs.dto.dollysearchservice.v1.SearchRequest.class,
                        no.nav.testnav.dollysearchservice.dto.SearchRequest.class
                )
                .customize(new CustomMapper<>() {

                    @Override
                    public void mapAtoB(
                            no.nav.testnav.libs.dto.dollysearchservice.v1.SearchRequest kilde,
                            no.nav.testnav.dollysearchservice.dto.SearchRequest destinasjon,
                            MappingContext context
                    ) {
                        var registreRequest = getRegistreRequest(context);
                        destinasjon.setRegistreRequest(registreRequest);
                        destinasjon.setMiljoer(getBetingedeMiljoer(kilde.getMiljoer(), registreRequest));
                    }

                })
                .exclude("miljoer")
                .byDefault()
                .register();
    }

    private static List<ElasticTyper> getRegistreRequest(MappingContext context) {

        var value = context.getProperty("registreRequest");
        if (value instanceof List<?> values) {
            return values
                    .stream()
                    .filter(ElasticTyper.class::isInstance)
                    .map(ElasticTyper.class::cast)
                    .toList();
        }
        return emptyList();

    }

    private static List<String> getBetingedeMiljoer(List<String> miljoer, List<ElasticTyper> registreRequest) {

        return nonNull(registreRequest) && registreRequest.stream()
                .anyMatch(ElasticTyper::isMiljoAvhengig) ? miljoer : emptyList();

    }
}
