package no.nav.dolly.bestilling.aareg.mapper;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsAaregArbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class AaregRequestMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsAaregArbeidsforhold.class, Arbeidsforhold.class)
                .customize(new CustomMapper<RsAaregArbeidsforhold, Arbeidsforhold>() {
                    @Override
                    public void mapAtoB(RsAaregArbeidsforhold rsArbeidsforhold,
                            Arbeidsforhold arbeidsforhold, MappingContext context) {

                        if (rsArbeidsforhold.getArbeidsgiver() instanceof RsOrganisasjon) {
                            arbeidsforhold.getArbeidsgiver().setAktoertype("ORG");
                        } else if (rsArbeidsforhold.getArbeidsgiver() instanceof RsAktoerPerson) {
                            arbeidsforhold.getArbeidsgiver().setAktoertype("PERS");
                        }
                    }
                })
                .byDefault()
                .register();
    }
}