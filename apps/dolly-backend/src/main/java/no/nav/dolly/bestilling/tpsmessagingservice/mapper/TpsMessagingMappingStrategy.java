package no.nav.dolly.bestilling.tpsmessagingservice.mapper;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TelefonnummerDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SpraakDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonTypeNummerDTO.TypeTelefon;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TpsMessagingMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(String.class, SpraakDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(String rsSpraakKode, SpraakDTO spraakKode, MappingContext context) {

                        spraakKode.setSprakKode(rsSpraakKode);
                    }
                })
                .register();

        factory.classMap(TelefonnummerDTO.class, no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonTypeNummerDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(TelefonnummerDTO kilde, no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonTypeNummerDTO destinasjon, MappingContext context) {

                        destinasjon.setTelefonnummer(kilde.getNummer());
                        destinasjon.setLandkode(kilde.getLandskode());
                        destinasjon.setTelefontype(kilde.getPrioritet().equals(1) ? TypeTelefon.MOBI : TypeTelefon.HJET);
                    }
                })
                .register();
    }
}
