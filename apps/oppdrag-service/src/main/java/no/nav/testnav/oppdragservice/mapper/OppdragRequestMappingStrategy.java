package no.nav.testnav.oppdragservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.libs.dto.oppdragservice.v1.Oppdrag;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragRequest;
import no.nav.testnav.oppdragservice.wsdl.Oppdragslinje;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragRequest;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragRequest2;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class OppdragRequestMappingStrategy implements MappingStrategy{

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(OppdragRequest.class, SendInnOppdragRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(OppdragRequest source,
                                        SendInnOppdragRequest destination,
                                        MappingContext context) {

                        var oppdragRequest = new SendInnOppdragRequest2();
                        oppdragRequest.setOppdrag(mapperFacade.map(source.getOppdrag(), no.nav.testnav.oppdragservice.wsdl.Oppdrag.class, context));
                        destination.setRequest(oppdragRequest);
                    }
                })
                .register();

        factory.classMap(Oppdrag.class, no.nav.testnav.oppdragservice.wsdl.Oppdrag.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Oppdrag source,
                                        no.nav.testnav.oppdragservice.wsdl.Oppdrag destination,
                                        MappingContext context) {

                        if (nonNull(source.getUtbetFrekvens())) {
                            destination.setUtbetFrekvens(destination.getUtbetFrekvens()
                                    .replace("_",""));
                        }
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Oppdrag.Oppdragslinje.class, Oppdragslinje.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Oppdrag.Oppdragslinje source,
                                        Oppdragslinje destination,
                                        MappingContext context) {

                        if (nonNull(source.getTypeSats())) {
                            destination.setTypeSats(destination.getTypeSats()
                                    .replace("_",""));
                        }
                    }
                })
                .byDefault()
                .register();
    }
}
