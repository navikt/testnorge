package no.nav.organisasjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.organisasjonforvalter.consumer.TpsfAdresseConsumer.GyldigeAdresserResponse.AdresseData;
import no.nav.organisasjonforvalter.jpa.entity.Adresse;
import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest.AdresseRequest;
import no.nav.organisasjonforvalter.provider.rs.responses.RsAdresse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdresseMappingStrategy implements MappingStrategy {

    private static final String NORGE = "NO";

    private static String fixHusnummer(String husnummer) {
        // Remove leading zeros
        return husnummer.replaceAll("^0+(?!$)", "");
    }

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(AdresseRequest.class, Adresse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(AdresseRequest request, Adresse adresse, MappingContext context) {
                        adresse.setAdresse(String.join(",", request.getAdresselinjer()));
                    }
                })
                .byDefault()                .register();

        factory.classMap(Adresse.class, RsAdresse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Adresse adresse, RsAdresse rsAdresse, MappingContext context) {
                        rsAdresse.setAdresselinjer(List.of(adresse.getAdresse().split(",")));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(AdresseData.class, AdresseRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(AdresseData adresse, AdresseRequest request, MappingContext context) {

                        request.getAdresselinjer().add(String.format("%s %s", adresse.getAdrnavn(), fixHusnummer(adresse.getHusnrfra())));
                        request.setPostnr(adresse.getPnr());
                        request.setKommunenr(adresse.getKnr());
                        request.setLandkode(NORGE);
                    }
                })
                .byDefault()
                .register();
    }
}
