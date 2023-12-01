package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonUforetrygdRequest;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

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
                        var navEnhet = (String) context.getProperty("navEnhet");

                        pensjonUforetrygdRequest.setFnr(ident);
                        pensjonUforetrygdRequest.setMiljoer(miljoer);

                        pensjonUforetrygdRequest.setUforetidspunkt(
                                nullcheckSetDefaultValue(uforetrygd.getUforetidspunkt(), getForrigeMaaned()));
                        pensjonUforetrygdRequest.setKravFremsattDato(
                                nullcheckSetDefaultValue(uforetrygd.getKravFremsattDato(), LocalDate.now()));
                        pensjonUforetrygdRequest.setOnsketVirkningsDato(
                                nullcheckSetDefaultValue(uforetrygd.getOnsketVirkningsDato(), getNesteMaaned()));
                        pensjonUforetrygdRequest.setNavEnhetId(
                                nullcheckSetDefaultValue(uforetrygd.getNavEnhetId(), navEnhet));

                        pensjonUforetrygdRequest.setSaksbehandler(nullcheckSetDefaultValue(uforetrygd.getSaksbehandler(), getRandomAnsatt()));
                        pensjonUforetrygdRequest.setAttesterer(nullcheckSetDefaultValue(uforetrygd.getAttesterer(), getRandomAnsatt()));
                    }
                })
                .byDefault()
                .register();
    }
}
