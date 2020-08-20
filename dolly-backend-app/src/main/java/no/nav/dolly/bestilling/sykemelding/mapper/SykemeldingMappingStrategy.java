package no.nav.dolly.bestilling.sykemelding.mapper;

import static java.util.Objects.isNull;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.sykemelding.domain.BestillingPersonWrapper;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest;
import no.nav.dolly.domain.resultset.sykemelding.RsDetaljertSykemelding;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.adresse.BoGateadresse;
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
                        mapperFacade.map(wrapper.getPerson(), request);
                    }
                })
                .register();

        factory.classMap(RsDetaljertSykemelding.class, DetaljertSykemeldingRequest.class)
                .customize(new CustomMapper<RsDetaljertSykemelding, DetaljertSykemeldingRequest>() {
                    @Override
                    public void mapAtoB(RsDetaljertSykemelding rsSykemelding, DetaljertSykemeldingRequest request, MappingContext context) {

                        DetaljertSykemeldingRequest.Organisasjon organisasjon = DetaljertSykemeldingRequest.Organisasjon.builder()
                                .adresse(DetaljertSykemeldingRequest.Adresse.builder()
                                        .postnummer("6789")
                                        .land("NOR")
                                        .gate("Syntegaten")
                                        .by("Synteby")
                                        .build())
                                .navn("Synt & Co.")
                                .orgNr("123")
                                .build();

                        if (isNull(rsSykemelding.getDetaljer())) {
                            request.setDetaljer(DetaljertSykemeldingRequest.Detaljer.builder()
                                    .arbeidsforEtterEndtPeriode(true)
                                    .build());
                        }

                        if (isNull(rsSykemelding.getMottaker())) {
                            request.setMottaker(organisasjon);
                        }

                        if (isNull(rsSykemelding.getSender())) {
                            request.setSender(organisasjon);
                        }
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Person.class, DetaljertSykemeldingRequest.class)
                .customize(new CustomMapper<Person, DetaljertSykemeldingRequest>() {
                    @Override
                    public void mapAtoB(Person person, DetaljertSykemeldingRequest request, MappingContext context) {
                        super.mapAtoB(person, request, context);
                        BoGateadresse pasientBoAdresse = (BoGateadresse) person.getBoadresse().get(0);

                        fyllRequestPasient(request, person, pasientBoAdresse);
                    }
                })
                .byDefault()
                .register();
    }

    private void fyllRequestPasient(DetaljertSykemeldingRequest detaljertSykemeldingRequest, Person pasient, BoGateadresse pasientAdresse) {
        detaljertSykemeldingRequest.setPasient(DetaljertSykemeldingRequest.Pasient.builder()
                .fornavn(pasient.getFornavn())
                .etternavn(pasient.getEtternavn())
                .mellomnavn(isNull(pasient.getMellomnavn()) ? null : pasient.getMellomnavn())
                .foedselsdato(pasient.getFoedselsdato().toLocalDate())
                .ident(pasient.getIdent())
                .navKontor(pasient.getTknavn())
                .telefon(isNull(pasient.getTelefonnummer_1()) ? null : pasient.getTelefonnummer_1())
                .adresse(DetaljertSykemeldingRequest.Adresse.builder()
                        .by(pasient.getBoadresse().get(0).getPostnr())
                        .gate(pasientAdresse.getGateadresse())
                        .land(pasient.getStatsborgerskap().get(0).getStatsborgerskap())
                        .postnummer(pasient.getBoadresse().get(0).getPostnr())
                        .build())
                .build());
    }
}
