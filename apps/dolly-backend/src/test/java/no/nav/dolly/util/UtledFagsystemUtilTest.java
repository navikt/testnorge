package no.nav.dolly.util;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.BestilteKriterier;
import no.nav.dolly.domain.resultset.RsNomData;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.breg.RsBregdata;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
import no.nav.dolly.domain.resultset.etterlatte.EtterlatteYtelse;
import no.nav.dolly.domain.resultset.fullmakt.RsFullmakt;
import no.nav.dolly.domain.resultset.histark.RsHistark;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.domain.resultset.kontoregister.BankkontoData;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.medl.RsMedl;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.domain.resultset.skattekort.SkattekortRequestDTO;
import no.nav.dolly.domain.resultset.skjerming.RsSkjerming;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding;
import no.nav.dolly.domain.resultset.tpsmessagingservice.RsTpsMessaging;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.yrkesskade.v1.YrkesskadeRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;

class UtledFagsystemUtilTest {

    private static Bestilling bestilling() {
        return Bestilling.builder().build();
    }

    private static Bestilling importBestilling() {
        return Bestilling.builder().pdlImport("12345678901").build();
    }

    @Test
    void shouldAlwaysIncludePenForvalter() {

        var result = UtledFagsystemUtil.resolve(BestilteKriterier.builder().build(), bestilling());

        assertThat(result, hasSize(1));
        assertThat(result, containsInAnyOrder(SystemTyper.PEN_FORVALTER));
    }

