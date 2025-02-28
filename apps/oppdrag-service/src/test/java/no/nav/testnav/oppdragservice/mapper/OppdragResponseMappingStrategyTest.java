package no.nav.testnav.oppdragservice.mapper;

import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.libs.dto.oppdragservice.v1.Oppdrag;
import no.nav.testnav.libs.dto.oppdragservice.v1.Oppdrag.JaNei;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragResponse;
import no.nav.testnav.oppdragservice.utilty.Oppdragsdata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static no.nav.testnav.oppdragservice.utilty.Oppdragsdata.BELOP_GRENSE;
import static no.nav.testnav.oppdragservice.utilty.Oppdragsdata.LOCAL_DATE;
import static no.nav.testnav.oppdragservice.utilty.Oppdragsdata.LOCAL_DATE_TIME;
import static no.nav.testnav.oppdragservice.utilty.Oppdragsdata.LONG_VALUE;
import static no.nav.testnav.oppdragservice.utilty.Oppdragsdata.NOM;
import static no.nav.testnav.oppdragservice.utilty.Oppdragsdata.NUMBER_VALUE;
import static no.nav.testnav.oppdragservice.utilty.Oppdragsdata.TEXT_VALUE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;

@ExtendWith(MockitoExtension.class)
class OppdragResponseMappingStrategyTest {

    private MapperFacade mapperFacade;

    @BeforeEach
    void setup() {

        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(
                List.of(new LocalDateCustomMapping(), new LocalDateTimeCustomMapping()),
                new OppdragResponseMappingStrategy());
    }

    @Test
    void mapOppdragResponse_OK() {

        var oppdragResponse = Oppdragsdata.buildOppdragResponse();

        var response = mapperFacade.map(oppdragResponse, OppdragResponse.class);

        assertThat(response.getInfomelding().getBeskrMelding(), is(equalTo(TEXT_VALUE)));

        var target = response.getOppdrag();

        assertThat(target.getOppdragsId(), is(equalTo(LONG_VALUE)));
        assertThat(target.getBilagstype(), contains(hasProperty("typeBilag", is(equalTo(TEXT_VALUE)))));
        assertThat(target.getAvstemmingsnokkel(), contains(allOf(
                hasProperty("kodeKomponent", is(equalTo(TEXT_VALUE))),
                hasProperty("avstemmingsNokkel", is(equalTo(TEXT_VALUE))),
                hasProperty("tidspktReg", is(equalTo(LOCAL_DATE_TIME))))));
        assertThat(target.getOmpostering(), allOf(
                hasProperty("datoOmposterFom", is(equalTo(LOCAL_DATE))),
                hasProperty("tidspktReg", is(equalTo(LOCAL_DATE_TIME))),
                hasProperty("omPostering", is(equalTo(JaNei.J))),
                hasProperty("saksbehId", is(equalTo(TEXT_VALUE))),
                hasProperty("feilreg", is(equalTo(TEXT_VALUE)))));
        assertThat(target.getKodeEndring(), is(equalTo(Oppdrag.KodeEndring.NY)));
        assertThat(target.getKodeStatus(), is(equalTo(Oppdrag.KodeStatus.ATTE)));
        assertThat(target.getDatoStatusFom(), is(equalTo(LOCAL_DATE)));
        assertThat(target.getKodeFagomraade(), is(equalTo(TEXT_VALUE)));
        assertThat(target.getFagsystemId(), is(equalTo(TEXT_VALUE)));
        assertThat(target.getOppdragsId(), is(equalTo(LONG_VALUE)));
        assertThat(target.getUtbetFrekvens(), is(equalTo(Oppdrag.UtbetalingFrekvensType._14DG)));
        assertThat(target.getDatoForfall(), is(equalTo(LOCAL_DATE)));
        assertThat(target.getStonadId(), is(equalTo(TEXT_VALUE)));
        assertThat(target.getOppdragGjelderId(), is(equalTo(TEXT_VALUE)));
        assertThat(target.getDatoOppdragGjelderFom(), is(equalTo(LOCAL_DATE)));
        assertThat(target.getSaksbehId(), is(equalTo(NOM)));
        assertThat(target.getEnhet(), contains(allOf(
                hasProperty("typeEnhet", is(equalTo(TEXT_VALUE))),
                hasProperty("enhet", is(equalTo(TEXT_VALUE))),
                hasProperty("datoEnhetFom", is(equalTo(LOCAL_DATE))))));
        assertThat(target.getBelopsgrense(), contains(allOf(
                hasProperty("belopGrense", is(equalTo(BELOP_GRENSE))),
                hasProperty("datoGrenseFom", is(equalTo(LOCAL_DATE))),
                hasProperty("datoGrenseTom", is(equalTo(LOCAL_DATE))),
                hasProperty("typeGrense", is(equalTo(TEXT_VALUE))),
                hasProperty("feilreg", is(equalTo(TEXT_VALUE))))));
        assertThat(target.getTekst(), contains(allOf(
                hasProperty("tekst", is(equalTo(TEXT_VALUE))),
                hasProperty("tekstLnr", is(equalTo(NUMBER_VALUE))),
                hasProperty("datoTekstFom", is(equalTo(LOCAL_DATE))),
                hasProperty("datoTekstTom", is(equalTo(LOCAL_DATE))),
                hasProperty("tekstKode", is(equalTo(TEXT_VALUE))),
                hasProperty("feilreg", is(equalTo(TEXT_VALUE))))));
    }

