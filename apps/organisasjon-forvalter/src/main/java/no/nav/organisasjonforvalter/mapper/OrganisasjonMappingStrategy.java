package no.nav.organisasjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.organisasjonforvalter.consumer.OrganisasjonApiConsumer.AdresseDto;
import no.nav.organisasjonforvalter.consumer.OrganisasjonApiConsumer.OrganisasjonDto;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest.OrganisasjonRequest;
import no.nav.organisasjonforvalter.provider.rs.responses.RsAdresse;
import no.nav.organisasjonforvalter.provider.rs.responses.RsAdresse.AdresseType;
import no.nav.organisasjonforvalter.provider.rs.responses.RsOrganisasjon;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Adresse;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Dato;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.DetaljertNavn;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Internettadresse;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Naeringskode;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Stiftelsesdato;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Telefon;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.organisasjonforvalter.provider.rs.responses.RsAdresse.AdresseType.FADR;
import static no.nav.organisasjonforvalter.provider.rs.responses.RsAdresse.AdresseType.PADR;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Component
public class OrganisasjonMappingStrategy implements MappingStrategy {

    private static Dato getDate(LocalDate date) {
        LocalDate fixDate = nonNull(date) ? date : LocalDate.now();
        return Dato.newBuilder()
                .setAar(fixDate.getYear())
                .setMaaned(fixDate.getMonthValue())
                .setDag(fixDate.getDayOfMonth())
                .build();
    }

    private static Internettadresse getNettside(String nettSide) {
        return isNotBlank(nettSide) ? Internettadresse.newBuilder().setInternettadresse(nettSide).build() : null;
    }

    private static Telefon getTelefonnr(String telefonnr) {
        return isNotBlank(telefonnr) ? Telefon.newBuilder().setTlf(telefonnr).build() : null;
    }

    private static Naeringskode getNæringskode(Organisasjon source) {
        return isNotBlank(source.getNaeringskode()) ? Naeringskode.newBuilder().setKode(source.getNaeringskode())
                .setHjelpeenhet(false)
                .setGyldighetsdato(getDate(source.getStiftelsesdato()))
                .build() : null;
    }

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(OrganisasjonRequest.class, Organisasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(OrganisasjonRequest request, Organisasjon organisasjon, MappingContext context) {
                        if (isNull(request.getStiftelsesdato())) {
                            organisasjon.setStiftelsesdato(LocalDate.now());
                        }
                        organisasjon.getAdresser().forEach(adr -> adr.setOrganisasjon(organisasjon));
                    }
                })
                .exclude("underenheter")
                .byDefault()
                .register();

        factory.classMap(Organisasjon.class, no.nav.registre.testnorge.libs.avro.organisasjon.v1.Organisasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Organisasjon source, no.nav.registre.testnorge.libs.avro.organisasjon.v1.Organisasjon target, MappingContext context) {
                        target.setOrgnummer(source.getOrganisasjonsnummer());
                        target.setNavn(DetaljertNavn.newBuilder().setNavn1(source.getOrganisasjonsnavn()).build());
                        target.setNaeringskode(getNæringskode(source));
                        target.setStiftelsesdato(Stiftelsesdato.newBuilder()
                                .setDato(getDate(source.getStiftelsesdato()))
                                .build());
                        target.setInternettadresse(getNettside(source.getNettside()));
                        target.setMobiltelefon(getTelefonnr(source.getTelefon()));
                        target.setTelefon(getTelefonnr(source.getTelefon()));
                        source.getAdresser().forEach(adresse -> {
                            if (adresse.isForretningsadresse()) {
                                target.setForretningsadresse(mapperFacade.map(adresse, Adresse.class));
                            } else if (adresse.isPostadresse()) {
                                target.setPostadresse(mapperFacade.map(adresse, Adresse.class));
                            }
                        });

                        target.setUnderenheter(mapperFacade.mapAsList(source.getUnderenheter(), no.nav.registre.testnorge.libs.avro.organisasjon.v1.Organisasjon.class));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(OrganisasjonDto.class, RsOrganisasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(OrganisasjonDto source, RsOrganisasjon target, MappingContext context) {
                        target.setOrganisasjonsnummer(source.getOrgnummer());
                        target.setEnhetstype(source.getEnhetType());
                        target.setOrganisasjonsnavn(source.getNavn());
                        if (nonNull(source.getPostadresse())) {
                            target.getAdresser().add(mapAdresse(source.getPostadresse(), PADR));
                        }
                        if (nonNull(source.getForretningsadresser())) {
                            target.getAdresser().add(mapAdresse(source.getForretningsadresser(), FADR));
                        }
                    }

                    private RsAdresse mapAdresse(AdresseDto adresseDto, AdresseType type) {
                        RsAdresse adresse = mapperFacade.map(adresseDto, RsAdresse.class);
                        adresse.setAdressetype(type);
                        return adresse;
                    }
                })
                .byDefault()
                .register();
    }
}
