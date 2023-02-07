package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class OriginatorMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(PdlPersondata.class, BestillingRequestDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PdlPersondata kilde, BestillingRequestDTO destinasjon, MappingContext context) {

                        destinasjon.setPerson(kilde.getPerson());
                        if (nonNull(kilde.getOpprettNyPerson())) {
                            mapperFacade.map(kilde.getOpprettNyPerson(), destinasjon);
                        }
                        if (isNull(destinasjon.getSyntetisk())) {
                            destinasjon.setSyntetisk((Boolean) context.getProperty("navSyntetiskIdent"));
                        }
                        destinasjon.setOpprettFraIdent((String) context.getProperty("opprettFraIdent"));
                    }
                })
                .exclude("person")
                .byDefault()
                .register();
    }
}
