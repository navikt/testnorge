package no.nav.dolly.bestilling.sykemelding.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest.Adresse;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest.Organisasjon;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest.Pasient;
import no.nav.dolly.bestilling.sykemelding.domain.SyntSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.dto.Norg2EnhetResponse;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding.RsDetaljertSykemelding;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class SykemeldingMappingStrategy implements MappingStrategy {

    private static final String STANDARD_ARBEIDSFORHOLD_ID = "1";

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsSykemelding.RsSyntSykemelding.class, SyntSykemeldingRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsSykemelding.RsSyntSykemelding kilde,
                                        SyntSykemeldingRequest destinasjon, MappingContext context) {

                        var person = ((PdlPersonBolk.Data) context.getProperty("persondata"))
                                .getHentPersonBolk().stream()
                                .filter(personBolk -> nonNull(personBolk.getPerson()))
                                .findFirst().orElse(PdlPersonBolk.PersonBolk.builder()
                                        .person(new PdlPerson.Person())
                                        .build());

                        destinasjon.setIdent(person.getIdent());

                        if (isNotBlank(destinasjon.getOrgnummer()) && isBlank(destinasjon.getArbeidsforholdId())) {
                            destinasjon.setArbeidsforholdId(STANDARD_ARBEIDSFORHOLD_ID);
                        }
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsDetaljertSykemelding.class, DetaljertSykemeldingRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsDetaljertSykemelding rsSykemelding, DetaljertSykemeldingRequest request, MappingContext context) {

                        request.setPasient(mapperFacade.map(
                                (PdlPersonBolk.Data) context.getProperty("persondata"), Pasient.class, context));

                        request.setSender(nonNull(request.getSender()) ?
                                request.getSender() :
                                Organisasjon.builder()
                                        .adresse(Adresse.builder()
                                                .postnummer("0557")
                                                .land("NOR")
                                                .gate("Sannergata 2")
                                                .by("Oslo")
                                                .build())
                                        .navn("Mini-Norge Legekontor")
                                        .orgNr("992741090")
                                        .build());

                        if (isNull(rsSykemelding.getDetaljer())) {
                            request.setDetaljer(DetaljertSykemeldingRequest.Detaljer.builder()
                                    .arbeidsforEtterEndtPeriode(true)
                                    .build());
                        }
                    }
                })
                .byDefault()
                .register();

        factory.classMap(PdlPersonBolk.Data.class, Pasient.class)
                .customize(new CustomMapper<>() {

                    @Override
                    public void mapAtoB(PdlPersonBolk.Data persondata, Pasient pasient, MappingContext context) {

                        var person = persondata.getHentPersonBolk().stream()
                                .filter(personBolk -> nonNull(personBolk.getPerson()))
                                .findFirst().orElse(PdlPersonBolk.PersonBolk.builder()
                                        .person(new PdlPerson.Person())
                                        .build());

                        mapperFacade.map(person.getPerson().getNavn().stream()
                                .findFirst().orElse(new PdlPerson.Navn()), pasient);
                        pasient.setAdresse(mapperFacade.map(person.getPerson().getBostedsadresse().stream()
                                .findFirst().orElse(new BostedadresseDTO()), Adresse.class, context));

                        pasient.setTelefon(person.getPerson().getTelefonnummer().stream()
                                .filter(telefonnummer -> telefonnummer.getPrioritet() == 1)
                                .map(telefonnummer -> String.format("%s %s",
                                        telefonnummer.getLandskode(),
                                        telefonnummer.getNummer()))
                                .findFirst().orElse(null));

                        var norg2enhet = (Norg2EnhetResponse) context.getProperty("norg2Enhet");
                        pasient.setNavKontor(nonNull(norg2enhet) ? norg2enhet.getEnhetNr() : null);
                    }
                })
                .byDefault()
                .register();

        factory.classMap(BostedadresseDTO.class, Adresse.class)
                .customize(new CustomMapper<>() {

                    @Override
                    public void mapAtoB(BostedadresseDTO kilde, Adresse destinasjon, MappingContext context) {

                        var postnummere = (Map<String, String>) context.getProperty("postnummer");

                        if (nonNull(kilde.getVegadresse())) {
                            destinasjon.setPostnummer(kilde.getVegadresse().getPostnummer());
                            destinasjon.setBy(postnummere.get(kilde.getVegadresse().getPostnummer()));
                            destinasjon.setGate(Stream.of(kilde.getVegadresse().getAdressenavn(),
                                            kilde.getVegadresse().getHusnummer(),
                                            kilde.getVegadresse().getHusbokstav())
                                    .filter(StringUtils::isNotBlank)
                                    .collect(Collectors.joining(" ")));
                            destinasjon.setLand("NOR");

                        } else if (nonNull(kilde.getUtenlandskAdresse())) {
                            destinasjon.setPostnummer(kilde.getUtenlandskAdresse().getPostkode());
                            destinasjon.setBy(kilde.getUtenlandskAdresse().getBySted());
                            destinasjon.setGate(kilde.getUtenlandskAdresse().getAdressenavnNummer());
                            destinasjon.setLand(kilde.getUtenlandskAdresse().getLandkode());
                        }
                    }
                })
                .register();
    }
}
