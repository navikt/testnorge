package no.nav.dolly.bestilling.instdata.mapper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.instdata.domain.InstdataKdiDTO;
import no.nav.dolly.domain.resultset.inst.RsInstdataKdi;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

class InstdataKdiMappingStrategyTest {

    private static final String IDENT = "12345678901";
    private static final String MILJOE = "q2";

    private MapperFacade mapperFacade;
    private MappingContext context;

    @BeforeEach
    void setUp() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new InstdataKdiMappingStrategy());
        context = MappingContextUtils.getMappingContext();
        context.setProperty("ident", IDENT);
        context.setProperty("miljoe", MILJOE);
    }

    @Test
    void shouldMapEnvironmentFromContext() {

        var kilde = RsInstdataKdi.builder().build();

        var result = mapperFacade.map(kilde, InstdataKdiDTO.class, context);

        assertThat(result.getEnvironment(), is(equalTo(MILJOE)));
    }

    @Test
    void shouldInitializeDataObject() {

        var kilde = RsInstdataKdi.builder().build();

        var result = mapperFacade.map(kilde, InstdataKdiDTO.class, context);

        assertThat(result.getData(), is(notNullValue()));
    }

    @Test
    void shouldMapEmptyListsWhenNoHendelser() {

        var kilde = RsInstdataKdi.builder().build();

        var result = mapperFacade.map(kilde, InstdataKdiDTO.class, context);

        assertThat(result.getData().getInnsettelse(), is(empty()));
        assertThat(result.getData().getAvbruddStart(), is(empty()));
        assertThat(result.getData().getAvbruddSlutt(), is(empty()));
        assertThat(result.getData().getLoeslatelse(), is(empty()));
        assertThat(result.getData().getForventetLoeslatelse(), is(empty()));
        assertThat(result.getData().getAnnullering(), is(empty()));
    }

    @Test
    void shouldMapInnsettelseWithIdent() {

        var tidspunkt = LocalDateTime.of(2025, 3, 1, 10, 0);
        var kilde = RsInstdataKdi.builder()
                .innsettelse(List.of(RsInstdataKdi.Innsettelse.builder()
                        .hendelseId("h1")
                        .meldingId("m1")
                        .kategori("DOM")
                        .organisasjonsnummer("123456789")
                        .tidspunkt(tidspunkt)
                        .build()))
                .build();

        var result = mapperFacade.map(kilde, InstdataKdiDTO.class, context);

        assertThat(result.getData().getInnsettelse(), hasSize(1));
        var innsettelse = result.getData().getInnsettelse().getFirst();
        assertThat(innsettelse.getNorskident(), is(equalTo(IDENT)));
        assertThat(innsettelse.getHendelseId(), is(equalTo("h1")));
        assertThat(innsettelse.getMeldingId(), is(equalTo("m1")));
        assertThat(innsettelse.getKategori(), is(equalTo("DOM")));
        assertThat(innsettelse.getOrganisasjonsnummer(), is(equalTo("123456789")));
        assertThat(innsettelse.getTidspunkt(), is(equalTo(tidspunkt)));
    }

    @Test
    void shouldMapAvbruddStartWithIdent() {

        var tidspunkt = LocalDateTime.of(2025, 4, 1, 8, 0);
        var forventet = LocalDateTime.of(2025, 5, 1, 8, 0);
        var kilde = RsInstdataKdi.builder()
                .avbruddStart(List.of(RsInstdataKdi.AvbruddStart.builder()
                        .hendelseId("h2")
                        .kategori("PERMISJON")
                        .organisasjonsnummer("987654321")
                        .tidspunkt(tidspunkt)
                        .forventetAvbruddSluttTidspunkt(forventet)
                        .build()))
                .build();

        var result = mapperFacade.map(kilde, InstdataKdiDTO.class, context);

        assertThat(result.getData().getAvbruddStart(), hasSize(1));
        var avbruddStart = result.getData().getAvbruddStart().getFirst();
        assertThat(avbruddStart.getNorskident(), is(equalTo(IDENT)));
        assertThat(avbruddStart.getKategori(), is(equalTo("PERMISJON")));
        assertThat(avbruddStart.getTidspunkt(), is(equalTo(tidspunkt)));
        assertThat(avbruddStart.getForventetAvbruddSluttTidspunkt(), is(equalTo(forventet)));
    }

    @Test
    void shouldMapAvbruddSluttWithIdent() {

        var tidspunkt = LocalDateTime.of(2025, 5, 1, 12, 0);
        var kilde = RsInstdataKdi.builder()
                .avbruddSlutt(List.of(RsInstdataKdi.AvbruddSlutt.builder()
                        .hendelseId("h3")
                        .kategori("PERMISJON")
                        .organisasjonsnummer("111222333")
                        .tidspunkt(tidspunkt)
                        .build()))
                .build();

        var result = mapperFacade.map(kilde, InstdataKdiDTO.class, context);

        assertThat(result.getData().getAvbruddSlutt(), hasSize(1));
        var avbruddSlutt = result.getData().getAvbruddSlutt().getFirst();
        assertThat(avbruddSlutt.getNorskident(), is(equalTo(IDENT)));
        assertThat(avbruddSlutt.getKategori(), is(equalTo("PERMISJON")));
        assertThat(avbruddSlutt.getTidspunkt(), is(equalTo(tidspunkt)));
    }

    @Test
    void shouldMapLoeslatelseWithIdent() {

        var tidspunkt = LocalDateTime.of(2025, 6, 15, 9, 0);
        var kilde = RsInstdataKdi.builder()
                .loeslatelse(List.of(RsInstdataKdi.Loeslatelse.builder()
                        .hendelseId("h4")
                        .kategori("DOM")
                        .organisasjonsnummer("444555666")
                        .tidspunkt(tidspunkt)
                        .erOverfoertTilUtlandskfengsel(true)
                        .build()))
                .build();

        var result = mapperFacade.map(kilde, InstdataKdiDTO.class, context);

        assertThat(result.getData().getLoeslatelse(), hasSize(1));
        var loeslatelse = result.getData().getLoeslatelse().getFirst();
        assertThat(loeslatelse.getNorskident(), is(equalTo(IDENT)));
        assertThat(loeslatelse.getKategori(), is(equalTo("DOM")));
        assertThat(loeslatelse.getErOverfoertTilUtlandskfengsel(), is(true));
    }

    @Test
    void shouldMapForventetLoeslatelseWithIdent() {

        var tidspunkt = LocalDateTime.of(2025, 12, 1, 8, 0);
        var kilde = RsInstdataKdi.builder()
                .innsettelse(List.of(RsInstdataKdi.Innsettelse.builder()
                        .hendelseId("inn-1")
                        .build()))
                .forventetLoeslatelse(List.of(RsInstdataKdi.ForventetLoeslatelse.builder()
                        .hendelseId("h5")
                        .innmeldingHendelseId("explicit-ref")
                        .tidspunkt(tidspunkt)
                        .build()))
                .build();

        var result = mapperFacade.map(kilde, InstdataKdiDTO.class, context);

        assertThat(result.getData().getForventetLoeslatelse(), hasSize(1));
        var forventet = result.getData().getForventetLoeslatelse().getFirst();
        assertThat(forventet.getNorskident(), is(equalTo(IDENT)));
        assertThat(forventet.getInnmeldingHendelseId(), is(equalTo("explicit-ref")));
        assertThat(forventet.getTidspunkt(), is(equalTo(tidspunkt)));
    }

    @Test
    void shouldDefaultInnmeldingHendelseIdFromMaxInnsettelseWhenBlank() {

        var kilde = RsInstdataKdi.builder()
                .innsettelse(List.of(
                        RsInstdataKdi.Innsettelse.builder().hendelseId("aaa").build(),
                        RsInstdataKdi.Innsettelse.builder().hendelseId("zzz").build()))
                .forventetLoeslatelse(List.of(RsInstdataKdi.ForventetLoeslatelse.builder()
                        .hendelseId("h6")
                        .build()))
                .build();

        var result = mapperFacade.map(kilde, InstdataKdiDTO.class, context);

        var forventet = result.getData().getForventetLoeslatelse().getFirst();
        assertThat(forventet.getInnmeldingHendelseId(), is(equalTo("zzz")));
    }

    @Test
    void shouldLeaveInnmeldingHendelseIdNullWhenNoInnsettelser() {

        var kilde = RsInstdataKdi.builder()
                .forventetLoeslatelse(List.of(RsInstdataKdi.ForventetLoeslatelse.builder()
                        .hendelseId("h7")
                        .build()))
                .build();

        var result = mapperFacade.map(kilde, InstdataKdiDTO.class, context);

        var forventet = result.getData().getForventetLoeslatelse().getFirst();
        assertThat(forventet.getInnmeldingHendelseId(), is(nullValue()));
    }

    @Test
    void shouldMapAnnulleringWithIdent() {

        var publiseringstidspunkt = LocalDateTime.of(2025, 7, 1, 14, 0);
        var kilde = RsInstdataKdi.builder()
                .annullering(List.of(RsInstdataKdi.Annullering.builder()
                        .hendelseId("h8")
                        .meldingId("m8")
                        .publiseringstidspunkt(publiseringstidspunkt)
                        .build()))
                .build();

        var result = mapperFacade.map(kilde, InstdataKdiDTO.class, context);

        assertThat(result.getData().getAnnullering(), hasSize(1));
        var annullering = result.getData().getAnnullering().getFirst();
        assertThat(annullering.getNorskident(), is(equalTo(IDENT)));
        assertThat(annullering.getHendelseId(), is(equalTo("h8")));
        assertThat(annullering.getMeldingId(), is(equalTo("m8")));
        assertThat(annullering.getPubliseringstidspunkt(), is(equalTo(publiseringstidspunkt)));
    }

    @Test
    void shouldMapMultipleHendelserOfSameType() {

        var kilde = RsInstdataKdi.builder()
                .innsettelse(List.of(
                        RsInstdataKdi.Innsettelse.builder().hendelseId("h-a").kategori("DOM").build(),
                        RsInstdataKdi.Innsettelse.builder().hendelseId("h-b").kategori("VARETEKT").build()))
                .build();

        var result = mapperFacade.map(kilde, InstdataKdiDTO.class, context);

        assertThat(result.getData().getInnsettelse(), hasSize(2));
        assertThat(result.getData().getInnsettelse().get(0).getNorskident(), is(equalTo(IDENT)));
        assertThat(result.getData().getInnsettelse().get(1).getNorskident(), is(equalTo(IDENT)));
        assertThat(result.getData().getInnsettelse().get(0).getKategori(), is(equalTo("DOM")));
        assertThat(result.getData().getInnsettelse().get(1).getKategori(), is(equalTo("VARETEKT")));
    }

    @Test
    void shouldMapAllHendelseTypesSimultaneously() {

        var kilde = RsInstdataKdi.builder()
                .innsettelse(List.of(RsInstdataKdi.Innsettelse.builder().hendelseId("i1").build()))
                .avbruddStart(List.of(RsInstdataKdi.AvbruddStart.builder().hendelseId("as1").build()))
                .avbruddSlutt(List.of(RsInstdataKdi.AvbruddSlutt.builder().hendelseId("ae1").build()))
                .loeslatelse(List.of(RsInstdataKdi.Loeslatelse.builder().hendelseId("l1").build()))
                .forventetLoeslatelse(List.of(RsInstdataKdi.ForventetLoeslatelse.builder().hendelseId("fl1").innmeldingHendelseId("i1").build()))
                .annullering(List.of(RsInstdataKdi.Annullering.builder().hendelseId("a1").build()))
                .build();

        var result = mapperFacade.map(kilde, InstdataKdiDTO.class, context);

        assertThat(result.getData().getInnsettelse(), hasSize(1));
        assertThat(result.getData().getAvbruddStart(), hasSize(1));
        assertThat(result.getData().getAvbruddSlutt(), hasSize(1));
        assertThat(result.getData().getLoeslatelse(), hasSize(1));
        assertThat(result.getData().getForventetLoeslatelse(), hasSize(1));
        assertThat(result.getData().getAnnullering(), hasSize(1));

        assertThat(result.getData().getInnsettelse().getFirst().getNorskident(), is(equalTo(IDENT)));
        assertThat(result.getData().getAvbruddStart().getFirst().getNorskident(), is(equalTo(IDENT)));
        assertThat(result.getData().getAvbruddSlutt().getFirst().getNorskident(), is(equalTo(IDENT)));
        assertThat(result.getData().getLoeslatelse().getFirst().getNorskident(), is(equalTo(IDENT)));
        assertThat(result.getData().getForventetLoeslatelse().getFirst().getNorskident(), is(equalTo(IDENT)));
        assertThat(result.getData().getAnnullering().getFirst().getNorskident(), is(equalTo(IDENT)));
    }
}
