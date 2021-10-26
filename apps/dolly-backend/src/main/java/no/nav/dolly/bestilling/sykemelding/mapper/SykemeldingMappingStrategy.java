package no.nav.dolly.bestilling.sykemelding.mapper;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.sykemelding.domain.BestillingPersonWrapper;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest.Adresse;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest.Organisasjon;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest.Pasient;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding.RsDetaljertSykemelding;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.adresse.BoGateadresse;
import no.nav.dolly.domain.resultset.tpsf.adresse.RsPostadresse;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class SykemeldingMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(BestillingPersonWrapper.class, DetaljertSykemeldingRequest.class)
                .customize(new CustomMapper<BestillingPersonWrapper, DetaljertSykemeldingRequest>() {
                    @Override
                    public void mapAtoB(BestillingPersonWrapper wrapper, DetaljertSykemeldingRequest request, MappingContext context) {
                        mapperFacade.map(wrapper.getSykemelding(), request);
                        if (isNull(request.getPasient())) {
                            request.setPasient(new Pasient());
                        }
                        mapperFacade.map(wrapper.getPerson(), request.getPasient());
                    }
                })
                .register();

        factory.classMap(RsDetaljertSykemelding.class, DetaljertSykemeldingRequest.class)
                .customize(new CustomMapper<RsDetaljertSykemelding, DetaljertSykemeldingRequest>() {
                    @Override
                    public void mapAtoB(RsDetaljertSykemelding rsSykemelding, DetaljertSykemeldingRequest request, MappingContext context) {

                        Organisasjon organisasjon = Organisasjon.builder()
                                .adresse(Adresse.builder()
                                        .postnummer("0557")
                                        .land("NOR")
                                        .gate("Sannergata 2")
                                        .by("Oslo")
                                        .build())
                                .navn("Mini-Norge Legekontor")
                                .orgNr("992741090")
                                .build();

                        if (isNull(rsSykemelding.getDetaljer())) {
                            request.setDetaljer(DetaljertSykemeldingRequest.Detaljer.builder()
                                    .arbeidsforEtterEndtPeriode(true)
                                    .build());
                        }

                        if (isNull(rsSykemelding.getSender())) {
                            request.setSender(organisasjon);
                        }
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Person.class, Pasient.class)
                .customize(new CustomMapper<Person, Pasient>() {
                    @Override
                    public void mapAtoB(Person person, Pasient pasient, MappingContext context) {

                        pasient.setNavKontor(person.getTknavn());
                        pasient.setTelefon(person.getTelefonnummer_1());
                        pasient.setAdresse(getAdresse(person));
                    }
                })
                .byDefault()
                .register();
    }

    private Adresse getAdresse(Person person) {

        if (!person.getBoadresse().isEmpty()) {
            BoGateadresse pasientBoAdresse = (BoGateadresse) person.getBoadresse().get(0);
            return Adresse.builder()
                    .gate(pasientBoAdresse.getGateadresse())
                    .land("NOR")
                    .postnummer(pasientBoAdresse.getPostnr())
                    .build();
        } else if (!person.getPostadresse().isEmpty()) {
            RsPostadresse pasientPostAdresse = person.getPostadresse().get(0);
            Adresse adresse = Adresse.builder()
                    .gate(getGyldigPostlinje(pasientPostAdresse))
                    .land(pasientPostAdresse.getPostLand())
                    .build();
            if (pasientPostAdresse.isNorsk()) {
                adresse.setBy(getPostNrOgSted(pasientPostAdresse).substring(4).trim());
                adresse.setPostnummer(getPostNrOgSted(pasientPostAdresse).substring(0, 4));
            }
            return adresse;
        } else {
            throw new NotFoundException("Person må ha enten BoAdresse eller PostAdresse!");
        }
    }

    private String getGyldigPostlinje(RsPostadresse pasientPostAdresse) {

        return String.format("%s%s%s", pasientPostAdresse.getPostLinje1(),
                isNotBlank(pasientPostAdresse.getPostLinje2()) ? ", " + pasientPostAdresse.getPostLinje2() : "",
                isNotBlank(pasientPostAdresse.getPostLinje3()) ? ", " + pasientPostAdresse.getPostLinje3() : "");
    }

    private String getPostNrOgSted(RsPostadresse postadresse) {

        if (isNotBlank(postadresse.getPostLinje3())) {
            return postadresse.getPostLinje3();
        }
        if (isNotBlank(postadresse.getPostLinje2())) {
            return postadresse.getPostLinje2();
        }
        return postadresse.getPostLinje1();
    }

}
