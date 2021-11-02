package no.nav.dolly.bestilling.aareg.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold.Periode;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsavtale;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.aareg.RsPermisjon;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Component
public class AaregRequestMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsAareg.class, Arbeidsforhold.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsAareg rsArbeidsforhold,
                                        Arbeidsforhold arbeidsforhold, MappingContext context) {

                        arbeidsforhold.setArbeidsforholdID(rsArbeidsforhold.getArbeidsforholdId());

                        if (nonNull(rsArbeidsforhold.getAntallTimerForTimeloennet()) && !rsArbeidsforhold.getAntallTimerForTimeloennet().isEmpty()) {
                            arbeidsforhold.setAntallTimerForTimeloennet(rsArbeidsforhold.getAntallTimerForTimeloennet());
                        }

                        arbeidsforhold.setArbeidsavtale(RsArbeidsavtale.builder()
                                .antallKonverterteTimer(rsArbeidsforhold.getArbeidsavtale().getAntallKonverterteTimer())
                                .arbeidstidsordning(rsArbeidsforhold.getArbeidsavtale().getArbeidstidsordning())
                                .avloenningstype(rsArbeidsforhold.getArbeidsavtale().getAvloenningstype())
                                .avtaltArbeidstimerPerUke(rsArbeidsforhold.getArbeidsavtale().getAvtaltArbeidstimerPerUke())
                                .endringsdatoStillingsprosent(null)
                                .stillingsprosent(rsArbeidsforhold.getArbeidsavtale().getStillingsprosent())
                                .yrke(rsArbeidsforhold.getArbeidsavtale().getYrke())
                                .endringsdatoLoenn(null)
                                .ansettelsesform(null)
                                .build());

                        if (nonNull(rsArbeidsforhold.getUtenlandsopphold()) && !rsArbeidsforhold.getUtenlandsopphold().isEmpty()) {
                            arbeidsforhold.setUtenlandsopphold(rsArbeidsforhold.getUtenlandsopphold());
                        }

                        if (rsArbeidsforhold.getArbeidsgiver() instanceof RsOrganisasjon) {
                            arbeidsforhold.setArbeidsgiver(RsOrganisasjon.builder()
                                    .orgnummer(((RsOrganisasjon) rsArbeidsforhold.getArbeidsgiver()).getOrgnummer())
                                    .build());
                            arbeidsforhold.getArbeidsgiver().setAktoertype("ORG");
                        } else if (rsArbeidsforhold.getArbeidsgiver() instanceof RsAktoerPerson) {
                            arbeidsforhold.setArbeidsgiver(RsAktoerPerson.builder()
                                    .ident(((RsAktoerPerson) rsArbeidsforhold.getArbeidsgiver()).getIdent())
                                    .identtype(((RsAktoerPerson) rsArbeidsforhold.getArbeidsgiver()).getIdenttype())
                                    .build());
                            arbeidsforhold.getArbeidsgiver().setAktoertype("PERS");
                        }
                        if (nonNull(rsArbeidsforhold.getAnsettelsesPeriode())) {
                            arbeidsforhold.setAnsettelsesPeriode(Periode.builder()
                                    .fom(rsArbeidsforhold.getAnsettelsesPeriode().getFom())
                                    .tom(rsArbeidsforhold.getAnsettelsesPeriode().getTom())
                                    .build());
                        }
                        arbeidsforhold.setArbeidsforholdstype(rsArbeidsforhold.getArbeidsforholdstype());

                        arbeidsforhold.setPermisjon((nonNull(rsArbeidsforhold.getPermisjon()) && !rsArbeidsforhold.getPermisjon().isEmpty())
                                || (nonNull(rsArbeidsforhold.getPermittering()) && !rsArbeidsforhold.getPermittering().isEmpty())
                                ? Stream.concat(
                                        rsArbeidsforhold.getPermisjon().stream(),
                                        mapperFacade.mapAsList(rsArbeidsforhold.getPermittering(), RsPermisjon.class).stream())
                                .collect(Collectors.toList())
                                : null);
                    }
                })
                .register();
    }
}