package no.nav.dolly.bestilling.aareg.mapper;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class AaregRequestMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(ArbeidsforholdMapper.PersonArbeidsforhold.class, Arbeidsforhold.class)
                .customize(new CustomMapper<ArbeidsforholdMapper.PersonArbeidsforhold, Arbeidsforhold>() {
                    @Override
                    public void mapAtoB(ArbeidsforholdMapper.PersonArbeidsforhold personArbeidsforhold,
                            Arbeidsforhold arbeidsforhold, MappingContext context) {

                        if (personArbeidsforhold.getArbeidsforhold().getArbeidsgiver() instanceof RsOrganisasjon) {
                            arbeidsforhold.getArbeidsgiver().setAktoertype("ORG");
                        } else if (personArbeidsforhold.getArbeidsforhold().getArbeidsgiver() instanceof RsAktoerPerson) {
                            arbeidsforhold.getArbeidsgiver().setAktoertype("PERS");
                        }
                    }
                })
                .byDefault()
                .register();
    }
}