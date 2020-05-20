package no.nav.dolly.bestilling.pdlforvalter.mapper;

import static java.util.Objects.nonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdresse.Bruksenhetstype;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdresse.Vegadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlMatrikkeladresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsGateadresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsMatrikkeladresse;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class PdlAdresseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsGateadresse.class, Vegadresse.class)
                .customize(new CustomMapper<RsGateadresse, Vegadresse>() {
                    @Override
                    public void mapAtoB(RsGateadresse gateadresse, Vegadresse vegadresse, MappingContext context) {

                        vegadresse.setAdressekode(gateadresse.getGatekode());
                        vegadresse.setAdressenavn(gateadresse.getGateadresse());
                        vegadresse.setHusnummer(gateadresse.getHusnummer());
                        vegadresse.setBruksenhetstype(Bruksenhetstype.BOLIG);
                        vegadresse.setKommunenummer(gateadresse.getKommunenr());
                        vegadresse.setPostnummer(gateadresse.getPostnr());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsMatrikkeladresse.class, PdlMatrikkeladresse.class)
                .customize(new CustomMapper<RsMatrikkeladresse, PdlMatrikkeladresse>() {
                    @Override
                    public void mapAtoB(RsMatrikkeladresse rsMatrikkeladresse, PdlMatrikkeladresse matrikkeladresse, MappingContext context) {

                        matrikkeladresse.setAdressetilleggsnavn(rsMatrikkeladresse.getMellomnavn());
                        matrikkeladresse.setBruksenhetstype(Bruksenhetstype.BOLIG);
                        matrikkeladresse.setBruksnummer(Integer.valueOf(rsMatrikkeladresse.getBruksnr()));
                        matrikkeladresse.setFestenummer(Integer.valueOf(rsMatrikkeladresse.getFestenr()));
                        matrikkeladresse.setGaardsnummer(Integer.valueOf(rsMatrikkeladresse.getGardsnr()));
                        matrikkeladresse.setKommunenummer(rsMatrikkeladresse.getKommunenr());
                        matrikkeladresse.setPostnummer(rsMatrikkeladresse.getPostnr());
                        matrikkeladresse.setUndernummer(Integer.valueOf(rsMatrikkeladresse.getUndernr()));
                    }
                })
                .register();
    }

    public static LocalDate getDato(LocalDateTime dateTime) {

        return nonNull(dateTime) ? dateTime.toLocalDate() : null;
    }
}
