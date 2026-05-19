package no.nav.testnav.oppdragservice.utilty;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.oppdragservice.v1.Oppdrag;
import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragRequest;
import no.nav.testnav.oppdragservice.wsdl.Bilagstype;
import no.nav.testnav.oppdragservice.wsdl.FradragTillegg;
import no.nav.testnav.oppdragservice.wsdl.Infomelding;
import no.nav.testnav.oppdragservice.wsdl.KodeArbeidsgiver;
import no.nav.testnav.oppdragservice.wsdl.KodeStatus;
import no.nav.testnav.oppdragservice.wsdl.KodeStatusLinje;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragResponse;
import no.nav.testnav.oppdragservice.wsdl.SendInnOppdragResponse2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@UtilityClass
public class Oppdragsdata {

    public static final LocalDate LOCAL_DATE = LocalDate.of(2024,11,4);
    public static final String TARGET_DATE_FORMAT = DateTimeFormatter
            .ofPattern("dd-MM-yyyy").format(LOCAL_DATE);

    public static final String DB2_DATE_TIME_FORMAT = "2024-11-04-07.17.34.123456";
    public static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.from(DateTimeFormatter
            .ofPattern("yyyy-MM-dd-HH.mm.ss.SSSSSS").parse(DB2_DATE_TIME_FORMAT));

    public static final String TEXT_VALUE = "tekst";
    public static final Integer NUMBER_VALUE = 123;
    public static final Long LONG_VALUE = 456L;
    public static final String NOM = "Z123456";
    public static final BigDecimal BELOP_GRENSE = new java.math.BigDecimal("12332.34");
    public static final BigInteger TARGET_NUMBER_VALUE = new BigInteger(NUMBER_VALUE.toString());

    public static OppdragRequest buildOppdragRequest() {

        return OppdragRequest.builder()
                .oppdrag(Oppdrag.builder()
                        .oppdragsId(LONG_VALUE)
                        .bilagstype(List.of(Oppdrag.Bilagstype.builder()
                                .typeBilag(TEXT_VALUE).build()))
                        .avstemmingsnokkel(List.of(Oppdrag.Avstemmingsnokkel
                                .builder().kodeKomponent(TEXT_VALUE)
                                .avstemmingsNokkel(TEXT_VALUE)
                                .tidspktReg(LOCAL_DATE_TIME)
                                .build()))
                        .ompostering(Oppdrag.Ompostering.builder()
                                .datoOmposterFom(LOCAL_DATE)
                                .tidspktReg(LOCAL_DATE_TIME)
                                .omPostering(Oppdrag.JaNei.J)
                                .saksbehId(TEXT_VALUE)
                                .feilreg(TEXT_VALUE)
                                .build())
                        .kodeEndring(Oppdrag.KodeEndring.NY)
                        .kodeStatus(Oppdrag.KodeStatus.ATTE)
                        .datoStatusFom(LOCAL_DATE)
                        .kodeFagomraade(TEXT_VALUE)
                        .fagsystemId(TEXT_VALUE)
                        .oppdragsId(LONG_VALUE)
                        .utbetFrekvens(Oppdrag.UtbetalingFrekvensType._14DG)
                        .datoForfall(LOCAL_DATE)
                        .stonadId(TEXT_VALUE)
                        .oppdragGjelderId(TEXT_VALUE)
                        .datoOppdragGjelderFom(LOCAL_DATE)
                        .saksbehId(NOM)
                        .enhet(List.of(Oppdrag.Enhet.builder().typeEnhet(TEXT_VALUE)
                                .enhet(TEXT_VALUE).datoEnhetFom(LOCAL_DATE).build()))
                        .belopsgrense(List.of(Oppdrag.Belopsgrense.builder()
                                .belopGrense(BELOP_GRENSE).datoGrenseFom(LOCAL_DATE)
                                .datoGrenseTom(LOCAL_DATE).typeGrense(TEXT_VALUE)
                                .feilreg(TEXT_VALUE).build()))
                        .tekst(List.of(Oppdrag.Tekst.builder().tekst(TEXT_VALUE)
                                .tekstLnr(NUMBER_VALUE).datoTekstFom(LOCAL_DATE)
                                .datoTekstTom(LOCAL_DATE).tekstKode(TEXT_VALUE)
                                .feilreg(TEXT_VALUE).build()))
                        .build())
                .build();
    }

