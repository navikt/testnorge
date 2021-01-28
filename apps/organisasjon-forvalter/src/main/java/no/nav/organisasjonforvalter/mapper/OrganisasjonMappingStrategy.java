package no.nav.organisasjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest.OrganisasjonRequest;
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
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Component
public class OrganisasjonMappingStrategy implements MappingStrategy {

    private static Dato getDate(LocalDate date) {
        return Dato.newBuilder()
                .setAar(date.getYear())
                .setMaaned(date.getMonthValue())
                .setDag(date.getDayOfMonth())
                .build();
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
                        target.setNaeringskode(isNotBlank(source.getNaeringskode()) ? Naeringskode.newBuilder().setKode(source.getNaeringskode())
                                .setHjelpeenhet(false)
                                .setGyldighetsdato(getDate(LocalDate.now()))
                                .build() : null);
                        target.setStiftelsesdato(Stiftelsesdato.newBuilder()
                                .setDato(getDate(nonNull(source.getStiftelsesdato()) ?
                                        source.getStiftelsesdato() : LocalDate.now()))
                                .build());
                        target.setInternettadresse(isNotBlank(source.getNettside()) ?
                                Internettadresse.newBuilder().setInternettadresse(source.getNettside()).build() : null);
                        target.setMobiltelefon(isNotBlank(source.getTelefon()) ? Telefon.newBuilder().setTlf(source.getTelefon()).build() : null);
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
    }
}