    @Test
    void mapOppdragsLinjeResponse_OK() {

        var oppdragResponse = Oppdragsdata.buildOppdragslinjeResponse();

        var response = mapperFacade.map(oppdragResponse, OppdragResponse.class);

        assertThat(response.getInfomelding().getBeskrMelding(), is(equalTo(TEXT_VALUE)));

        var target = response.getOppdrag();

        assertThat(target.getOppdragslinje(), contains(allOf(
                hasProperty("kodeEndringLinje", is(equalTo(Oppdrag.KodeEndringType.NY))),
                hasProperty("kodeStatusLinje", is(equalTo(Oppdrag.KodeStatusLinje.OPPH))),
                hasProperty("datoStatusFom", is(equalTo(LOCAL_DATE))),
                hasProperty("vedtakId", is(equalTo(TEXT_VALUE))),
                hasProperty("delytelseId", is(equalTo(TEXT_VALUE))),
                hasProperty("linjeId", is(equalTo(NUMBER_VALUE))),
                hasProperty("kodeKlassifik", is(equalTo(TEXT_VALUE))),
                hasProperty("datoKlassifikFom", is(equalTo(LOCAL_DATE))),
                hasProperty("datoVedtakFom", is(equalTo(LOCAL_DATE))),
                hasProperty("datoVedtakTom", is(equalTo(LOCAL_DATE))),
                hasProperty("sats", is(equalTo(BELOP_GRENSE))),
                hasProperty("fradragTillegg", is(equalTo(Oppdrag.FradragTillegg.F))),
                hasProperty("typeSats", is(equalTo(Oppdrag.SatsType._14DB))),
                hasProperty("skyldnerId", is(equalTo(TEXT_VALUE))),
                hasProperty("datoSkyldnerFom", is(equalTo(LOCAL_DATE))),
                hasProperty("kravhaverId", is(equalTo(TEXT_VALUE))),
                hasProperty("datoKravhaverFom", is(equalTo(LOCAL_DATE))),
                hasProperty("kid", is(equalTo(TEXT_VALUE))),
                hasProperty("datoKidFom", is(equalTo(LOCAL_DATE))),
                hasProperty("brukKjoreplan", is(equalTo(TEXT_VALUE))),
                hasProperty("saksbehId", is(equalTo(NOM))),
                hasProperty("utbetalesTilId", is(equalTo(TEXT_VALUE))),
                hasProperty("datoUtbetalesTilIdFom", is(equalTo(LOCAL_DATE))),
                hasProperty("kodeArbeidsgiver", is(equalTo(Oppdrag.KodeArbeidsgiver.A))),
                hasProperty("henvisning", is(equalTo(TEXT_VALUE))),
                hasProperty("typeSoknad", is(equalTo(TEXT_VALUE))),
                hasProperty("refFagsystemId", is(equalTo(TEXT_VALUE))),
                hasProperty("refOppdragsId", is(equalTo(LONG_VALUE))),
                hasProperty("refDelytelseId", is(equalTo(TEXT_VALUE))),
                hasProperty("refLinjeId", is(equalTo(NUMBER_VALUE))),
                hasProperty("refusjonsInfo", allOf(
                        hasProperty("refunderesId", is(equalTo(TEXT_VALUE))),
                        hasProperty("maksDato", is(equalTo(LOCAL_DATE))),
                        hasProperty("datoFom", is(equalTo(LOCAL_DATE))))),
                hasProperty("tekst", contains(allOf(
                        hasProperty("tekst", is(equalTo(TEXT_VALUE))),
                        hasProperty("tekstLnr", is(equalTo(NUMBER_VALUE))),
                        hasProperty("datoTekstFom", is(equalTo(LOCAL_DATE))),
                        hasProperty("datoTekstTom", is(equalTo(LOCAL_DATE))),
                        hasProperty("tekstKode", is(equalTo(TEXT_VALUE))),
                        hasProperty("feilreg", is(equalTo(TEXT_VALUE)))))),
                hasProperty("enhet", contains(allOf(
                        hasProperty("typeEnhet", is(equalTo(TEXT_VALUE))),
                        hasProperty("enhet", is(equalTo(TEXT_VALUE))),
                        hasProperty("datoEnhetFom", is(equalTo(LOCAL_DATE)))))),
                hasProperty("grad", contains(allOf(
                        hasProperty("typeGrad", is(equalTo(TEXT_VALUE))),
                        hasProperty("grad", is(equalTo(NUMBER_VALUE)))))),
                hasProperty("attestant", contains(allOf(
                        hasProperty("attestantId", is(equalTo(TEXT_VALUE))),
                        hasProperty("datoUgyldigFom", is(equalTo(LOCAL_DATE)))))),
                hasProperty("valuta", contains(allOf(
                        hasProperty("typeValuta", is(equalTo(Oppdrag.ValutaType.FAKT))),
                        hasProperty("valuta", is(equalTo(TEXT_VALUE))),
                        hasProperty("datoValutaFom", is(equalTo(LOCAL_DATE))),
                        hasProperty("feilreg", is(equalTo(TEXT_VALUE))))))
        )));
    }
}