    public static OppdragRequest buildOppdragsLinjeRequest() {

        return OppdragRequest.builder()
                .oppdrag(Oppdrag.builder()
                        .oppdragslinje(List.of(Oppdrag.Oppdragslinje.builder()
                                .kodeEndringLinje(Oppdrag.KodeEndringType.NY)
                                .kodeStatusLinje(Oppdrag.KodeStatusLinje.OPPH)
                                .datoStatusFom(LOCAL_DATE)
                                .vedtakId(TEXT_VALUE)
                                .delytelseId(TEXT_VALUE)
                                .linjeId(NUMBER_VALUE)
                                .kodeKlassifik(TEXT_VALUE)
                                .datoKlassifikFom(LOCAL_DATE)
                                .datoVedtakFom(LOCAL_DATE)
                                .datoVedtakTom(LOCAL_DATE)
                                .sats(BELOP_GRENSE)
                                .fradragTillegg(Oppdrag.FradragTillegg.F)
                                .typeSats(Oppdrag.SatsType._14DB)
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
                                .kodeArbeidsgiver(Oppdrag.KodeArbeidsgiver.A)
                                .henvisning(TEXT_VALUE)
                                .typeSoknad(TEXT_VALUE)
                                .refFagsystemId(TEXT_VALUE)
                                .refOppdragsId(LONG_VALUE)
                                .refDelytelseId(TEXT_VALUE)
                                .refLinjeId(NUMBER_VALUE)
                                .refusjonsInfo(Oppdrag.RefusjonsInfo.builder()
                                        .refunderesId(TEXT_VALUE)
                                        .maksDato(LOCAL_DATE)
                                        .datoFom(LOCAL_DATE)
                                        .build())
                                .tekst(List.of(Oppdrag.Tekst.builder()
                                        .tekst(TEXT_VALUE)
                                        .tekstLnr(NUMBER_VALUE)
                                        .datoTekstFom(LOCAL_DATE)
                                        .datoTekstTom(LOCAL_DATE)
                                        .tekstKode(TEXT_VALUE)
                                        .feilreg(TEXT_VALUE)
                                        .build()))
                                .enhet(List.of(Oppdrag.Enhet.builder()
                                        .typeEnhet(TEXT_VALUE)
                                        .enhet(TEXT_VALUE)
                                        .datoEnhetFom(LOCAL_DATE)
                                        .build()))
                                .grad(List.of(Oppdrag.Grad.builder()
                                        .typeGrad(TEXT_VALUE)
                                        .grad(NUMBER_VALUE)
                                        .build()))
                                .attestant(List.of(Oppdrag.Attestant.builder()
                                        .attestantId(TEXT_VALUE)
                                        .datoUgyldigFom(LOCAL_DATE)
                                        .build()))
                                .valuta(List.of(Oppdrag.Valuta.builder()
                                        .typeValuta(Oppdrag.ValutaType.FAKT)
                                        .valuta(TEXT_VALUE)
                                        .datoValutaFom(LOCAL_DATE)
                                        .feilreg(TEXT_VALUE)
                                        .build()))
                                .build()))
                        .build())
                .build();
    }