    @Test
    void shouldResolvePdlTyperForPdldata() {

        var kriterier = BestilteKriterier.builder()
                .pdldata(new PdlPersondata())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.PDL_FORVALTER));
        assertThat(result, hasItem(SystemTyper.PDL_ORDRE));
        assertThat(result, hasItem(SystemTyper.PDL_PERSONSTATUS));
    }

    @Test
    void shouldResolvePdlImportTyper() {

        var result = UtledFagsystemUtil.resolve(BestilteKriterier.builder().build(), importBestilling());

        assertThat(result, hasItem(SystemTyper.PDLIMPORT));
        assertThat(result, hasItem(SystemTyper.PDL_FORVALTER));
        assertThat(result, hasItem(SystemTyper.PDL_ORDRE));
        assertThat(result, not(hasItem(SystemTyper.PDL_PERSONSTATUS)));
    }

    @Test
    void shouldResolveAllPensjonSubTyper() {

        var kriterier = BestilteKriterier.builder()
                .pensjonforvalter(PensjonData.builder()
                        .inntekt(new PensjonData.PoppInntekt())
                        .tp(List.of(new PensjonData.TpOrdning()))
                        .alderspensjon(new PensjonData.Alderspensjon())
                        .alderspensjonNyUtaksgrad(new PensjonData.AlderspensjonNyUtaksgrad())
                        .uforetrygd(new PensjonData.Uforetrygd())
                        .pensjonsavtale(List.of(new PensjonData.Pensjonsavtale()))
                        .afpOffentlig(new PensjonData.AfpOffentlig())
                        .build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.PEN_FORVALTER));
        assertThat(result, hasItem(SystemTyper.PEN_INNTEKT));
        assertThat(result, hasItem(SystemTyper.TP_FORVALTER));
        assertThat(result, hasItem(SystemTyper.PEN_AP));
        assertThat(result, hasItem(SystemTyper.PEN_AP_NY_UTTAKSGRAD));
        assertThat(result, hasItem(SystemTyper.PEN_UT));
        assertThat(result, hasItem(SystemTyper.PEN_PENSJONSAVTALE));
        assertThat(result, hasItem(SystemTyper.PEN_AFP_OFFENTLIG));
    }

    @Test
    void shouldNotResolvePensjonSubTyperWhenFieldsAreNull() {

        var kriterier = BestilteKriterier.builder()
                .pensjonforvalter(PensjonData.builder().build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.PEN_FORVALTER));
        assertThat(result, not(hasItem(SystemTyper.PEN_INNTEKT)));
        assertThat(result, not(hasItem(SystemTyper.TP_FORVALTER)));
        assertThat(result, not(hasItem(SystemTyper.PEN_AP)));
        assertThat(result, not(hasItem(SystemTyper.PEN_UT)));
    }

    @Test
    void shouldResolveSimpleFagsystemer() {

        var kriterier = BestilteKriterier.builder()
                .krrstub(new RsDigitalKontaktdata())
                .medl(new RsMedl())
                .udistub(new RsUdiPerson())
                .inntektstub(new InntektMultiplierWrapper())
                .arenaforvalter(new Arenadata())
                .inntektsmelding(new RsInntektsmelding())
                .brregstub(new RsBregdata())
                .histark(new RsHistark())
                .sykemelding(new RsSykemelding())
                .skattekort(new SkattekortRequestDTO())
                .nomdata(new RsNomData())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.KRRSTUB));
        assertThat(result, hasItem(SystemTyper.MEDL));
        assertThat(result, hasItem(SystemTyper.UDISTUB));
        assertThat(result, hasItem(SystemTyper.INNTK));
        assertThat(result, hasItem(SystemTyper.ARENA_BRUKER));
        assertThat(result, hasItem(SystemTyper.INNTKMELD));
        assertThat(result, hasItem(SystemTyper.BRREGSTUB));
        assertThat(result, hasItem(SystemTyper.HISTARK));
        assertThat(result, hasItem(SystemTyper.SYKEMELDING));
        assertThat(result, hasItem(SystemTyper.SKATTEKORT));
        assertThat(result, hasItem(SystemTyper.NOM));
    }

    @Test
    void shouldResolveListBasedFagsystemer() {

        var kriterier = BestilteKriterier.builder()
                .aareg(List.of(new RsAareg()))
                .fullmakt(List.of(new RsFullmakt()))
                .instdata(List.of(new RsInstdata()))
                .dokarkiv(List.of(new RsDokarkiv()))
                .yrkesskader(List.of(new YrkesskadeRequest()))
                .etterlatteYtelser(List.of(new EtterlatteYtelse()))
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.AAREG));
        assertThat(result, hasItem(SystemTyper.FULLMAKT));
        assertThat(result, hasItem(SystemTyper.INST2));
        assertThat(result, hasItem(SystemTyper.DOKARKIV));
        assertThat(result, hasItem(SystemTyper.YRKESSKADE));
        assertThat(result, hasItem(SystemTyper.ETTERLATTE));
    }

    @Test
    void shouldNotResolveListBasedFagsystemerWhenEmpty() {

        var kriterier = BestilteKriterier.builder()
                .aareg(List.of())
                .fullmakt(List.of())
                .instdata(List.of())
                .dokarkiv(List.of())
                .yrkesskader(List.of())
                .etterlatteYtelser(List.of())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, not(hasItem(SystemTyper.AAREG)));
        assertThat(result, not(hasItem(SystemTyper.FULLMAKT)));
        assertThat(result, not(hasItem(SystemTyper.INST2)));
        assertThat(result, not(hasItem(SystemTyper.DOKARKIV)));
        assertThat(result, not(hasItem(SystemTyper.YRKESSKADE)));
        assertThat(result, not(hasItem(SystemTyper.ETTERLATTE)));
    }

    @Test
    void shouldResolveBankkontoToKontoregisterAndTpsMessaging() {

        var kriterier = BestilteKriterier.builder()
                .bankkonto(new BankkontoData())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.KONTOREGISTER));
        assertThat(result, hasItem(SystemTyper.TPS_MESSAGING));
    }

    @Test
    void shouldResolveTpsMessagingForSkjerming() {

        var kriterier = BestilteKriterier.builder()
                .skjerming(RsSkjerming.builder().build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.TPS_MESSAGING));
    }

    @Test
    void shouldResolveTpsMessagingForPdldataWithPerson() {

        var pdldata = new PdlPersondata();
        pdldata.setPerson(new PersonDTO());

        var kriterier = BestilteKriterier.builder()
                .pdldata(pdldata)
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.TPS_MESSAGING));
    }

    @Test
    void shouldNotResolveTpsMessagingForPdldataWithoutPerson() {

        var kriterier = BestilteKriterier.builder()
                .pdldata(new PdlPersondata())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, not(hasItem(SystemTyper.TPS_MESSAGING)));
    }

    @Test
    void shouldResolveSkjermingsregisterForSkjermingWithDates() {

        var kriterier = BestilteKriterier.builder()
                .skjerming(RsSkjerming.builder()
                        .egenAnsattDatoFom(LocalDateTime.now())
                        .build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.SKJERMINGSREGISTER));
    }

    @Test
    void shouldResolveSkjermingsregisterForTpsMessagingEgenansatt() {

        var kriterier = BestilteKriterier.builder()
                .tpsMessaging(RsTpsMessaging.builder()
                        .egenAnsattDatoFom(LocalDate.now())
                        .build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, hasItem(SystemTyper.SKJERMINGSREGISTER));
    }

    @Test
    void shouldNotResolveSkjermingsregisterForSkjermingWithoutDates() {

        var kriterier = BestilteKriterier.builder()
                .skjerming(RsSkjerming.builder().build())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, bestilling());

        assertThat(result, not(hasItem(SystemTyper.SKJERMINGSREGISTER)));
    }

    @Test
    void shouldResolveTypicalImportScenario() {

        var pdldata = new PdlPersondata();
        pdldata.setPerson(new PersonDTO());

        var kriterier = BestilteKriterier.builder()
                .pdldata(pdldata)
                .bankkonto(new BankkontoData())
                .skjerming(RsSkjerming.builder()
                        .egenAnsattDatoFom(LocalDateTime.now())
                        .build())
                .pensjonforvalter(PensjonData.builder()
                        .inntekt(new PensjonData.PoppInntekt())
                        .build())
                .aareg(List.of(new RsAareg()))
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, importBestilling());

        assertThat(result, hasItem(SystemTyper.PDLIMPORT));
        assertThat(result, hasItem(SystemTyper.PDL_FORVALTER));
        assertThat(result, hasItem(SystemTyper.PDL_ORDRE));
        assertThat(result, hasItem(SystemTyper.PEN_FORVALTER));
        assertThat(result, hasItem(SystemTyper.PEN_INNTEKT));
        assertThat(result, hasItem(SystemTyper.AAREG));
        assertThat(result, hasItem(SystemTyper.KONTOREGISTER));
        assertThat(result, hasItem(SystemTyper.TPS_MESSAGING));
        assertThat(result, hasItem(SystemTyper.SKJERMINGSREGISTER));
    }

    @Test
    void shouldNotContainDuplicates() {

        var pdldata = new PdlPersondata();
        pdldata.setPerson(new PersonDTO());

        var kriterier = BestilteKriterier.builder()
                .pdldata(pdldata)
                .bankkonto(new BankkontoData())
                .build();

        var result = UtledFagsystemUtil.resolve(kriterier, importBestilling());

        var uniqueCount = result.stream().distinct().count();
        assertThat((long) result.size(), is(uniqueCount));
    }
}
