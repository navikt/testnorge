package no.nav.dolly.bestilling.sykemelding.mapper;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.sykemelding.domain.BestillingPersonWrapper;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest.Pasient;
import no.nav.dolly.domain.resultset.sykemelding.RsDetaljertSykemelding;
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

                        DetaljertSykemeldingRequest.Organisasjon organisasjon = DetaljertSykemeldingRequest.Organisasjon.builder()
                                .adresse(DetaljertSykemeldingRequest.Adresse.builder()
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
                    public void mapAtoB(Person person, Pasient request, MappingContext context) {

                        request.setNavKontor(person.getTknavn());
                        request.setTelefon(person.getTelefonnummer_1());
                        setRequestAdresse(person, request);
                    }
                })
                .byDefault()
                .register();
    }

    private void setRequestAdresse(Person person, Pasient request) {
        BoGateadresse pasientBoAdresse = null;
        RsPostadresse pasientPostAdresse = null;
        if (!person.getBoadresse().isEmpty()) {
            pasientBoAdresse = (BoGateadresse) person.getBoadresse().get(0);
        } else if (!person.getPostadresse().isEmpty()) {
            pasientPostAdresse = person.getPostadresse().get(0);
        } else {
            throw new NotFoundException("Person må ha enten BoAdresse eller PostAdresse!");
        }
        request.setAdresse(DetaljertSykemeldingRequest.Adresse.builder()
                .by(nonNull(pasientBoAdresse) ? pasientBoAdresse.getPostnr() : pasientPostAdresse.getPostLinje1())
                .gate(nonNull(pasientBoAdresse) ? pasientBoAdresse.getGateadresse() : pasientPostAdresse.getPostLinje1())
                .land(!person.getBoadresse().isEmpty() ? "NOR" : pasientPostAdresse.getPostLand())
                .postnummer(nonNull(pasientBoAdresse) ? pasientBoAdresse.getPostnr() : pasientPostAdresse.getPostLinje1())
                .build());
    }

}
