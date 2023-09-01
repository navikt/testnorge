package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonUforetrygdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonUforetrygdRequest.UforeType;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

@Component
public class PensjonUforetrygdMappingStrategy implements MappingStrategy {
    @Override
    public void register(MapperFactory factory) {
        factory.classMap(PensjonData.Uforetrygd.class, PensjonUforetrygdRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PensjonData.Uforetrygd uforetrygd, PensjonUforetrygdRequest pensjonUforetrygdRequest, MappingContext context) {

                        var ident = (String) context.getProperty("ident");
                        var miljoer = (List<String>) context.getProperty("miljoer");
                        var persondata = (List<PdlPersonBolk.PersonBolk>) context.getProperty("persondata");
                        var hovedperson = persondata.stream()
                                .filter(personBolk -> personBolk.getIdent().equals(ident))
                                .map(PdlPersonBolk.PersonBolk::getPerson)
                                .findFirst();

                        pensjonUforetrygdRequest.setFnr(ident);
                        pensjonUforetrygdRequest.setMiljoer(miljoer);

                        var fraDato = Stream.of(uforetrygd.getUforetidspunkt(),
                                        uforetrygd.getKravFremsattDato(),
                                        uforetrygd.getOnsketVirkningsDato())
                                .filter(Objects::nonNull)
                                .min(LocalDate::compareTo)
                                .orElse(LocalDate.now());

                        pensjonUforetrygdRequest.setUforetidspunkt(
                                nullcheckSetDefaultValue(uforetrygd.getUforetidspunkt(), fraDato));
                        pensjonUforetrygdRequest.setKravFremsattDato(
                                nullcheckSetDefaultValue(uforetrygd.getKravFremsattDato(), fraDato));
                        pensjonUforetrygdRequest.setOnsketVirkningsDato(
                                nullcheckSetDefaultValue(uforetrygd.getOnsketVirkningsDato(), fraDato));

                        if (isNull(uforetrygd.getMinimumInntektForUforhetType())) {
                            hovedperson
                                    .flatMap(person -> person.getSivilstand().stream()
                                            .max(Comparator.comparing(PdlPerson.Sivilstand::getId)))
                                    .ifPresent(sivilstatus -> {
                                        if (sivilstatus.isGift()) {
                                            pensjonUforetrygdRequest.setMinimumInntektForUforhetType(UforeType.GIFT);

                                        } else {
                                            hovedperson
                                                    .flatMap(person1 -> person1.getFoedsel().stream()
                                                            .max(Comparator.comparing(PdlPerson.Foedsel::getId)))
                                                    .ifPresent(foedsel -> pensjonUforetrygdRequest.setMinimumInntektForUforhetType(

                                                            ChronoUnit.YEARS.between(getFoedselsdato(foedsel), fraDato) > 23 ?
                                                                    UforeType.ENSLIG :
                                                                    UforeType.UNGUFOR));
                                        }
                                    });
                        }

                        var ansatt = Stream.of(uforetrygd.getAttesterer(), uforetrygd.getSaksbehandler())
                                .filter(Objects::nonNull)
                                .findFirst()
                                .orElse("Dolly");
                        pensjonUforetrygdRequest.setSaksbehandler(nullcheckSetDefaultValue(uforetrygd.getSaksbehandler(), ansatt));
                        pensjonUforetrygdRequest.setAttesterer(nullcheckSetDefaultValue(uforetrygd.getAttesterer(), ansatt));
                    }
                })
                .byDefault()
                .register();
    }

    private static LocalDate getFoedselsdato(PdlPerson.Foedsel foedsel) {

        return nullcheckSetDefaultValue(foedsel.getFoedselsdato(),
                LocalDate.of(foedsel.getFoedselsaar(), 1, 1));
    }
}
