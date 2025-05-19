package no.nav.dolly.bestilling.aareg.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.aareg.AaregClient;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.aareg.RsAktoer;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsAnsettelsesPeriode;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsavtale;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.aareg.RsPermisjon;
import no.nav.dolly.domain.resultset.aareg.RsPermittering;
import no.nav.dolly.domain.resultset.aareg.RsUtenlandsopphold;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.aareg.v1.Ansettelsesperiode;
import no.nav.testnav.libs.dto.aareg.v1.AntallTimerForTimeloennet;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsavtale;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.dto.aareg.v1.ForenkletOppgjoersordningArbeidsavtale;
import no.nav.testnav.libs.dto.aareg.v1.FrilanserArbeidsavtale;
import no.nav.testnav.libs.dto.aareg.v1.MaritimArbeidsavtale;
import no.nav.testnav.libs.dto.aareg.v1.OpplysningspliktigArbeidsgiver;
import no.nav.testnav.libs.dto.aareg.v1.OrdinaerArbeidsavtale;
import no.nav.testnav.libs.dto.aareg.v1.Organisasjon;
import no.nav.testnav.libs.dto.aareg.v1.Periode;
import no.nav.testnav.libs.dto.aareg.v1.PermisjonPermittering;
import no.nav.testnav.libs.dto.aareg.v1.Person;
import no.nav.testnav.libs.dto.aareg.v1.Utenlandsopphold;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class AaregRequestMappingStrategy implements MappingStrategy {

    private static LocalDate toDate(LocalDateTime dateTime) {

        return nonNull(dateTime) ? dateTime.toLocalDate() : null;
    }

    private static Double toDouble(BigDecimal value) {

        return nonNull(value) ? value.doubleValue() : null;
    }

    private static OpplysningspliktigArbeidsgiver getArbeidsgiver(RsAktoer arbeidsgiver) {

        if (arbeidsgiver instanceof RsOrganisasjon organisasjon) {
            return Organisasjon.builder()
                    .organisasjonsnummer(organisasjon.getOrgnummer())
                    .build();
        } else if (arbeidsgiver instanceof RsAktoerPerson aktoerPerson) {
            return Person.builder()
                    .offentligIdent(aktoerPerson.getIdent())
                    .build();
        }
        return null;
    }

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsAareg.class, Arbeidsforhold.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsAareg rsArbeidsforhold,
                                        Arbeidsforhold arbeidsforhold, MappingContext context) {

                        arbeidsforhold.setArbeidstaker(Person.builder()
                                .offentligIdent((String) context.getProperty(AaregClient.IDENT))
                                .build());

                        arbeidsforhold.setArbeidsforholdId(rsArbeidsforhold.getArbeidsforholdId());

                        arbeidsforhold.getAntallTimerForTimeloennet()
                                .addAll(mapperFacade.mapAsList(rsArbeidsforhold.getAntallTimerForTimeloennet(), AntallTimerForTimeloennet.class));

                        arbeidsforhold.getArbeidsavtaler().addAll(getArbeidsavtale(rsArbeidsforhold));
                        arbeidsforhold.setUtenlandsopphold(mapperFacade.mapAsList(rsArbeidsforhold.getUtenlandsopphold(), Utenlandsopphold.class));

                        arbeidsforhold.setArbeidsgiver(getArbeidsgiver(rsArbeidsforhold.getArbeidsgiver()));

                        arbeidsforhold.setType(rsArbeidsforhold.getArbeidsforholdstype());

                        arbeidsforhold.setAnsettelsesperiode(getAnsettelsesperiode(rsArbeidsforhold.getAnsettelsesPeriode()));

                        arbeidsforhold.getPermisjonPermitteringer().addAll(mapperFacade.mapAsList(rsArbeidsforhold.getPermisjon(), PermisjonPermittering.class));
                        arbeidsforhold.getPermisjonPermitteringer().addAll(mapperFacade.mapAsList(rsArbeidsforhold.getPermittering(), PermisjonPermittering.class));

                        arbeidsforhold.setNavArbeidsforholdPeriode(rsArbeidsforhold.getNavArbeidsforholdPeriode());
                        arbeidsforhold.setIsOppdatering(rsArbeidsforhold.getIsOppdatering());

                        arbeidsforhold.setInnrapportertEtterAOrdningen(true);
                    }

                    private Ansettelsesperiode getAnsettelsesperiode(RsAnsettelsesPeriode ansettelsesperiode) {

                        return nonNull(ansettelsesperiode) ?
                                Ansettelsesperiode.builder()
                                        .periode(mapperFacade.map(ansettelsesperiode, Periode.class))
                                        .sluttaarsak(ansettelsesperiode.getSluttaarsak())
                                        .build() :
                                null;
                    }

                    private List<Arbeidsavtale> getArbeidsavtale(RsAareg kilde) {

                        Arbeidsavtale arbeidsavtale = null;
                        if (nonNull(kilde.getArbeidsforholdstype())) {
                            arbeidsavtale = switch (kilde.getArbeidsforholdstype()) {
                                case "maritimtArbeidsforhold" -> new MaritimArbeidsavtale();
                                case "forenkletOppgjoersordning" -> new ForenkletOppgjoersordningArbeidsavtale();
                                case "frilanserOppdragstakerHonorarPersonerMm" -> new FrilanserArbeidsavtale();
                                default -> new OrdinaerArbeidsavtale();
                            };
                            if (nonNull(kilde.getArbeidsavtale())) {
                                mapperFacade.map(kilde.getArbeidsavtale(), arbeidsavtale);
                            }
                        }

                        List<Arbeidsavtale> arbeidsavtaler = new ArrayList<>();

                        for (int i = 0; i < kilde.getFartoy().size(); i++) {
                            if (i == 0 && arbeidsavtale instanceof MaritimArbeidsavtale maritimArbeidsavtale) {
                                mapperFacade.map(kilde.getFartoy().get(0), maritimArbeidsavtale);

                            } else {
                                var arbeidsavtale2 = nonNull(kilde.getArbeidsavtale()) ?
                                        mapperFacade.map(kilde.getArbeidsavtale(), MaritimArbeidsavtale.class) :
                                        new MaritimArbeidsavtale();
                                mapperFacade.map(kilde.getFartoy().get(i), arbeidsavtale2);
                                arbeidsavtaler.add(arbeidsavtale2);

                            }
                        }

                        if (nonNull(arbeidsavtale)) {
                            arbeidsavtaler.add(0, arbeidsavtale);
                        }
                        return arbeidsavtaler;
                    }
                })
                .register();

        factory.classMap(RsArbeidsavtale.class, Arbeidsavtale.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsArbeidsavtale kilde,
                                        Arbeidsavtale destinasjon, MappingContext context) {

                        destinasjon.setAntallTimerPrUke(kilde.getAvtaltArbeidstimerPerUke());
                        destinasjon.setSistLoennsendring(toDate(kilde.getSisteLoennsendringsdato()));
                        destinasjon.setSistStillingsendring(toDate(kilde.getEndringsdatoStillingsprosent()));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsUtenlandsopphold.class, Utenlandsopphold.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsUtenlandsopphold kilde,
                                        Utenlandsopphold destinasjon, MappingContext context) {

                        destinasjon.setLandkode(kilde.getLand());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsPermisjon.class, PermisjonPermittering.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsPermisjon kilde,
                                        PermisjonPermittering destinasjon, MappingContext context) {

                        destinasjon.setPermisjonPermitteringId(kilde.getPermisjonId());
                        destinasjon.setPeriode(mapperFacade.map(kilde.getPermisjonsPeriode(), Periode.class));
                        destinasjon.setProsent(toDouble(kilde.getPermisjonsprosent()));
                        destinasjon.setType(isNotBlank(kilde.getPermisjon()) ? kilde.getPermisjon() : kilde.getPermisjonOgPermittering());
                    }
                })
                .register();

        factory.classMap(RsPermittering.class, PermisjonPermittering.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsPermittering kilde,
                                        PermisjonPermittering destinasjon, MappingContext context) {

                        destinasjon.setPeriode(mapperFacade.map(kilde.getPermitteringsPeriode(), Periode.class));
                        destinasjon.setProsent(toDouble(kilde.getPermitteringsprosent()));
                        destinasjon.setType("permittering");
                    }
                })
                .register();
    }
}