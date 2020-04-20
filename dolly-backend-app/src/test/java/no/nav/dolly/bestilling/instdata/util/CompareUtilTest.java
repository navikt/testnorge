package no.nav.dolly.bestilling.instdata.util;

import static java.util.Arrays.asList;
import static no.nav.dolly.domain.resultset.inst.TssEksternId.INDRE_OSTFOLD_FENGSEL;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDate;
import org.junit.Test;

import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.domain.resultset.inst.InstdataInstitusjonstype;

public class CompareUtilTest {

    private static final LocalDate STARTDATO_1 = LocalDate.of(1995, 5, 9);
    private static final LocalDate STARTDATO_2 = LocalDate.of(2002, 12, 5);
    private static final LocalDate SLUTTDATO_1 = LocalDate.of(1998, 8, 3);
    private static final LocalDate SLUTTDATO_2 = LocalDate.of(2004, 3, 4);
    private static final Instdata OPPHOLD_1 = Instdata.builder()
            .startdato(STARTDATO_1)
            .faktiskSluttdato(SLUTTDATO_1)
            .institusjonstype(InstdataInstitusjonstype.FO)
            .tssEksternId(INDRE_OSTFOLD_FENGSEL.getId())
            .overfoert(true)
            .build();
    private static final Instdata OPPHOLD_2 = Instdata.builder()
            .startdato(STARTDATO_1)
            .faktiskSluttdato(SLUTTDATO_1)
            .institusjonstype(InstdataInstitusjonstype.FO)
            .tssEksternId(INDRE_OSTFOLD_FENGSEL.getId())
            .overfoert(true)
            .build();

    @Test
    public void requestSameAsExisting_MatchFound() {

        assertThat(CompareUtil.isSubsetOf(asList(OPPHOLD_1), asList(OPPHOLD_1)), is(true));
    }

    @Test
    public void emptyRequest_MatchFound() {

        assertThat(CompareUtil.isSubsetOf(asList(), asList(OPPHOLD_1)), is(true));
    }

    @Test
    public void requestSubsetOfExisting_MatchFound() {

        assertThat(CompareUtil.isSubsetOf(asList(OPPHOLD_1), asList(OPPHOLD_1, OPPHOLD_2)), is(true));
    }

    @Test
    public void requestSubsetOfExisting_MatchFound() {

        assertThat(CompareUtil.isSubsetOf(asList(OPPHOLD_1), asList(OPPHOLD_1, OPPHOLD_2)), is(true));
    }
}