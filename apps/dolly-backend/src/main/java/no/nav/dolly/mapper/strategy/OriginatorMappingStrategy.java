package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import org.springframework.stereotype.Component;

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
                        destinasjon.setSyntetisk((Boolean) context.getProperty("navSyntetiskIdent"));
                    }
                })
                .exclude("person")
                .byDefault()
                .register();

        factory.classMap(RsTpsfUtvidetBestilling.class, TpsfBestilling.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsTpsfUtvidetBestilling kilde, TpsfBestilling destinasjon, MappingContext context) {

                        destinasjon.setAntall(1);
                    }
                })
                .byDefault()
                .register();
    }
}
