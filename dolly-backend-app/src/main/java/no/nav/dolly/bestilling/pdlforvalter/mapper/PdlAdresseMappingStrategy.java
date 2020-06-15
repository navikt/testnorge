package no.nav.dolly.bestilling.pdlforvalter.mapper;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdresse.Bruksenhetstype;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdresse.Vegadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlMatrikkeladresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.BoAdresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.BoGateadresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.BoMatrikkeladresse;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class PdlAdresseMappingStrategy implements MappingStrategy {

    private static final String CO_NAME = "C/O";

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(BoGateadresse.class, Vegadresse.class)
                .customize(new CustomMapper<BoGateadresse, Vegadresse>() {
                    @Override
                    public void mapAtoB(BoGateadresse gateadresse, Vegadresse vegadresse, MappingContext context) {

                        vegadresse.setAdressekode(gateadresse.getGatekode());
                        vegadresse.setAdressenavn(gateadresse.getGateadresse());
                        vegadresse.setHusnummer(gateadresse.getHusnummer());
                        vegadresse.setBruksenhetstype(Bruksenhetstype.BOLIG);
                        vegadresse.setKommunenummer(gateadresse.getKommunenr());
                        vegadresse.setPostnummer(gateadresse.getPostnr());
                        vegadresse.setAdressetillegsnavn(getTilleggsnavn(gateadresse));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(BoMatrikkeladresse.class, PdlMatrikkeladresse.class)
                .customize(new CustomMapper<BoMatrikkeladresse, PdlMatrikkeladresse>() {
                    @Override
                    public void mapAtoB(BoMatrikkeladresse rsMatrikkeladresse, PdlMatrikkeladresse matrikkeladresse, MappingContext context) {

                        matrikkeladresse.setAdressetilleggsnavn(rsMatrikkeladresse.getMellomnavn());
                        matrikkeladresse.setBruksenhetstype(Bruksenhetstype.BOLIG);
                        matrikkeladresse.setBruksnummer(Integer.valueOf(rsMatrikkeladresse.getBruksnr()));
                        matrikkeladresse.setFestenummer(Integer.valueOf(rsMatrikkeladresse.getFestenr()));
                        matrikkeladresse.setGaardsnummer(Integer.valueOf(rsMatrikkeladresse.getGardsnr()));
                        matrikkeladresse.setKommunenummer(rsMatrikkeladresse.getKommunenr());
                        matrikkeladresse.setPostnummer(rsMatrikkeladresse.getPostnr());
                        matrikkeladresse.setUndernummer(Integer.valueOf(rsMatrikkeladresse.getUndernr()));
                        matrikkeladresse.setAdressetilleggsnavn(getTilleggsnavn(rsMatrikkeladresse));
                    }
                })
                .register();
    }

    public static String getCoadresse(BoAdresse boAdresse) {

        return isNotBlank(boAdresse.getTilleggsadresse()) &&
                boAdresse.getTilleggsadresse().contains(CO_NAME) ?
                boAdresse.getTilleggsadresse() : null;
    }

    public static String getTilleggsnavn(BoAdresse boAdresse) {

        return isNotBlank(boAdresse.getTilleggsadresse()) &&
                !boAdresse.getTilleggsadresse().contains(CO_NAME) ?
                boAdresse.getTilleggsadresse() : null;
    }

    public static LocalDate getDato(LocalDateTime dateTime) {

        return nonNull(dateTime) ? dateTime.toLocalDate() : null;
    }
}
