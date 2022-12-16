package no.nav.dolly.bestilling.krrstub.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Component
public class DigitalKontaktMappingStrategy implements MappingStrategy {

    private static void kobleMaalformTilSpraak(RsDollyUtvidetBestilling bestilling, DigitalKontaktdata digitalKontaktdata) {

        String maalform = null;

        if (nonNull(bestilling.getTpsf()) && isKrrMaalform(bestilling.getTpsf().getSprakKode())) {
            maalform = bestilling.getTpsf().getSprakKode();

        } else if (nonNull(bestilling.getTpsMessaging()) && isKrrMaalform(bestilling.getTpsMessaging().getSpraakKode())) {
            maalform = bestilling.getTpsMessaging().getSpraakKode();
        }

        if (isNotBlank(maalform) && isBlank(digitalKontaktdata.getSpraak())) {

            digitalKontaktdata.setSpraak(isNotBlank(maalform) ? maalform.toLowerCase() : maalform); //NOSONAR
            digitalKontaktdata.setSpraakOppdatert(ZonedDateTime.now());
            digitalKontaktdata.setRegistrert(true);
        }
    }

    private static boolean isKrrMaalform(String spraak) {

        return isNotBlank(spraak) && Stream.of("NB", "NN", "EN", "SE").anyMatch(spraak::equalsIgnoreCase);
    }

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsDigitalKontaktdata.class, DigitalKontaktdata.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsDigitalKontaktdata digitalKontaktdata, DigitalKontaktdata kontaktdataRequest, MappingContext context) {

                        kontaktdataRequest.setPersonident((String) context.getProperty("ident"));

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

                        kobleMaalformTilSpraak((RsDollyUtvidetBestilling) context.getProperty("bestilling"), kontaktdataRequest);
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
