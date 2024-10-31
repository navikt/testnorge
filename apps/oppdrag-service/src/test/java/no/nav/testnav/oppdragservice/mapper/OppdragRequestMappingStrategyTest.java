package no.nav.testnav.oppdragservice.mapper;

import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragRequest;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragRequest.SatsType;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragRequest.ValuteType;
import no.nav.testnav.oppdragservice.wsdl.FradragTillegg;
import no.nav.testnav.oppdragservice.wsdl.KodeArbeidsgiver;
import no.nav.testnav.oppdragservice.wsdl.KodeStatus;
import no.nav.testnav.oppdragservice.wsdl.KodeStatusLinje;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;

@ExtendWith(MockitoExtension.class)
class OppdragRequestMappingStrategyTest {

    private static final LocalDate LOCAL_DATE = LocalDate.now();
    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.now();

    private static final String TARGET_DATE_FORMAT = DateTimeFormatter
            .ofPattern("dd-MM-yyyy").format(LOCAL_DATE);
    private static final String DB2_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd-HH.mm.ss.SSSSSS").format(LOCAL_DATE_TIME);

    private static final String TEXT_VALUE = "tekst";
    private static final Integer NUMBER_VALUE = 123;
    private static final Long LONG_VALUE = 456L;
    private static final String NOM = "Z123456";
    private static final BigDecimal BELOP_GRENSE = new java.math.BigDecimal("12332.34");
    private static final BigInteger TARGET_NUMBER_VALUE = new BigInteger(NUMBER_VALUE.toString());

    private MapperFacade mapperFacade;

