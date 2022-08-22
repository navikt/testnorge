package no.nav.dolly.bestilling.tpsmessagingservice.mapper;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TelefonnummerDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.AdresseUtlandDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SpraakDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonTypeNummerDTO.TypeTelefon;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

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

        factory.classMap(BostedadresseDTO.class, AdresseUtlandDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BostedadresseDTO kilde, AdresseUtlandDTO destinasjon, MappingContext context) {

                        mapperFacade.map(kilde.getUtenlandskAdresse(), destinasjon);
                        destinasjon.setDatoAdresse(kilde.getAngittFlyttedato());
                    }
                })
                .register();

        factory.classMap(KontaktadresseDTO.class, AdresseUtlandDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(KontaktadresseDTO kilde, AdresseUtlandDTO destinasjon, MappingContext context) {

                        mapperFacade.map(kilde.getUtenlandskAdresse(), destinasjon);
                        destinasjon.setDatoAdresse(kilde.getGyldigFraOgMed());
                    }
                })
                .register();

        factory.classMap(UtenlandskAdresseDTO.class, AdresseUtlandDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(UtenlandskAdresseDTO kilde, AdresseUtlandDTO destinasjon, MappingContext context) {

                        destinasjon.setAdresse1(isNotBlank(kilde.getAdressenavnNummer()) ?
                                kilde.getAdressenavnNummer() :
                                kilde.getPostboksNummerNavn());
                        destinasjon.setAdresse2(Stream.of(kilde.getBySted(),
                                        kilde.getPostkode())
                                .filter(StringUtils::isNotBlank)
                                .collect(Collectors.joining(" ")));
                        destinasjon.setAdresse3(Stream.of(kilde.getRegion(),
                                        kilde.getRegionDistriktOmraade(),
                                        kilde.getDistriktsnavn())
                                .filter(StringUtils::isNotBlank)
                                .collect(Collectors.joining(" ")));
                        destinasjon.setKodeLand(kilde.getLandkode());
                    }
                })
                .register();

        factory.classMap(TelefonnummerDTO.class, no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonTypeNummerDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(TelefonnummerDTO kilde, no.nav.testnav.libs.dto.tpsmessagingservice.v1.TelefonTypeNummerDTO destinasjon, MappingContext context) {

                        destinasjon.setTelefonnummer(kilde.getNummer());
                        destinasjon.setLandkode(kilde.getLandskode());
                        destinasjon.setTelefontype(switch (kilde.getPrioritet()) {
                            case 1 -> TypeTelefon.MOBI;
                            default -> TypeTelefon.HJET;
                        });
                    }
                })
                .register();
    }
}
