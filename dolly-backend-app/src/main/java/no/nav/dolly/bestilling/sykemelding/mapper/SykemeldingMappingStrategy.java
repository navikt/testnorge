package no.nav.dolly.bestilling.sykemelding.mapper;

import static java.util.Objects.isNull;

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
                    .by(pasientBoAdresse.getPostnr())
                    .gate(pasientBoAdresse.getGateadresse())
                    .land("NOR")
                    .postnummer(pasientBoAdresse.getPostnr())
                    .build();

        } else if (!person.getPostadresse().isEmpty()) {
            RsPostadresse pasientPostAdresse = person.getPostadresse().get(0);
            return Adresse.builder()
                    .gate(pasientPostAdresse.getPostLinje1())
                    .land(pasientPostAdresse.getPostLand())
                    .postnummer(pasientPostAdresse.getPostLinje1())
                    .build();
        } else {
            throw new NotFoundException("Person må ha enten BoAdresse eller PostAdresse!");
        }
    }

}
