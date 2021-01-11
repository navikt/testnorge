package no.nav.organisasjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest.OrganisasjonRequest;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Dato;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.DetaljertNavn;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Epost;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Formaal;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Internettadresse;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Knytning;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Maalform;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Naeringskode;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Sektorkode;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static java.util.Objects.nonNull;

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

                        organisasjon.getAdresser().forEach(adr -> adr.setOrganisasjon(organisasjon));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Organisasjon.class, no.nav.registre.testnorge.libs.avro.organisasjon.v1.Organisasjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Organisasjon source, no.nav.registre.testnorge.libs.avro.organisasjon.v1.Organisasjon target, MappingContext context) {
                        target.setNavn(DetaljertNavn.newBuilder().setNavn1(source.getOrganisasjonsnavn()).build());
                        target.setNaeringskode(Naeringskode.newBuilder().setKode(source.getNaeringskode())
                                .setHjelpeenhet(false)
                                .setGyldighetsdato(getDate(LocalDate.now()))
                                .build());
                        target.setSektorkode(Sektorkode.newBuilder().setSektorkode(source.getSektorkode()).build());
                        target.setFormaal(Formaal.newBuilder().setFormaal(source.getFormaal()).build());
                        target.setMaalform(Maalform.newBuilder().setMaalform(source.getMaalform()).build());
                        target.setEpost(Epost.newBuilder().setEpost(source.getEpost()).build());
                        target.setInternettadresse(Internettadresse.newBuilder().setInternettadresse(source.getNettside()).build());
                        source.getAdresser().forEach(adresse -> {
                            if (adresse.isForretningsadresse()) {
                                target.setForretningsadresse(mapperFacade.map(adresse, no.nav.registre.testnorge.libs.avro.organisasjon.v1.Adresse.class));
                            } else if (adresse.isPostadresse()) {
                                target.setPostadresse(mapperFacade.map(adresse, no.nav.registre.testnorge.libs.avro.organisasjon.v1.Adresse.class));
                            }
                        });
                        if (nonNull(source.getParent())) {
                            target.setKnytning(Knytning.newBuilder()
                                    .setJuridiskEnhet(source.getParent().getOrganisasjonsnummer()).build());
                        }
                    }
                })
                .byDefault()
                .register();
    }
}
