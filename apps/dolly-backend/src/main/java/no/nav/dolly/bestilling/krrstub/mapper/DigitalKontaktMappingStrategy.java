package no.nav.dolly.bestilling.krrstub.mapper;

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
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Component
public class DigitalKontaktMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsDigitalKontaktdata.class, DigitalKontaktdata.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsDigitalKontaktdata digitalKontaktdata, DigitalKontaktdata kontaktdataRequest, MappingContext context) {

                        kontaktdataRequest.setPersonident((String) context.getProperty("ident"));

                        kontaktdataRequest.setGyldigFra(getDato(digitalKontaktdata));

                        if (isNotBlank(digitalKontaktdata.getMobil())) {
                            kontaktdataRequest.setMobilOppdatert(getDato(digitalKontaktdata));
                            kontaktdataRequest.setMobilVerifisert(getDato(digitalKontaktdata));
                            kontaktdataRequest.setMobil(digdirFormatertTlfNummer(digitalKontaktdata.getMobil(), digitalKontaktdata.getLandkode()));
                        }
                        if (isNotBlank(digitalKontaktdata.getEpost())) {
                            kontaktdataRequest.setEpostOppdatert(getDato(digitalKontaktdata));
                            kontaktdataRequest.setEpostVerifisert(getDato(digitalKontaktdata));
                        }
                        if (isNotBlank(digitalKontaktdata.getSpraak())) {
                            kontaktdataRequest.setSpraakOppdatert(getDato(digitalKontaktdata));
                        }

                        kontaktdataRequest.setEpost(isBlank(digitalKontaktdata.getEpost()) ? null : digitalKontaktdata.getEpost());
                        kontaktdataRequest.setSpraak(isBlank(digitalKontaktdata.getSpraak()) ? null : digitalKontaktdata.getSpraak());
                    }

                    private String digdirFormatertTlfNummer(String mobil, String landkode) {
                        if (isBlank(mobil)) {
                            return null;
                        }
                        var nummerUtenSpace = mobil.replace(" ", "");
                        if (nummerUtenSpace.contains("+")) {
                            return nummerUtenSpace;
                        } else if (isBlank(landkode)) {
                            return "+47%s".formatted(nummerUtenSpace);
                        } else {
                            return "%s%s".formatted(landkode, nummerUtenSpace);
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
