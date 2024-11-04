package no.nav.testnav.oppdragservice.mapper;

import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.libs.dto.oppdragservice.v1.Oppdrag;
import no.nav.testnav.libs.dto.oppdragservice.v1.Oppdrag.ValutaType;
import no.nav.testnav.oppdragservice.utilty.Oppdragsdata;
import no.nav.testnav.oppdragservice.wsdl.FradragTillegg;
import no.nav.testnav.oppdragservice.wsdl.KodeArbeidsgiver;
import no.nav.testnav.oppdragservice.wsdl.KodeStatus;
import no.nav.testnav.oppdragservice.wsdl.KodeStatusLinje;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static no.nav.testnav.oppdragservice.utilty.Oppdragsdata.BELOP_GRENSE;
import static no.nav.testnav.oppdragservice.utilty.Oppdragsdata.DB2_DATE_TIME_FORMAT;
import static no.nav.testnav.oppdragservice.utilty.Oppdragsdata.LONG_VALUE;
import static no.nav.testnav.oppdragservice.utilty.Oppdragsdata.NOM;
import static no.nav.testnav.oppdragservice.utilty.Oppdragsdata.TARGET_DATE_FORMAT;
import static no.nav.testnav.oppdragservice.utilty.Oppdragsdata.TARGET_NUMBER_VALUE;
import static no.nav.testnav.oppdragservice.utilty.Oppdragsdata.TEXT_VALUE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;

@ExtendWith(MockitoExtension.class)
class OppdragRequestMappingStrategyTest {

    private MapperFacade mapperFacade;

