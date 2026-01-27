package no.nav.skattekortservice.mapper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import no.nav.skattekortservice.dto.v2.OpprettSkattekortRequest;
import no.nav.skattekortservice.dto.v2.ResultatForSkattekort;
import no.nav.skattekortservice.dto.v2.TilleggsopplysningType;
import no.nav.skattekortservice.dto.v2.TrekkodeType;
import no.nav.testnav.libs.dto.skattekortservice.v1.Forskuddstrekk;
import no.nav.testnav.libs.dto.skattekortservice.v1.Frikort;
import no.nav.testnav.libs.dto.skattekortservice.v1.Resultatstatus;
import no.nav.testnav.libs.dto.skattekortservice.v1.Skattekort;
import no.nav.testnav.libs.dto.skattekortservice.v1.Skattekortmelding;
import no.nav.testnav.libs.dto.skattekortservice.v1.Tilleggsopplysning;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekkode;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekkprosent;
import no.nav.testnav.libs.dto.skattekortservice.v1.Trekktabell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SkattekortRequestMappingStrategyTest {

    private MapperFacade mapperFacade;

    @BeforeEach
    void setUp() {
        var factory = new DefaultMapperFactory.Builder().build();
        new SkattekortRequestMappingStrategy().register(factory);
        mapperFacade = factory.getMapperFacade();
    }

    @Test
    void shouldMapSkattekortmeldingWithFrikort() {
        var frikort = Frikort.builder()
                .trekkode(Trekkode.LOENN_FRA_HOVEDARBEIDSGIVER)
                .frikortbeloep(50000)
                .build();
        var forskuddstrekk = Forskuddstrekk.builder()
                .frikort(frikort)
                .build();
        var skattekort = Skattekort.builder()
                .utstedtDato(LocalDate.of(2026, 1, 15))
                .forskuddstrekk(List.of(forskuddstrekk))
                .build();
        var melding = Skattekortmelding.builder()
                .arbeidstakeridentifikator("12345678901")
                .inntektsaar(2026)
                .resultatPaaForespoersel(Resultatstatus.SKATTEKORTOPPLYSNINGER_OK)
                .skattekort(skattekort)
                .tilleggsopplysning(List.of(Tilleggsopplysning.OPPHOLD_PAA_SVALBARD))
                .build();

        OpprettSkattekortRequest result = mapperFacade.map(melding, OpprettSkattekortRequest.class);

        assertThat(result.getFnr()).isEqualTo("12345678901");
        assertThat(result.getSkattekort().getInntektsaar()).isEqualTo(2026);
        assertThat(result.getSkattekort().getUtstedtDato()).isEqualTo(LocalDate.of(2026, 1, 15));
        assertThat(result.getSkattekort().getResultatForSkattekort()).isEqualTo(ResultatForSkattekort.SKATTEKORTOPPLYSNINGER_OK);
        assertThat(result.getSkattekort().getForskuddstrekkList()).hasSize(1);
        assertThat(result.getSkattekort().getForskuddstrekkList().getFirst().getTrekkode()).isEqualTo(TrekkodeType.LOENN_FRA_HOVEDARBEIDSGIVER);
        assertThat(result.getSkattekort().getForskuddstrekkList().getFirst().getFrikortBeloep()).isEqualTo(50000);
        assertThat(result.getSkattekort().getTilleggsopplysningList()).containsExactly(TilleggsopplysningType.OPPHOLD_PAA_SVALBARD);
    }

    @Test
    void shouldMapSkattekortmeldingWithTrekktabell() {
        var trekktabell = Trekktabell.builder()
                .trekkode(Trekkode.PENSJON)
                .tabellnummer("8010")
                .prosentsats(25)
                .antallMaanederForTrekk(10)
                .build();
        var forskuddstrekk = Forskuddstrekk.builder()
                .trekktabell(trekktabell)
                .build();
        var skattekort = Skattekort.builder()
                .forskuddstrekk(List.of(forskuddstrekk))
                .build();
        var melding = Skattekortmelding.builder()
                .arbeidstakeridentifikator("12345678901")
                .inntektsaar(2026)
                .skattekort(skattekort)
                .build();

        OpprettSkattekortRequest result = mapperFacade.map(melding, OpprettSkattekortRequest.class);

        assertThat(result.getSkattekort().getForskuddstrekkList()).hasSize(1);
        assertThat(result.getSkattekort().getForskuddstrekkList().getFirst().getTrekkode()).isEqualTo(TrekkodeType.PENSJON);
        assertThat(result.getSkattekort().getForskuddstrekkList().getFirst().getTabell()).isEqualTo("8010");
        assertThat(result.getSkattekort().getForskuddstrekkList().getFirst().getProsentSats()).isEqualTo(25.0);
        assertThat(result.getSkattekort().getForskuddstrekkList().getFirst().getAntallMndForTrekk()).isEqualTo(10.0);
    }

    @Test
    void shouldMapSkattekortmeldingWithTrekkprosent() {
        var trekkprosent = Trekkprosent.builder()
                .trekkode(Trekkode.LOENN_FRA_BIARBEIDSGIVER)
                .prosentsats(30)
                .antallMaanederForTrekk(12)
                .build();
        var forskuddstrekk = Forskuddstrekk.builder()
                .trekkprosent(trekkprosent)
                .build();
        var skattekort = Skattekort.builder()
                .forskuddstrekk(List.of(forskuddstrekk))
                .build();
        var melding = Skattekortmelding.builder()
                .arbeidstakeridentifikator("12345678901")
                .inntektsaar(2025)
                .skattekort(skattekort)
                .build();

        OpprettSkattekortRequest result = mapperFacade.map(melding, OpprettSkattekortRequest.class);

        assertThat(result.getSkattekort().getForskuddstrekkList()).hasSize(1);
        assertThat(result.getSkattekort().getForskuddstrekkList().getFirst().getTrekkode()).isEqualTo(TrekkodeType.LOENN_FRA_BIARBEIDSGIVER);
        assertThat(result.getSkattekort().getForskuddstrekkList().getFirst().getProsentSats()).isEqualTo(30.0);
        assertThat(result.getSkattekort().getForskuddstrekkList().getFirst().getAntallMndForTrekk()).isEqualTo(12.0);
    }

    @Test
    void shouldMapResultatstatusIkkeSkattekort() {
        var melding = Skattekortmelding.builder()
                .arbeidstakeridentifikator("12345678901")
                .inntektsaar(2026)
                .resultatPaaForespoersel(Resultatstatus.IKKE_SKATTEKORT)
                .build();

        OpprettSkattekortRequest result = mapperFacade.map(melding, OpprettSkattekortRequest.class);

        assertThat(result.getSkattekort().getResultatForSkattekort()).isEqualTo(ResultatForSkattekort.IKKE_SKATTEKORT);
    }

    @Test
    void shouldMapResultatstatusIkkeTrekkplikt() {
        var melding = Skattekortmelding.builder()
                .arbeidstakeridentifikator("12345678901")
                .inntektsaar(2026)
                .resultatPaaForespoersel(Resultatstatus.IKKE_TREKKPLIKT)
                .build();

        OpprettSkattekortRequest result = mapperFacade.map(melding, OpprettSkattekortRequest.class);

        assertThat(result.getSkattekort().getResultatForSkattekort()).isEqualTo(ResultatForSkattekort.IKKE_TREKKPLIKT);
    }

    @Test
    void shouldMapMultipleTilleggsopplysninger() {
        var melding = Skattekortmelding.builder()
                .arbeidstakeridentifikator("12345678901")
                .inntektsaar(2026)
                .tilleggsopplysning(List.of(
                        Tilleggsopplysning.KILDESKATTPENSJONIST,
                        Tilleggsopplysning.KILDESKATT_PAA_LOENN))
                .build();

        OpprettSkattekortRequest result = mapperFacade.map(melding, OpprettSkattekortRequest.class);

        assertThat(result.getSkattekort().getTilleggsopplysningList())
                .containsExactly(TilleggsopplysningType.KILDESKATTPENSJONIST, TilleggsopplysningType.KILDESKATT_PAA_LOENN);
    }

    @Test
    void shouldHandleNullSkattekort() {
        var melding = Skattekortmelding.builder()
                .arbeidstakeridentifikator("12345678901")
                .inntektsaar(2026)
                .build();

        OpprettSkattekortRequest result = mapperFacade.map(melding, OpprettSkattekortRequest.class);

        assertThat(result.getSkattekort().getForskuddstrekkList()).isNullOrEmpty();
        assertThat(result.getSkattekort().getUtstedtDato()).isNull();
    }

    @Test
    void shouldHandleNullResultatPaaForespoersel() {
        var melding = Skattekortmelding.builder()
                .arbeidstakeridentifikator("12345678901")
                .inntektsaar(2026)
                .resultatPaaForespoersel(null)
                .build();

        OpprettSkattekortRequest result = mapperFacade.map(melding, OpprettSkattekortRequest.class);

        assertThat(result.getSkattekort().getResultatForSkattekort()).isEqualTo(ResultatForSkattekort.SKATTEKORTOPPLYSNINGER_OK);
    }
}
