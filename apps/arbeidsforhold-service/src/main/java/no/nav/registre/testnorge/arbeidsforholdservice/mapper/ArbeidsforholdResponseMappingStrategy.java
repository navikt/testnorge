package no.nav.registre.testnorge.arbeidsforholdservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.ArbeidsforholdDTO;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.PeriodeDTO;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.PermisjonPermitteringDTO;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.UtenlandsoppholdDTO;
import no.nav.registre.testnorge.arbeidsforholdservice.domain.v2.Arbeidsforhold;
import no.nav.registre.testnorge.arbeidsforholdservice.domain.v2.Arbeidsforhold.RsFartoy;
import no.nav.registre.testnorge.arbeidsforholdservice.domain.v2.Arbeidsforhold.RsUtenlandsopphold;
import no.nav.registre.testnorge.libs.dto.ameldingservice.v1.PermisjonDTO;
import org.springframework.stereotype.Component;

import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class ArbeidsforholdResponseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(ArbeidsforholdDTO.class, Arbeidsforhold.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(ArbeidsforholdDTO arbeidsforholdResponse, Arbeidsforhold arbeidsforhold, MappingContext context) {

                        arbeidsforhold.setArbeidsforholdstype(arbeidsforholdResponse.getType());
                        arbeidsforhold.setArbeidsforholdID(arbeidsforholdResponse.getArbeidsforholdId());

                        if (isNull(arbeidsforhold.getAnsettelsesPeriode())) {
                            arbeidsforhold.setAnsettelsesPeriode(Arbeidsforhold.RsPeriodeAareg.builder().build());
                        }
                        arbeidsforhold.setArbeidsgiver(arbeidsforholdResponse.getArbeidsgiver().getType().equals("Organisasjon")
                                ? Arbeidsforhold.RsOrganisasjon.builder()
                                .orgnummer(arbeidsforholdResponse.getArbeidsgiver().getOrganisasjonsnummer())
                                .build()
                                : Arbeidsforhold.RsAktoerPerson.builder()
                                .ident(arbeidsforholdResponse.getArbeidsgiver().getIdent())
                                .build()
                        );

                        arbeidsforhold.getAnsettelsesPeriode().setFom(
                                nonNull(arbeidsforholdResponse.getAnsettelsesperiode())
                                        && nonNull(arbeidsforholdResponse.getAnsettelsesperiode().getPeriode())
                                        ? arbeidsforholdResponse.getAnsettelsesperiode().getPeriode().getFom()
                                        : null);
                        arbeidsforhold.getAnsettelsesPeriode().setTom(
                                nonNull(arbeidsforholdResponse.getAnsettelsesperiode())
                                        && nonNull(arbeidsforholdResponse.getAnsettelsesperiode().getPeriode())
                                        ? arbeidsforholdResponse.getAnsettelsesperiode().getPeriode().getTom()
                                        : null);
                        if (nonNull(arbeidsforholdResponse.getArbeidsavtaler()) && !arbeidsforholdResponse.getArbeidsavtaler().isEmpty()) {
                            arbeidsforhold.setArbeidsavtale(Arbeidsforhold.RsArbeidsavtale.builder()
                                    .ansettelsesform(arbeidsforholdResponse.getArbeidsavtaler().get(0).getAnsettelsesform())
                                    .arbeidstidsordning(arbeidsforholdResponse.getArbeidsavtaler().get(0).getArbeidstidsordning())
                                    .avtaltArbeidstimerPerUke(arbeidsforholdResponse.getArbeidsavtaler().get(0).getAntallTimerPrUke().doubleValue())
                                    .endringsdatoLoenn(nonNull(arbeidsforholdResponse.getArbeidsavtaler().get(0).getSistLoennsendring())
                                            ? arbeidsforholdResponse.getArbeidsavtaler().get(0).getSistLoennsendring()
                                            : null)
                                    .sisteLoennsendringsdato(nonNull(arbeidsforholdResponse.getArbeidsavtaler().get(0).getSistLoennsendring())
                                            ? arbeidsforholdResponse.getArbeidsavtaler().get(0).getSistLoennsendring()
                                            : null)
                                    .stillingsprosent(arbeidsforholdResponse.getArbeidsavtaler().get(0).getStillingsprosent().doubleValue())
                                    .yrke(arbeidsforholdResponse.getArbeidsavtaler().get(0).getYrke())
                                    .build());

                            arbeidsforhold.setFartoy(singletonList(RsFartoy.builder()
                                    .skipsregister(arbeidsforholdResponse.getArbeidsavtaler().get(0).getSkipsregister())
                                    .skipstype(arbeidsforholdResponse.getArbeidsavtaler().get(0).getSkipstype())
                                    .fartsomraade(arbeidsforholdResponse.getArbeidsavtaler().get(0).getFartsomraade())
                                    .build()));
                        }

                        arbeidsforhold.setArbeidstaker(Arbeidsforhold.RsPersonAareg.builder()
                                .identtype(arbeidsforholdResponse.getArbeidstaker().getType())
                                .ident(arbeidsforholdResponse.getArbeidstaker().getOffentligIdent())
                                .build());

                        if (nonNull(arbeidsforholdResponse.getUtenlandsopphold())) {
                            arbeidsforhold.setUtenlandsopphold(mapperFacade.mapAsList(arbeidsforholdResponse.getUtenlandsopphold(), RsUtenlandsopphold.class));
                        }
                        if (nonNull(arbeidsforholdResponse.getPermisjonPermitteringer())) {
                            arbeidsforhold.setPermisjon(mapperFacade.mapAsList(arbeidsforholdResponse.getPermisjonPermitteringer(), PermisjonDTO.class));
                        }
                    }
                })
                .register();

        factory.classMap(PermisjonPermitteringDTO.class, PermisjonDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PermisjonPermitteringDTO permisjonPermitteringDTO, PermisjonDTO permisjonDTO, MappingContext context) {

                        permisjonDTO.setPermisjonId(permisjonPermitteringDTO.getPermisjonPermitteringId());
                        permisjonDTO.setStartdato(permisjonPermitteringDTO.getPeriode().getFom());
                        permisjonDTO.setSluttdato(permisjonPermitteringDTO.getPeriode().getTom());
                        permisjonDTO.setPermisjonsprosent(permisjonPermitteringDTO.getProsent());
                        permisjonDTO.setBeskrivelse(permisjonPermitteringDTO.getType());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(UtenlandsoppholdDTO.class, RsUtenlandsopphold.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(UtenlandsoppholdDTO utenlandsoppholdDTO, RsUtenlandsopphold rsUtenlandsopphold, MappingContext context) {

                        rsUtenlandsopphold.setLand(utenlandsoppholdDTO.getLandkode());
                        if (nonNull(utenlandsoppholdDTO.getPeriode())) {
                            rsUtenlandsopphold.setPeriode(new PeriodeDTO(utenlandsoppholdDTO.getPeriode().getFom(), utenlandsoppholdDTO.getPeriode().getTom()));
                        }
                    }
                })
                .byDefault()
                .register();
    }

}