    @BeforeEach
    void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(
                List.of(new LocalDateCustomMapping(),
                        new LocalDateTimeCustomMapping()),
                new OppdragRequestMappingStrategy());
    }

    @Test
    void mapOppdragRequest_OK() {

        var oppdragRequest = Oppdragsdata.buildOppdragRequest();

        var target = mapperFacade.map(oppdragRequest, SendInnOppdragRequest.class)
                .getRequest().getOppdrag();

        assertThat(target.getOppdragsId(), is(equalTo(LONG_VALUE)));
        assertThat(target.getBilagstype(), contains(hasProperty("typeBilag", is(equalTo(TEXT_VALUE)))));
        assertThat(target.getAvstemmingsnokkel(), contains(allOf(
                hasProperty("kodeKomponent", is(equalTo(TEXT_VALUE))),
                hasProperty("avstemmingsNokkel", is(equalTo(TEXT_VALUE))),
                hasProperty("tidspktReg", is(equalTo(DB2_DATE_TIME_FORMAT))))));
        assertThat(target.getOmpostering(), allOf(
                hasProperty("datoOmposterFom", is(equalTo(TARGET_DATE_FORMAT))),
                hasProperty("tidspktReg", is(equalTo(DB2_DATE_TIME_FORMAT))),
                hasProperty("omPostering", is(equalTo("J"))),
                hasProperty("saksbehId", is(equalTo(TEXT_VALUE))),
                hasProperty("feilreg", is(equalTo(TEXT_VALUE)))));
        assertThat(target.getKodeEndring(), is(equalTo(Oppdrag.KodeEndring.NY.toString())));
        assertThat(target.getKodeStatus(), is(equalTo(KodeStatus.ATTE)));
        assertThat(target.getDatoStatusFom(), is(equalTo(TARGET_DATE_FORMAT)));
        assertThat(target.getKodeFagomraade(), is(equalTo(TEXT_VALUE)));
        assertThat(target.getFagsystemId(), is(equalTo(TEXT_VALUE)));
        assertThat(target.getOppdragsId(), is(equalTo(LONG_VALUE)));
        assertThat(target.getUtbetFrekvens(), is(equalTo("14DG")));
        assertThat(target.getDatoForfall(), is(equalTo(TARGET_DATE_FORMAT)));
        assertThat(target.getStonadId(), is(equalTo(TEXT_VALUE)));
        assertThat(target.getOppdragGjelderId(), is(equalTo(TEXT_VALUE)));
        assertThat(target.getDatoOppdragGjelderFom(), is(equalTo(TARGET_DATE_FORMAT)));
        assertThat(target.getSaksbehId(), is(equalTo(NOM)));
        assertThat(target.getEnhet(), contains(allOf(
                hasProperty("typeEnhet", is(equalTo(TEXT_VALUE))),
                hasProperty("enhet", is(equalTo(TEXT_VALUE))),
                hasProperty("datoEnhetFom", is(equalTo(TARGET_DATE_FORMAT))))));
        assertThat(target.getBelopsgrense(), contains(allOf(
                hasProperty("belopGrense", is(equalTo(BELOP_GRENSE))),
                hasProperty("datoGrenseFom", is(equalTo(TARGET_DATE_FORMAT))),
                hasProperty("datoGrenseTom", is(equalTo(TARGET_DATE_FORMAT))),
                hasProperty("typeGrense", is(equalTo(TEXT_VALUE))),
                hasProperty("feilreg", is(equalTo(TEXT_VALUE))))));
        assertThat(target.getTekst(), contains(allOf(
                hasProperty("tekst", is(equalTo(TEXT_VALUE))),
                hasProperty("tekstLnr", is(equalTo(TARGET_NUMBER_VALUE))),
                hasProperty("datoTekstFom", is(equalTo(TARGET_DATE_FORMAT))),
                hasProperty("datoTekstTom", is(equalTo(TARGET_DATE_FORMAT))),
                hasProperty("tekstKode", is(equalTo(TEXT_VALUE))),
                hasProperty("feilreg", is(equalTo(TEXT_VALUE))))));
    }

    @Test
    void mapOppdragsLinjeRequest_OK() {

        var oppdragRequest = Oppdragsdata.buildOppdragsLinjeRequest();

        var target = mapperFacade.map(oppdragRequest, SendInnOppdragRequest.class)
                .getRequest().getOppdrag();

        assertThat(target.getOppdragslinje(), contains(allOf(
                hasProperty("kodeEndringLinje", is(equalTo("NY"))),
                hasProperty("kodeStatusLinje", is(equalTo(KodeStatusLinje.OPPH))),
                hasProperty("datoStatusFom", is(equalTo(TARGET_DATE_FORMAT))),
                hasProperty("vedtakId", is(equalTo(TEXT_VALUE))),
                hasProperty("delytelseId", is(equalTo(TEXT_VALUE))),
                hasProperty("linjeId", is(equalTo(TARGET_NUMBER_VALUE))),
                hasProperty("kodeKlassifik", is(equalTo(TEXT_VALUE))),
                hasProperty("datoKlassifikFom", is(equalTo(TARGET_DATE_FORMAT))),
                hasProperty("datoVedtakFom", is(equalTo(TARGET_DATE_FORMAT))),
                hasProperty("datoVedtakTom", is(equalTo(TARGET_DATE_FORMAT))),
                hasProperty("sats", is(equalTo(BELOP_GRENSE))),
                hasProperty("fradragTillegg", is(equalTo(FradragTillegg.F))),
                hasProperty("typeSats", is(equalTo("14DB"))),
                hasProperty("skyldnerId", is(equalTo(TEXT_VALUE))),
                hasProperty("datoSkyldnerFom", is(equalTo(TARGET_DATE_FORMAT))),
                hasProperty("kravhaverId", is(equalTo(TEXT_VALUE))),
                hasProperty("datoKravhaverFom", is(equalTo(TARGET_DATE_FORMAT))),
                hasProperty("kid", is(equalTo(TEXT_VALUE))),
                hasProperty("datoKidFom", is(equalTo(TARGET_DATE_FORMAT))),
                hasProperty("brukKjoreplan", is(equalTo(TEXT_VALUE))),
                hasProperty("saksbehId", is(equalTo(NOM))),
                hasProperty("utbetalesTilId", is(equalTo(TEXT_VALUE))),
                hasProperty("datoUtbetalesTilIdFom", is(equalTo(TARGET_DATE_FORMAT))),
                hasProperty("kodeArbeidsgiver", is(equalTo(KodeArbeidsgiver.A))),
                hasProperty("henvisning", is(equalTo(TEXT_VALUE))),
                hasProperty("typeSoknad", is(equalTo(TEXT_VALUE))),
                hasProperty("refFagsystemId", is(equalTo(TEXT_VALUE))),
                hasProperty("refOppdragsId", is(equalTo(LONG_VALUE))),
                hasProperty("refDelytelseId", is(equalTo(TEXT_VALUE))),
                hasProperty("refLinjeId", is(equalTo(TARGET_NUMBER_VALUE))),
                hasProperty("refusjonsInfo", allOf(
                        hasProperty("refunderesId", is(equalTo(TEXT_VALUE))),
                        hasProperty("maksDato", is(equalTo(TARGET_DATE_FORMAT))),
                        hasProperty("datoFom", is(equalTo(TARGET_DATE_FORMAT))))),
                hasProperty("tekst", contains(allOf(
                        hasProperty("tekst", is(equalTo(TEXT_VALUE))),
                        hasProperty("tekstLnr", is(equalTo(TARGET_NUMBER_VALUE))),
                        hasProperty("datoTekstFom", is(equalTo(TARGET_DATE_FORMAT)))))),
                hasProperty("enhet", contains(allOf(
                        hasProperty("typeEnhet", is(equalTo(TEXT_VALUE))),
                        hasProperty("enhet", is(equalTo(TEXT_VALUE))),
                        hasProperty("datoEnhetFom", is(equalTo(TARGET_DATE_FORMAT)))))),
                hasProperty("grad", contains(allOf(
                        hasProperty("typeGrad", is(equalTo(TEXT_VALUE))),
                        hasProperty("grad", is(equalTo(TARGET_NUMBER_VALUE)))))),
                hasProperty("attestant", contains(allOf(
                        hasProperty("attestantId", is(equalTo(TEXT_VALUE))),
                        hasProperty("datoUgyldigFom", is(equalTo(TARGET_DATE_FORMAT)))))),
                hasProperty("valuta", contains(allOf(
                        hasProperty("typeValuta", is(equalTo(ValutaType.FAKT.toString()))),
                        hasProperty("valuta", is(equalTo(TEXT_VALUE))),
                        hasProperty("datoValutaFom", is(equalTo(TARGET_DATE_FORMAT))),
                        hasProperty("feilreg", is(equalTo(TEXT_VALUE))))))
        )));
    }
}