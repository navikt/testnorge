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
import static no.nav.dolly.util.DateZoneUtil.CET;
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
                            kontaktdataRequest.setMobil(digdirFormatertTlfNummer(digitalKontaktdata.getMobil(), digitalKontaktdata.getLandkode()));
                            kontaktdataRequest.setMobilOppdatert(ZonedDateTime.now(ZoneId.of("UTC")));
                        }
                        if (isNotBlank(digitalKontaktdata.getEpost())) {
                            kontaktdataRequest.setEpostOppdatert(ZonedDateTime.now(ZoneId.of("UTC")));
                        }
                        if (isNotBlank(digitalKontaktdata.getSpraak())) {
                            kontaktdataRequest.setSpraakOppdatert(ZonedDateTime.now(ZoneId.of("UTC")));
                        }

                        kontaktdataRequest.setEpost(isBlank(digitalKontaktdata.getEpost()) ? null : digitalKontaktdata.getEpost());
                        kontaktdataRequest.setSpraak(isBlank(digitalKontaktdata.getSpraak()) ? null : digitalKontaktdata.getSpraak());

                        kontaktdataRequest.setReservertOppdatert(ZonedDateTime.now(CET));
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
                                digitalKontaktdata.getGyldigFra().atStartOfDay(ZoneId.of("UTC")) :
                                ZonedDateTime.now(CET);
                    }
                })
                .exclude("gyldigFra")
                .byDefault()
                .register();
    }
}
