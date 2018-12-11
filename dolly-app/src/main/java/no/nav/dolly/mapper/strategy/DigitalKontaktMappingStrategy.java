package no.nav.dolly.mapper.strategy;

import java.time.ZonedDateTime;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.RsDollyBestillingsRequest;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdataRequest;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class DigitalKontaktMappingStrategy implements MappingStrategy{
    @Override public void register(MapperFactory factory) {
        factory.classMap(RsDollyBestillingsRequest.class, DigitalKontaktdataRequest.class)
                .customize(new CustomMapper<RsDollyBestillingsRequest, DigitalKontaktdataRequest>() {
                    @Override public void mapAtoB(RsDollyBestillingsRequest bestilling, DigitalKontaktdataRequest kontaktdataRequest, MappingContext context) {

                        kontaktdataRequest.setGyldigFra(ZonedDateTime.now());
                        kontaktdataRequest.setEpost(bestilling.getKrrstub().getEpost());
                        kontaktdataRequest.setMobil(bestilling.getKrrstub().getMobil());
                        kontaktdataRequest.setReservert(bestilling.getKrrstub().isReservert());
                    }
                })
                .register();
    }
}