    public static SendInnOppdragResponse buildOppdragResponse() {

        var response = new SendInnOppdragResponse();
        response.setResponse(new SendInnOppdragResponse2());

        var infomelding = new Infomelding();
        infomelding.setBeskrMelding(TEXT_VALUE);
        response.getResponse().setInfomelding(infomelding);

        var oppdrag = new no.nav.testnav.oppdragservice.wsdl.Oppdrag();
        oppdrag.setOppdragsId(LONG_VALUE);
        response.getResponse().setOppdrag(oppdrag);

        var bilagstype = new Bilagstype();
        bilagstype.setTypeBilag(TEXT_VALUE);
        oppdrag.getBilagstype().add(bilagstype);

        var avstemmingsnokkel = new no.nav.testnav.oppdragservice.wsdl.Avstemmingsnokkel();
        avstemmingsnokkel.setKodeKomponent(TEXT_VALUE);
        avstemmingsnokkel.setAvstemmingsNokkel(TEXT_VALUE);
        avstemmingsnokkel.setTidspktReg(DB2_DATE_TIME_FORMAT);
        oppdrag.getAvstemmingsnokkel().add(avstemmingsnokkel);

        var ompostering = new no.nav.testnav.oppdragservice.wsdl.Ompostering();
        ompostering.setDatoOmposterFom(TARGET_DATE_FORMAT);
        ompostering.setTidspktReg(DB2_DATE_TIME_FORMAT);
        ompostering.setOmPostering(Oppdrag.JaNei.J.toString());
        ompostering.setSaksbehId(TEXT_VALUE);
        ompostering.setFeilreg(TEXT_VALUE);
        oppdrag.setOmpostering(ompostering);

        oppdrag.setKodeEndring(Oppdrag.KodeEndring.NY.toString());
        oppdrag.setKodeStatus(KodeStatus.ATTE);
        oppdrag.setDatoStatusFom(TARGET_DATE_FORMAT);
        oppdrag.setKodeFagomraade(TEXT_VALUE);
        oppdrag.setFagsystemId(TEXT_VALUE);
        oppdrag.setOppdragsId(LONG_VALUE);
        oppdrag.setUtbetFrekvens("14DG");
        oppdrag.setDatoForfall(TARGET_DATE_FORMAT);
        oppdrag.setStonadId(TEXT_VALUE);
        oppdrag.setOppdragGjelderId(TEXT_VALUE);
        oppdrag.setDatoOppdragGjelderFom(TARGET_DATE_FORMAT);
        oppdrag.setSaksbehId(NOM);

        var enhet = new no.nav.testnav.oppdragservice.wsdl.Enhet();
        enhet.setTypeEnhet(TEXT_VALUE);
        enhet.setEnhet(TEXT_VALUE);
        enhet.setDatoEnhetFom(TARGET_DATE_FORMAT);
        oppdrag.getEnhet().add(enhet);

        var belopsgrense = new no.nav.testnav.oppdragservice.wsdl.Belopsgrense();
        belopsgrense.setBelopGrense(BELOP_GRENSE);
        belopsgrense.setDatoGrenseFom(TARGET_DATE_FORMAT);
        belopsgrense.setDatoGrenseTom(TARGET_DATE_FORMAT);
        belopsgrense.setTypeGrense(TEXT_VALUE);
        belopsgrense.setFeilreg(TEXT_VALUE);
        oppdrag.getBelopsgrense().add(belopsgrense);

        var tekst = new no.nav.testnav.oppdragservice.wsdl.Tekst();
        tekst.setTekst(TEXT_VALUE);
        tekst.setTekstLnr(TARGET_NUMBER_VALUE);
        tekst.setDatoTekstFom(TARGET_DATE_FORMAT);
        tekst.setDatoTekstTom(TARGET_DATE_FORMAT);
        tekst.setTekstKode(TEXT_VALUE);
        tekst.setFeilreg(TEXT_VALUE);
        oppdrag.getTekst().add(tekst);

        return response;
    }

