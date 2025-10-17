package no.nav.dolly.bestilling.sykemelding.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.sykemelding.domain.dto.DetaljertSykemeldingRequestDTO;
import no.nav.dolly.bestilling.sykemelding.domain.dto.DetaljertSykemeldingRequestDTO.Adresse;
import no.nav.dolly.bestilling.sykemelding.domain.dto.DetaljertSykemeldingRequestDTO.Organisasjon;
import no.nav.dolly.bestilling.sykemelding.domain.dto.DetaljertSykemeldingRequestDTO.Pasient;
import no.nav.dolly.consumer.norg2.dto.Norg2EnhetResponse;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding.RsDetaljertSykemelding;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.data.pdlforvalter.v1.BostedadresseDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class SykemeldingMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsDetaljertSykemelding.class, DetaljertSykemeldingRequestDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsDetaljertSykemelding rsSykemelding,
                                        DetaljertSykemeldingRequestDTO request,
                                        MappingContext context) {
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
                            request.setDetaljer(DetaljertSykemeldingRequestDTO.Detaljer.builder()
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
                        pasient.setIdent(person.getIdent());
                        mapperFacade.map(person.getPerson().getNavn().stream()
                                .findFirst().orElse(new PdlPerson.Navn()), pasient);
                        pasient.setAdresse(mapperFacade.map(person.getPerson().getBostedsadresse().stream()
                                .findFirst().orElse(new BostedadresseDTO()), Adresse.class, context));
                        pasient.setFoedselsdato(person.getPerson().getFoedselsdato().stream()
                                .map(PdlPerson.Foedselsdato::getFoedselsdato)
                                .findFirst().orElse(null));
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
                .register();

        factory.classMap(BostedadresseDTO.class, Adresse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BostedadresseDTO kilde, Adresse dest, MappingContext context) {
                        var postnummere = (Map<String, String>) context.getProperty("postnummer");
                        if (nonNull(kilde.getVegadresse())) {
                            dest.setPostnummer(kilde.getVegadresse().getPostnummer());
                            dest.setBy(postnummere.get(kilde.getVegadresse().getPostnummer()));
                            dest.setGate(Stream.of(kilde.getVegadresse().getAdressenavn(),
                                            kilde.getVegadresse().getHusnummer(),
                                            kilde.getVegadresse().getHusbokstav())
                                    .filter(StringUtils::isNotBlank)
                                    .collect(Collectors.joining(" ")));
                            dest.setLand("NOR");
                        } else if (nonNull(kilde.getUtenlandskAdresse())) {
                            dest.setPostnummer(kilde.getUtenlandskAdresse().getPostkode());
                            dest.setBy(kilde.getUtenlandskAdresse().getBySted());
                            dest.setGate(kilde.getUtenlandskAdresse().getAdressenavnNummer());
                            dest.setLand(kilde.getUtenlandskAdresse().getLandkode());
                        }
                    }
                })
                .register();
    }
}
