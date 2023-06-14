package no.nav.dolly.bestilling.medl.mapper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.medl.MedlData;
import no.nav.dolly.domain.resultset.medl.MedlDataResponse;
import no.nav.dolly.domain.resultset.medl.RsMedl;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class MedlMappingStrategyTest {

    private static final String IDENT = "1234567890";
    private static final LocalDate FRA_OG_MED = LocalDate.of(2000, 10, 10);
    private static final LocalDate TIL_OG_MED = LocalDate.of(2022, 10, 10);
    private static final String DEKNING = "DEKNING";
    private static final String GRUNNLAG = "GRUNNLAG";
    private static final String KILDE = "KILDE";
    private static final String KILDEDOKUMENT = "KILDEDOKUMENT";
    private static final String LOVVALG = "LOVVALG";
    private static final String LOVVALGSLAND = "LOVVALGSLAND";
    private static final String STATUS = "STATUS";
    private static final String STATUSAARSAK = "STATUSÅRSAK";
    private static final String STATSBORGERLAND = "STATSBORGERLAND";
    private static final String STUDIELAND = "STUDIELAND";
    private static final String VERSJON = "5";

    private MapperFacade mapperFacade;
    private MappingContext context;

    @BeforeEach
    void setUp() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(
                new MedlMappingStrategy());
        context = new MappingContext.Factory().getContext();
        context.setProperty("ident", IDENT);
    }

    @Test
    void TestRsMedlMappingWithContext() {

        var rsMedl = RsMedl.builder()
                .dekning(DEKNING)
                .fraOgMed(FRA_OG_MED)
                .tilOgMed(TIL_OG_MED)
                .grunnlag(GRUNNLAG)
                .kilde(KILDE)
                .kildedokument(KILDEDOKUMENT)
                .lovvalg(LOVVALG)
                .lovvalgsland(LOVVALGSLAND)
                .status(STATUS)
                .statusaarsak(STATUSAARSAK)
                .studieinformasjon(RsMedl.Studieinformasjon.builder()
                        .delstudie(true)
                        .soeknadInnvilget(true)
                        .statsborgerland(STATSBORGERLAND)
                        .studieland(STUDIELAND)
                        .build())
                .build();

        var medlData = mapperFacade.map(rsMedl, MedlData.class, context);

        assertThat(medlData.getDekning(), is(DEKNING));
        assertThat(medlData.getFraOgMed(), is(FRA_OG_MED));
        assertThat(medlData.getTilOgMed(), is(TIL_OG_MED));
        assertThat(medlData.getGrunnlag(), is(GRUNNLAG));
        assertThat(medlData.getId(), is(nullValue()));
        assertThat(medlData.getIdent(), is(IDENT));
        assertThat(medlData.getKildedokument(), is(KILDEDOKUMENT));
        assertThat(medlData.getKilde(), is(KILDE));
        assertThat(medlData.getLovvalg(), is(LOVVALG));
        assertThat(medlData.getLovvalgsland(), is(LOVVALGSLAND));
        assertThat(medlData.getVersjon(), is(nullValue()));
        assertThat(medlData.getStatus(), is(STATUS));
        assertThat(medlData.getStatusaarsak(), is(STATUSAARSAK));
        assertThat(medlData.getStudieinformasjon().getDelstudie(), is(true));
        assertThat(medlData.getStudieinformasjon().getSoeknadInnvilget(), is(true));
        assertThat(medlData.getStudieinformasjon().getStatsborgerland(), is(STATSBORGERLAND));
        assertThat(medlData.getStudieinformasjon().getStudieland(), is(STUDIELAND));
    }

    @Test
    void TestMedlResponseMapping() {

        var medlResponse = MedlDataResponse.builder()
                .dekning(DEKNING)
                .fraOgMed(FRA_OG_MED)
                .tilOgMed(TIL_OG_MED)
                .grunnlag(GRUNNLAG)
                .lovvalg(LOVVALG)
                .lovvalgsland(LOVVALGSLAND)
                .status(STATUS)
                .statusaarsak(STATUSAARSAK)
                .ident(IDENT)
                .sporingsinformasjon(MedlDataResponse.Sporingsinformasjon.builder()
                        .kilde(KILDE)
                        .kildedokument(KILDEDOKUMENT)
                        .versjon(VERSJON)
                        .build())
                .studieinformasjon(MedlDataResponse.Studieinformasjon.builder()
                        .delstudie(true)
                        .soeknadInnvilget(true)
                        .statsborgerland(STATSBORGERLAND)
                        .studieland(STUDIELAND)
                        .build())
                .build();

        var medlData = mapperFacade.map(medlResponse, MedlData.class);

        assertThat(medlData.getDekning(), is(DEKNING));
        assertThat(medlData.getFraOgMed(), is(FRA_OG_MED));
        assertThat(medlData.getTilOgMed(), is(TIL_OG_MED));
        assertThat(medlData.getGrunnlag(), is(GRUNNLAG));
        assertThat(medlData.getId(), is(nullValue()));
        assertThat(medlData.getIdent(), is(IDENT));
        assertThat(medlData.getKildedokument(), is(KILDEDOKUMENT));
        assertThat(medlData.getKilde(), is(KILDE));
        assertThat(medlData.getLovvalg(), is(LOVVALG));
        assertThat(medlData.getLovvalgsland(), is(LOVVALGSLAND));
        assertThat(medlData.getVersjon(), is(VERSJON));
        assertThat(medlData.getStatus(), is(STATUS));
        assertThat(medlData.getStatusaarsak(), is(STATUSAARSAK));
        assertThat(medlData.getStudieinformasjon().getDelstudie(), is(true));
        assertThat(medlData.getStudieinformasjon().getSoeknadInnvilget(), is(true));
        assertThat(medlData.getStudieinformasjon().getStatsborgerland(), is(STATSBORGERLAND));
        assertThat(medlData.getStudieinformasjon().getStudieland(), is(STUDIELAND));
    }
}