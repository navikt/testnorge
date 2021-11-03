package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.util.Objects.nonNull;

@Component
public class DigitalKontaktMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsDigitalKontaktdata.class, DigitalKontaktdata.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsDigitalKontaktdata digitalKontaktdata, DigitalKontaktdata kontaktdataRequest, MappingContext context) {

                        kontaktdataRequest.setGyldigFra(getDato(digitalKontaktdata));

                        if (nonNull(digitalKontaktdata.getMobil())) {
                            kontaktdataRequest.setMobilOppdatert(getDato(digitalKontaktdata));
                            kontaktdataRequest.setMobilVerifisert(getDato(digitalKontaktdata));
                        }
                        if (nonNull(digitalKontaktdata.getEpost())) {
                            kontaktdataRequest.setEpostOppdatert(getDato(digitalKontaktdata));
                            kontaktdataRequest.setEpostVerifisert(getDato(digitalKontaktdata));
                        }
                        if (nonNull(digitalKontaktdata.getSpraak())) {
                            kontaktdataRequest.setSpraakOppdatert(getDato(digitalKontaktdata));
                        }
                    }

                    private ZonedDateTime getDato(RsDigitalKontaktdata digitalKontaktdata) {
                        return nonNull(digitalKontaktdata.getGyldigFra()) ?
                                ZonedDateTime.of(digitalKontaktdata.getGyldigFra(), ZoneId.systemDefault()) :
                                ZonedDateTime.now();
                    }
                })
                .exclude("gyldigFra")
                .byDefault()
                .register();
    }
}
