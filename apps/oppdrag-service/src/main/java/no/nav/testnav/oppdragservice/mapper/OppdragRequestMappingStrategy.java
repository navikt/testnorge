package no.nav.testnav.oppdragservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragRequest;
import no.nav.testnav.oppdragservice.wsdl.Oppdrag;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragRequest;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragRequest2;
import org.springframework.stereotype.Component;

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

                        destination.setRequest(mapperFacade.map(source, SendInnOppdragRequest2.class, context));
                    }
                })
                .register();

        factory.classMap(OppdragRequest.class, Oppdrag.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(OppdragRequest source,
                                        Oppdrag destination,
                                        MappingContext context) {


                    }
                })
                .byDefault()
                .register();
    }
}
