package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.aareg.amelding.mapper.AmeldingRequestMappingStrategy;
import no.nav.dolly.domain.resultset.aareg.RsAmeldingRequest;
import no.nav.dolly.domain.resultset.aareg.RsAnsettelsesPeriode;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsavtale;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforholdAareg;
import no.nav.dolly.domain.resultset.aareg.RsPeriodeAareg;
import no.nav.dolly.domain.resultset.aareg.RsPermisjon;
import no.nav.dolly.domain.resultset.aareg.RsPermittering;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.testnav.libs.dto.ameldingservice.v1.AMeldingDTO;
import no.nav.testnav.libs.dto.ameldingservice.v1.ArbeidsforholdDTO;
import no.nav.testnav.libs.dto.ameldingservice.v1.PermisjonDTO;
import no.nav.testnav.libs.dto.ameldingservice.v1.PersonDTO;
import no.nav.testnav.libs.dto.ameldingservice.v1.VirksomhetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
public class AmeldingRequestMappingStrategyTest {

    private static final String IDENT = "1234567890";

    private static final String ARBEIDSFORHOLDSTYPE = "ordinaertArbeidsforhold";
    private static final String ARBEIDSTIDSORDNING = "ikkeSkift";
    private static final String YRKE = "2521106";
    private static final String ANSETTELSESFORM = "fast";
    private static final Double AVTALTARBEIDSTIMERPERUKE = 37.5;
    private static final Double STILLINGSPROSENT = 100.0;

    private static final String AKTOERTYPE = "ORG";
    private static final String ORGNUMMER = "805824352";
    private static final String JURIDISK_ENHET = "987654321";
    private static final String PERMISJON_ID = "dolly-123456";

    private static final LocalDateTime ANSETTELSESPERIODEFOM = LocalDateTime.of(2001, 1, 1, 0, 0);
    private static final LocalDateTime ANSETTELSESPERIODETOM = LocalDateTime.of(2001, 6, 1, 0, 0);

    private MapperFacade mapperFacade;
    private List<RsAmeldingRequest> rsAmeldingRequest;
    private AMeldingDTO validAmeldingDto;

