package no.nav.organisasjonforvalter.mapper;

import lombok.val;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.organisasjonforvalter.dto.responses.RsAdresse;
import no.nav.organisasjonforvalter.dto.responses.RsOrganisasjon;
import no.nav.testnav.libs.dto.ereg.v1.Enhetstype;
import no.nav.testnav.libs.dto.ereg.v1.Epostadresse;
import no.nav.testnav.libs.dto.ereg.v1.Forretningsadresse;
import no.nav.testnav.libs.dto.ereg.v1.Internettadresse;
import no.nav.testnav.libs.dto.ereg.v1.Organisasjon;
import no.nav.testnav.libs.dto.ereg.v1.Postadresse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@Component
public class EregOrganisasjonMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Organisasjon.class, RsOrganisasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Organisasjon kilde, RsOrganisasjon destinasjon, MappingContext context) {

                        val juridiskEnhet = (Set<String>) context.getProperty("opplysningspliktige");

                        destinasjon.setOrganisasjonsnummer(kilde.getOrganisasjonsnummer());
                        destinasjon.setJuridiskEnhet(juridiskEnhet.stream()
                                .findFirst()
                                .orElse(null));
                        destinasjon.setEnhetstype(kilde.getOrganisasjonDetaljer().getEnhetstyper().stream()
                                .map(Enhetstype::getEnhetstype)
                                .findFirst()
                                .orElse(null));
                        destinasjon.setOrganisasjonsnavn(kilde.getNavn().getNavnelinje1());
                        destinasjon.setAdresser(
                                new ArrayList<>(kilde.getOrganisasjonDetaljer().getForretningsadresser().stream()
                                        .filter(Objects::nonNull)
                                        .map(adresse -> mapperFacade.map(adresse, RsAdresse.class))
                                        .toList()));
                        destinasjon.getAdresser().addAll(kilde.getOrganisasjonDetaljer().getPostadresser().stream()
                                .filter(Objects::nonNull)
                                .map(adresse -> mapperFacade.map(adresse, RsAdresse.class))
                                .toList());
                        destinasjon.setEpost(kilde.getOrganisasjonDetaljer().getEpostadresser().stream()
                                .map(Epostadresse::getAdresse)
                                .findFirst()
                                .orElse(null));
                        destinasjon.setNettside(kilde.getOrganisasjonDetaljer().getInternettadresser().stream()
                                .map(Internettadresse::getAdresse)
                                .findFirst()
                                .orElse(null));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Forretningsadresse.class, RsAdresse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Forretningsadresse kilde, RsAdresse destinasjon, MappingContext context) {

                        destinasjon.setAdressetype(RsAdresse.AdresseType.FADR);
                        destinasjon.setAdresselinjer(Stream.of(kilde.getAdresselinje1(),
                                        kilde.getAdresselinje2(),
                                        kilde.getAdresselinje3())
                                .filter(Objects::nonNull)
                                .toList());
                        destinasjon.setPostnr(kilde.getPostnummer());
                        destinasjon.setKommunenr(kilde.getKommunenummer());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Postadresse.class, RsAdresse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Postadresse kilde, RsAdresse destinasjon, MappingContext context) {

                        destinasjon.setAdressetype(RsAdresse.AdresseType.PADR);
                        destinasjon.setAdresselinjer(Stream.of(kilde.getAdresselinje1(),
                                        kilde.getAdresselinje2(),
                                        kilde.getAdresselinje3())
                                .filter(Objects::nonNull)
                                .toList());
                        destinasjon.setPostnr(kilde.getPostnummer());
                        destinasjon.setKommunenr(kilde.getKommunenummer());
                    }
                })
                .byDefault()
                .register();
    }
}
