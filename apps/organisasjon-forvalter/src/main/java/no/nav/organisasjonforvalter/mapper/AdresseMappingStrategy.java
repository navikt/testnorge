package no.nav.organisasjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.organisasjonforvalter.consumer.TpsfAdresseConsumer.GyldigeAdresserResponse.AdresseData;
import no.nav.organisasjonforvalter.jpa.entity.Adresse;
import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest.AdresseRequest;
import no.nav.organisasjonforvalter.provider.rs.responses.RsAdresse;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.AdresseDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

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
                .byDefault().register();

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

        factory.classMap(Adresse.class, no.nav.registre.testnorge.libs.avro.organisasjon.v1.Adresse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Adresse source, no.nav.registre.testnorge.libs.avro.organisasjon.v1.Adresse target, MappingContext context) {
                        String[] adresselinjer = source.getAdresse().split(",");
                        target.setPostadresse1(adresselinjer.length > 0 ? adresselinjer[0] : null);
                        target.setPostadresse2(adresselinjer.length > 1 ? adresselinjer[1] : null);
                        target.setPostadresse3(adresselinjer.length > 2 ? adresselinjer[2] : null);
                        target.setKommunenummer(source.getKommunenr());
                        target.setPostnummer(source.getPostnr());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(AdresseDTO.class, RsAdresse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(AdresseDTO source, RsAdresse target, MappingContext context) {
                        List<String> adresselinjer = new ArrayList<>();
                        adresselinjer.add(source.getAdresselinje1());
                        if (isNotBlank(source.getAdresselinje2())) {
                            adresselinjer.add(source.getAdresselinje2());
                        }
                        if (isNotBlank(source.getAdresselinje3())) {
                            adresselinjer.add(source.getAdresselinje3());
                        }
                        target.setAdresselinjer(adresselinjer);
                        target.setKommunenr(source.getKommunenummer());
                        target.setPostnr(source.getPostnummer());
                        target.setPoststed(source.getPoststed());
                    }
                })
                .byDefault()
                .register();
    }
}