    public static SendInnOppdragResponse buildOppdragslinjeResponse() {

        var response = new SendInnOppdragResponse();
        response.setResponse(new SendInnOppdragResponse2());

        var infomelding = new Infomelding();
        infomelding.setBeskrMelding(TEXT_VALUE);
        response.getResponse().setInfomelding(infomelding);

        var oppdrag = new no.nav.testnav.oppdragservice.wsdl.Oppdrag();
        response.getResponse().setOppdrag(oppdrag);

        var oppdragslinje = new no.nav.testnav.oppdragservice.wsdl.Oppdragslinje();
        oppdrag.getOppdragslinje().add(oppdragslinje);

        oppdragslinje.setKodeEndringLinje(Oppdrag.KodeEndringType.NY.toString());
        oppdragslinje.setKodeStatusLinje(KodeStatusLinje.OPPH);
        oppdragslinje.setDatoStatusFom(TARGET_DATE_FORMAT);
        oppdragslinje.setVedtakId(TEXT_VALUE);
        oppdragslinje.setDelytelseId(TEXT_VALUE);
        oppdragslinje.setLinjeId(TARGET_NUMBER_VALUE);
        oppdragslinje.setKodeKlassifik(TEXT_VALUE);
        oppdragslinje.setDatoKlassifikFom(TARGET_DATE_FORMAT);
        oppdragslinje.setDatoVedtakFom(TARGET_DATE_FORMAT);
        oppdragslinje.setDatoVedtakTom(TARGET_DATE_FORMAT);
        oppdragslinje.setSats(BELOP_GRENSE);
        oppdragslinje.setFradragTillegg(FradragTillegg.F);
        oppdragslinje.setTypeSats("14DB");
        oppdragslinje.setSkyldnerId(TEXT_VALUE);
        oppdragslinje.setDatoSkyldnerFom(TARGET_DATE_FORMAT);
        oppdragslinje.setKravhaverId(TEXT_VALUE);
        oppdragslinje.setDatoKravhaverFom(TARGET_DATE_FORMAT);
        oppdragslinje.setKid(TEXT_VALUE);
        oppdragslinje.setDatoKidFom(TARGET_DATE_FORMAT);
        oppdragslinje.setBrukKjoreplan(TEXT_VALUE);
        oppdragslinje.setSaksbehId(NOM);
        oppdragslinje.setUtbetalesTilId(TEXT_VALUE);
        oppdragslinje.setDatoUtbetalesTilIdFom(TARGET_DATE_FORMAT);
        oppdragslinje.setKodeArbeidsgiver(KodeArbeidsgiver.A);
        oppdragslinje.setHenvisning(TEXT_VALUE);
        oppdragslinje.setTypeSoknad(TEXT_VALUE);
        oppdragslinje.setRefFagsystemId(TEXT_VALUE);
        oppdragslinje.setRefOppdragsId(LONG_VALUE);
        oppdragslinje.setRefDelytelseId(TEXT_VALUE);
        oppdragslinje.setRefLinjeId(TARGET_NUMBER_VALUE);

        var refusjonsInfo = new no.nav.testnav.oppdragservice.wsdl.RefusjonsInfo();
        refusjonsInfo.setRefunderesId(TEXT_VALUE);
        refusjonsInfo.setMaksDato(TARGET_DATE_FORMAT);
        refusjonsInfo.setDatoFom(TARGET_DATE_FORMAT);
        oppdragslinje.setRefusjonsInfo(refusjonsInfo);

        var tekst = new no.nav.testnav.oppdragservice.wsdl.Tekst();
        tekst.setTekst(TEXT_VALUE);
        tekst.setTekstLnr(TARGET_NUMBER_VALUE);
        tekst.setDatoTekstFom(TARGET_DATE_FORMAT);
        tekst.setDatoTekstTom(TARGET_DATE_FORMAT);
        tekst.setTekstKode(TEXT_VALUE);
        tekst.setFeilreg(TEXT_VALUE);
        oppdragslinje.getTekst().add(tekst);

        var enhet = new no.nav.testnav.oppdragservice.wsdl.Enhet();
        enhet.setTypeEnhet(TEXT_VALUE);
        enhet.setEnhet(TEXT_VALUE);
        enhet.setDatoEnhetFom(TARGET_DATE_FORMAT);
        oppdragslinje.getEnhet().add(enhet);

        var grad = new no.nav.testnav.oppdragservice.wsdl.Grad();
        grad.setTypeGrad(TEXT_VALUE);
        grad.setGrad(TARGET_NUMBER_VALUE);
        oppdragslinje.getGrad().add(grad);

        var attestant = new no.nav.testnav.oppdragservice.wsdl.Attestant();
        attestant.setAttestantId(TEXT_VALUE);
        attestant.setDatoUgyldigFom(TARGET_DATE_FORMAT);
        oppdragslinje.getAttestant().add(attestant);

        var valuta = new no.nav.testnav.oppdragservice.wsdl.Valuta();
        valuta.setTypeValuta(Oppdrag.ValutaType.FAKT.toString());
        valuta.setValuta(TEXT_VALUE);
        valuta.setDatoValutaFom(TARGET_DATE_FORMAT);
        valuta.setFeilreg(TEXT_VALUE);
        oppdragslinje.getValuta().add(valuta);

        return response;
    }
}
