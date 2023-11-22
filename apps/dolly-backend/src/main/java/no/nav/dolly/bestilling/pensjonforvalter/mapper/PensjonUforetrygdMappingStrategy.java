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
import java.util.List;

import static java.util.Objects.isNull;
import static no.nav.dolly.bestilling.pensjonforvalter.mapper.PensjonMappingSupportUtils.getFoedselsdato;
import static no.nav.dolly.bestilling.pensjonforvalter.mapper.PensjonMappingSupportUtils.getForrigeMaaned;
import static no.nav.dolly.bestilling.pensjonforvalter.mapper.PensjonMappingSupportUtils.getNesteMaaned;
import static no.nav.dolly.bestilling.pensjonforvalter.mapper.PensjonMappingSupportUtils.getRandomAnsatt;
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

                        pensjonUforetrygdRequest.setUforetidspunkt(
                                nullcheckSetDefaultValue(uforetrygd.getUforetidspunkt(), getForrigeMaaned()));
                        pensjonUforetrygdRequest.setKravFremsattDato(
                                nullcheckSetDefaultValue(uforetrygd.getKravFremsattDato(), LocalDate.now()));
                        pensjonUforetrygdRequest.setOnsketVirkningsDato(
                                nullcheckSetDefaultValue(uforetrygd.getOnsketVirkningsDato(), getNesteMaaned()));

                        if (isNull(uforetrygd.getMinimumInntektForUforhetType())) {
                            hovedperson
                                    .flatMap(person -> person.getSivilstand().stream()
                                            .filter(PdlPerson.Sivilstand::isGift)
                                            .findFirst())
                                    .ifPresentOrElse(statusGift ->
                                            pensjonUforetrygdRequest.setMinimumInntektForUforhetType(UforeType.GIFT),
                                            () -> hovedperson
                                                    .flatMap(person1 -> person1.getFoedsel().stream()
                                                            .findFirst())
                                                    .ifPresent(foedsel -> pensjonUforetrygdRequest.setMinimumInntektForUforhetType(

                                                            ChronoUnit.YEARS.between(getFoedselsdato(foedsel),
                                                                    pensjonUforetrygdRequest.getUforetidspunkt()) > 23 ?
                                                                    UforeType.ENSLIG :
                                                                    UforeType.UNGUFOR))
                                    );
                        }

                        pensjonUforetrygdRequest.setSaksbehandler(nullcheckSetDefaultValue(uforetrygd.getSaksbehandler(), getRandomAnsatt()));
                        pensjonUforetrygdRequest.setAttesterer(nullcheckSetDefaultValue(uforetrygd.getAttesterer(), getRandomAnsatt()));
                    }
                })
                .byDefault()
                .register();
    }
}
