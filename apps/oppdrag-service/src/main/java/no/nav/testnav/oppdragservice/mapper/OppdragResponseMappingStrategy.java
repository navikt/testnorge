package no.nav.testnav.oppdragservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.libs.dto.oppdragservice.v1.Oppdrag;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragResponse;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragResponse.Infomelding;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragResponse;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class OppdragResponseMappingStrategy implements MappingStrategy{

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(SendInnOppdragResponse.class, OppdragResponse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(SendInnOppdragResponse source,
                                        OppdragResponse destination,
                                        MappingContext context) {

                        var oppdrag = source.getResponse().getOppdrag();
                        if (nonNull(oppdrag.getUtbetFrekvens())) {
                            oppdrag.setUtbetFrekvens(oppdrag.getUtbetFrekvens()
                                    .replace("14DG", "_14DG"));
                        }

                        oppdrag.getOppdragslinje().stream()
                                .filter(oppdragslinje -> nonNull(oppdragslinje.getTypeSats()))
                                        .forEach(oppdragslinje ->
                                                oppdragslinje.setTypeSats(oppdragslinje.getTypeSats()
                                                        .replace("14DB","_14DB")));

                        destination.setOppdrag(mapperFacade.map(oppdrag, Oppdrag.class, context));
                        destination.setInfomelding(mapperFacade.map(source.getResponse().getInfomelding(), Infomelding.class));
                    }
                })
                .register();
    }
}