    @BeforeEach
    void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(
                List.of(new LocalDateCustomMapping(),
                        new LocalDateTimeCustomMapping()),
                new OppdragRequestMappingStrategy());
    }

    @Test
    void mapOppdrag_OK() {

        var oppdragRequest = buildOppdragRequest();

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
                hasProperty("saksbehId", is(equalTo(TEXT_VALUE)))));
        assertThat(target.getKodeEndring(), is(equalTo(OppdragRequest.KodeEndring.NY.toString())));
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
    void mapOppdragsLinje_OK() {

        var oppdragRequest = buildOppdragsLinje();

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
                        hasProperty("typeValuta", is(equalTo(ValuteType.FAKT.toString()))),
                        hasProperty("valuta", is(equalTo(TEXT_VALUE))),
                        hasProperty("datoValutaFom", is(equalTo(TARGET_DATE_FORMAT))),
                        hasProperty("feilreg", is(equalTo(TEXT_VALUE))))))
        )));
    }

    private static OppdragRequest buildOppdragRequest() {

        return OppdragRequest.builder()
                .oppdragsId(LONG_VALUE)
                .bilagstype(List.of(OppdragRequest.Bilagstype.builder()
                        .typeBilag(TEXT_VALUE).build()))
                .avstemmingsnokkel(List.of(OppdragRequest.Avstemmingsnokkel
                        .builder().kodeKomponent(TEXT_VALUE)
                        .avstemmingsNokkel(TEXT_VALUE)
                        .tidspktReg(LOCAL_DATE_TIME).build()))
                .ompostering(OppdragRequest.Ompostering.builder()
                        .datoOmposterFom(LOCAL_DATE)
                        .tidspktReg(LOCAL_DATE_TIME)
                        .omPostering(OppdragRequest.JaNei.J)
                        .saksbehId(TEXT_VALUE).build())
                .kodeEndring(OppdragRequest.KodeEndring.NY)
                .kodeStatus(OppdragRequest.KodeStatus.ATTE)
                .datoStatusFom(LOCAL_DATE)
                .kodeFagomraade(TEXT_VALUE)
                .fagsystemId(TEXT_VALUE)
                .oppdragsId(LONG_VALUE)
                .utbetFrekvens(OppdragRequest.UtbetalingFrekvensType._14DG)
                .datoForfall(LOCAL_DATE)
                .stonadId(TEXT_VALUE)
                .oppdragGjelderId(TEXT_VALUE)
                .datoOppdragGjelderFom(LOCAL_DATE)
                .saksbehId(NOM)
                .enhet(List.of(OppdragRequest.Enhet.builder().typeEnhet(TEXT_VALUE)
                        .enhet(TEXT_VALUE).datoEnhetFom(LOCAL_DATE).build()))
                .belopsgrense(List.of(OppdragRequest.Belopsgrense.builder()
                        .belopGrense(BELOP_GRENSE).datoGrenseFom(LOCAL_DATE)
                        .datoGrenseTom(LOCAL_DATE).typeGrense(TEXT_VALUE)
                        .feilreg(TEXT_VALUE).build()))
                .tekst(List.of(OppdragRequest.Tekst.builder().tekst(TEXT_VALUE)
                        .tekstLnr(NUMBER_VALUE).datoTekstFom(LOCAL_DATE)
                        .datoTekstTom(LOCAL_DATE).tekstKode(TEXT_VALUE)
                        .feilreg(TEXT_VALUE).build()))
                .build();
    }

    private static OppdragRequest buildOppdragsLinje() {

        return OppdragRequest.builder()
                .oppdragslinje(List.of(OppdragRequest.Oppdragslinje.builder()
                        .kodeEndringLinje(OppdragRequest.KodeEndringType.NY)
                        .kodeStatusLinje(OppdragRequest.KodeStatusLinje.OPPH)
                        .datoStatusFom(LOCAL_DATE)
                        .vedtakId(TEXT_VALUE)
                        .delytelseId(TEXT_VALUE)
                        .linjeId(NUMBER_VALUE)
                        .kodeKlassifik(TEXT_VALUE)
                        .datoKlassifikFom(LOCAL_DATE)
                        .datoVedtakFom(LOCAL_DATE)
                        .datoVedtakTom(LOCAL_DATE)
                        .sats(BELOP_GRENSE)
                        .fradragTillegg(OppdragRequest.FradragTillegg.F)
                        .typeSats(SatsType._14DB)
                        .skyldnerId(TEXT_VALUE)
                        .datoSkyldnerFom(LOCAL_DATE)
                        .kravhaverId(TEXT_VALUE)
                        .datoKravhaverFom(LOCAL_DATE)
                        .kid(TEXT_VALUE)
                        .datoKidFom(LOCAL_DATE)
                        .brukKjoreplan(TEXT_VALUE)
                        .saksbehId(NOM)
                        .utbetalesTilId(TEXT_VALUE)
                        .datoUtbetalesTilIdFom(LOCAL_DATE)
                        .kodeArbeidsgiver(OppdragRequest.KodeArbeidsgiver.A)
                        .henvisning(TEXT_VALUE)
                        .typeSoknad(TEXT_VALUE)
                        .refFagsystemId(TEXT_VALUE)
                        .refOppdragsId(LONG_VALUE)
                        .refDelytelseId(TEXT_VALUE)
                        .refLinjeId(NUMBER_VALUE)
                        .refusjonsInfo(OppdragRequest.RefusjonsInfo.builder()
                                .refunderesId(TEXT_VALUE)
                                .maksDato(LOCAL_DATE)
                                .datoFom(LOCAL_DATE)
                                .build())
                        .tekst(List.of(OppdragRequest.Tekst.builder()
                                .tekst(TEXT_VALUE)
                                .tekstLnr(NUMBER_VALUE)
                                .datoTekstFom(LOCAL_DATE)
                                .build()))
                        .enhet(List.of(OppdragRequest.Enhet.builder()
                                .typeEnhet(TEXT_VALUE)
                                .enhet(TEXT_VALUE)
                                .datoEnhetFom(LOCAL_DATE)
                                .build()))
                        .grad(List.of(OppdragRequest.Grad.builder()
                                .typeGrad(TEXT_VALUE)
                                .grad(NUMBER_VALUE)
                                .build()))
                        .attestant(List.of(OppdragRequest.Attestant.builder()
                                .attestantId(TEXT_VALUE)
                                .datoUgyldigFom(LOCAL_DATE)
                                .build()))
                        .valuta(List.of(OppdragRequest.Valuta.builder()
                                .typeValuta(ValuteType.FAKT)
                                .valuta(TEXT_VALUE)
                                .datoValutaFom(LOCAL_DATE)
                                .feilreg(TEXT_VALUE)
                                .build()))
                        .build()))
                .build();
    }
}