    @BeforeEach
    public void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new AmeldingRequestMappingStrategy());

        RsArbeidsforholdAareg arbeidsforholdAareg = RsArbeidsforholdAareg.builder()
                .ansettelsesPeriode(RsAnsettelsesPeriode.builder()
                        .fom(ANSETTELSESPERIODEFOM)
                        .tom(ANSETTELSESPERIODETOM)
                        .build())
                .antallTimerForTimeloennet(new ArrayList<>())
                .fartoy(null)
                .arbeidsavtale(RsArbeidsavtale.builder()
                        .arbeidstidsordning(ARBEIDSTIDSORDNING)
                        .yrke(YRKE)
                        .ansettelsesform(ANSETTELSESFORM)
                        .avtaltArbeidstimerPerUke(AVTALTARBEIDSTIMERPERUKE)
                        .stillingsprosent(STILLINGSPROSENT)
                        .build())
                .permisjon(new ArrayList<>())
                .permittering(new ArrayList<>())
                .utenlandsopphold(new ArrayList<>())
                .arbeidsgiver(RsArbeidsforholdAareg.RsArbeidsgiver.builder()
                        .aktoertype(AKTOERTYPE)
                        .orgnummer(ORGNUMMER)
                        .build())
                .build();

        rsAmeldingRequest = List.of(
                RsAmeldingRequest.builder()
                        .maaned("2021-01")
                        .arbeidsforhold(List.of(arbeidsforholdAareg))
                        .build());

        validAmeldingDto = AMeldingDTO.builder()
                .kalendermaaned(LocalDate.of(2021, 1, 1))
                .opplysningspliktigOrganisajonsnummer(JURIDISK_ENHET)
                .virksomheter(List.of(VirksomhetDTO.builder()
                        .organisajonsnummer(ORGNUMMER)
                        .personer(List.of(PersonDTO.builder()
                                .ident(IDENT)
                                .arbeidsforhold(List.of(ArbeidsforholdDTO.builder()
                                        .arbeidsforholdId("1")
                                        .arbeidsforholdType(ARBEIDSFORHOLDSTYPE)
                                        .arbeidstidsordning(ARBEIDSTIDSORDNING)
                                        .antallTimerPerUke(AVTALTARBEIDSTIMERPERUKE.floatValue())
                                        .yrke(YRKE)
                                        .startdato(ANSETTELSESPERIODEFOM.toLocalDate())
                                        .sluttdato(ANSETTELSESPERIODETOM.toLocalDate())
                                        .stillingsprosent(STILLINGSPROSENT.floatValue())
                                        .build()))
                                .build()))
                        .build()))
                .build();
    }

    @Test
    public void map_AmeldingRequest_Returns_Valid_Amelding() {

        MappingContext context = new MappingContext.Factory().getContext();
        context.setProperty("personIdent", IDENT);
        context.setProperty("arbeidsforholdstype", ARBEIDSFORHOLDSTYPE);
        context.setProperty("opplysningsPliktig", Map.of(ORGNUMMER, JURIDISK_ENHET));

        List<AMeldingDTO> result = mapperFacade.mapAsList(rsAmeldingRequest, AMeldingDTO.class, context);

        assertThat(result.get(0), is(equalTo(validAmeldingDto)));
    }

    @Test
    public void map_Permisjon_Returns_Valid_Amelding_Permisjon() {

        final String PERMISJON = "permisjon";

        RsPermisjon permisjon = RsPermisjon.builder()
                .permisjonsPeriode(RsPeriodeAareg.builder()
                        .fom(LocalDateTime.of(2021, 5, 1, 0, 0))
                        .tom(LocalDateTime.of(2021, 5, 10, 0, 0))
                        .build())
                .permisjonsprosent(BigDecimal.valueOf(100))
                .permisjon(PERMISJON)
                .build();

        List<PermisjonDTO> result = mapperFacade.mapAsList(List.of(permisjon), PermisjonDTO.class);

        assertThat(result.get(0), is(equalTo(PermisjonDTO.builder()
                .permisjonId(PERMISJON_ID)
                .permisjonsprosent(100F)
                .beskrivelse(PERMISJON)
                .startdato(LocalDate.of(2021, 5, 1))
                .sluttdato(LocalDate.of(2021, 5, 10))
                .build())));
    }

    @Test
    public void map_Permittering_Returns_Valid_Amelding_Permisjon() {

        final String PERMITTERING = "permittering";

        RsPermittering permittering = RsPermittering.builder()
                .permitteringsPeriode(RsPeriodeAareg.builder()
                        .fom(LocalDateTime.of(2021, 5, 1, 0, 0))
                        .tom(LocalDateTime.of(2021, 5, 10, 0, 0))
                        .build())
                .permitteringsprosent(BigDecimal.valueOf(100))
                .build();

        List<RsPermisjon> result = mapperFacade.mapAsList(List.of(permittering), RsPermisjon.class);

        RsPermisjon expected = RsPermisjon.builder()
                .permisjonId(PERMISJON_ID)
                .permisjonsprosent(BigDecimal.valueOf(100))
                .permisjon(PERMITTERING)
                .permisjonsPeriode(RsPeriodeAareg.builder()
                        .fom(LocalDateTime.of(2021, 5, 1, 0, 0))
                        .tom(LocalDateTime.of(2021, 5, 10, 0, 0))
                        .build())
                .build();

        assertThat(result.get(0).getPermisjonId(), is(equalTo(expected.getPermisjonId())));
        assertThat(result.get(0).getPermisjonsprosent(), is(equalTo(expected.getPermisjonsprosent())));
        assertThat(result.get(0).getPermisjon(), is(equalTo(expected.getPermisjon())));
        assertThat(result.get(0).getPermisjonsPeriode().getFom(), is(equalTo(expected.getPermisjonsPeriode().getFom())));
        assertThat(result.get(0).getPermisjonsPeriode().getTom(), is(equalTo(expected.getPermisjonsPeriode().getTom())));
    }
}