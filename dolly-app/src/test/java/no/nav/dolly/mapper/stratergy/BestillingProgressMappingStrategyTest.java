package no.nav.dolly.mapper.stratergy;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsBestillingProgress;
import no.nav.dolly.mapper.utils.MapperTestUtils;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class BestillingProgressMappingStrategyTest {

    private MapperFacade mapper;

    @Before
    public void setUpHappyPath() {
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new BestillingProgressMappingStrategy());
    }

    @Test
    public void mapBestillingProgressToRsBestillingProgress(){
        BestillingProgress progress = new BestillingProgress();
        progress.setFeil("feil");
        progress.setIdent("ident");
        progress.setId(1l);
        progress.setBestillingId(2l);
        progress.setTpsfSuccessEnv("u1,u2,t1,t10,q10,q11");
        progress.setSigrunSuccessEnv(null);
        progress.setAaregSuccessEnv(null);

        RsBestillingProgress rsProgress = mapper.map(progress, RsBestillingProgress.class);

        assertThat(rsProgress.getId(), is(1l));
        assertThat(rsProgress.getBestillingsId(), is(2l));
        assertThat(rsProgress.getFeil(), is("feil"));
        assertThat(rsProgress.getIdent(), is("ident"));
        assertThat(rsProgress.getAaregSuccessEnv(), is(nullValue()));
        assertThat(rsProgress.getSigrunSuccessEnv(), is(nullValue()));

        assertThat(rsProgress.getTpsfSuccessEnv().size(), is(6));
        assertThat(rsProgress.getTpsfSuccessEnv().get(0), is("u1"));
        assertThat(rsProgress.getTpsfSuccessEnv().get(1), is("u2"));
        assertThat(rsProgress.getTpsfSuccessEnv().get(2), is("t1"));
        assertThat(rsProgress.getTpsfSuccessEnv().get(3), is("t10"));
        assertThat(rsProgress.getTpsfSuccessEnv().get(4), is("q10"));
        assertThat(rsProgress.getTpsfSuccessEnv().get(5), is("q11"));
    }

    @Test
    public void mappingKasterIkkeNullPointerTrossNullVerdiForAlleRegistere(){
        BestillingProgress progress = new BestillingProgress();
        progress.setFeil("feil");
        progress.setIdent("ident");
        progress.setId(1l);
        progress.setBestillingId(2l);
        progress.setTpsfSuccessEnv(null);
        progress.setSigrunSuccessEnv(null);
        progress.setAaregSuccessEnv(null);

        RsBestillingProgress rsProgress = mapper.map(progress, RsBestillingProgress.class);

        assertThat(rsProgress.getId(), is(1l));
        assertThat(rsProgress.getBestillingsId(), is(2l));
        assertThat(rsProgress.getFeil(), is("feil"));
        assertThat(rsProgress.getIdent(), is("ident"));
        assertThat(rsProgress.getAaregSuccessEnv(), is(nullValue()));
        assertThat(rsProgress.getSigrunSuccessEnv(), is(nullValue()));
        assertThat(rsProgress.getTpsfSuccessEnv(), is(nullValue()));
    }
}