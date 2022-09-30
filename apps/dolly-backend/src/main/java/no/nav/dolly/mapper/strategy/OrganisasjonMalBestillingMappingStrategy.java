package no.nav.dolly.mapper.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrganisasjonMalBestillingMappingStrategy implements MappingStrategy {

    private final JsonBestillingMapper jsonBestillingMapper;

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(OrganisasjonBestilling.class, RsOrganisasjonBestilling.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(OrganisasjonBestilling bestilling, RsOrganisasjonBestilling malBestilling, MappingContext context) {

                        malBestilling.setEnvironments(new ArrayList<>(List.of(bestilling.getMiljoer().split(","))));
                        malBestilling.setOrganisasjon(jsonBestillingMapper.mapOrganisasjonBestillingRequest(bestilling.getBestKriterier()));
                        malBestilling.setMalBestillingNavn(null);
                    }
                })
                .byDefault()
                .register();
    }
}
