package no.nav.testnav.apps.syntvedtakshistorikkservice.service.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.FinnTiltakRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.rettighet.RettighetAap115Request;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.rettighet.RettighetAapRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.rettighet.RettighetEndreDeltakerstatusRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.rettighet.RettighetFritakMeldekortRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.rettighet.RettighetTilleggRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.rettighet.RettighetTilleggsytelseRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.rettighet.RettighetTiltaksaktivitetRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.rettighet.RettighetTiltaksdeltakelseRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.rettighet.RettighetTiltakspengerRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.rettighet.RettighetTvungenForvaltningRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.rettighet.RettighetUngUfoerRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.KontoinfoResponse;

import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.Deltakerstatuser;
import no.nav.testnav.libs.domain.dto.arena.testnorge.tilleggsstoenad.Vedtaksperiode;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.forvalter.Adresse;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.forvalter.Forvalter;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.forvalter.Konto;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestUtils {

    private final Random rand = new Random();
    private final ServiceUtils serviceUtils;

    private static final Map<String, List<String>> deltakerstatuskoderMedAarsakkoder;
    private static final Map<String, List<KodeMedSannsynlighet>> vedtakMedAktitivetskode;

    static {
        deltakerstatuskoderMedAarsakkoder = new HashMap<>();
        deltakerstatuskoderMedAarsakkoder.put(Deltakerstatuser.NEITAKK.toString(), Arrays.asList("ANN", "BEGA", "FRISM", "FTOAT", "HENLU", "SYK", "UTV"));
        deltakerstatuskoderMedAarsakkoder.put(Deltakerstatuser.IKKEM.toString(), Arrays.asList("ANN", "BEGA", "SYK"));
        deltakerstatuskoderMedAarsakkoder.put(Deltakerstatuser.DELAVB.toString(), Arrays.asList("ANN", "BEGA", "FTOAT", "SYK"));

        vedtakMedAktitivetskode = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();

        URL resourceAktivitetkoder = Resources.getResource("files/vedtak_til_aktivitetkode.json");

        try {
            Map<String, List<KodeMedSannsynlighet>> map = objectMapper.readValue(resourceAktivitetkoder, new TypeReference<>() {
            });
            vedtakMedAktitivetskode.putAll(map);

        } catch (IOException e) {
            log.error("Kunne ikke laste inn fordeling.", e);
        }
    }

    public static RettighetAap115Request getRettighetAap115Request(
            String personident,
            String miljoe,
            NyttVedtakAap vedtak
    ) {
        vedtak.setBegrunnelse(BEGRUNNELSE);

        var rettighetRequest = new RettighetAap115Request(Collections.singletonList(vedtak));
        rettighetRequest.setPersonident(personident);
        rettighetRequest.setMiljoe(miljoe);

        return rettighetRequest;
    }

    public static RettighetAapRequest getRettighetAapRequest(
            String personident,
            String miljoe,
            NyttVedtakAap vedtak
    ) {
        vedtak.setBegrunnelse(BEGRUNNELSE);

        var rettighetRequest = new RettighetAapRequest(Collections.singletonList(vedtak));
        rettighetRequest.setPersonident(personident);
        rettighetRequest.setMiljoe(miljoe);

        return rettighetRequest;
    }

    public static RettighetUngUfoerRequest getRettighetUngUfoerRequest(
            String personident,
            String miljoe,
            LocalDate foedselsdato,
            NyttVedtakAap vedtak
    ) {
        var alderPaaVedtaksdato = Math.toIntExact(ChronoUnit.YEARS.between(foedselsdato, vedtak.getFraDato()));
        if (alderPaaVedtaksdato < MIN_ALDER_UNG_UFOER || alderPaaVedtaksdato > MAX_ALDER_UNG_UFOER) {
            log.error("Kan ikke opprette vedtak ung-ufør på ident som er {} år gammel.", alderPaaVedtaksdato);
            return null;
        }

        vedtak.setBegrunnelse(BEGRUNNELSE);

        var rettighetRequest = new RettighetUngUfoerRequest(Collections.singletonList(vedtak));
        rettighetRequest.setPersonident(personident);
        rettighetRequest.setMiljoe(miljoe);

        return rettighetRequest;
    }

    public static RettighetTvungenForvaltningRequest getRettighetTvungenForvaltningRequest(
            String personident,
            String miljoe,
            KontoinfoResponse identMedKontonummer,
            NyttVedtakAap vedtak
    ) {
        vedtak.setBegrunnelse(BEGRUNNELSE);
        vedtak.setForvalter(buildForvalter(identMedKontonummer));

        var rettighetRequest = new RettighetTvungenForvaltningRequest(Collections.singletonList(vedtak));
        rettighetRequest.setPersonident(personident);
        rettighetRequest.setMiljoe(miljoe);

        return rettighetRequest;
    }

    public static RettighetFritakMeldekortRequest getRettighetFritakMeldekortRequest(
            String personident,
            String miljoe,
            NyttVedtakAap vedtak
    ) {
        vedtak.setBegrunnelse(BEGRUNNELSE);

        var rettighetRequest = new RettighetFritakMeldekortRequest(Collections.singletonList(vedtak));
        rettighetRequest.setPersonident(personident);
        rettighetRequest.setMiljoe(miljoe);

        return rettighetRequest;
    }

    public static RettighetTiltaksdeltakelseRequest getRettighetTiltaksdeltakelseRequest(
            String personident,
            String miljoe,
            NyttVedtakTiltak syntetiskDeltakelse
    ) {
        var nyTiltaksdeltakelse = NyttVedtakTiltak.builder()
                .lagOppgave(syntetiskDeltakelse.getLagOppgave())
                .tiltakId(syntetiskDeltakelse.getTiltakId())
                .build();
        nyTiltaksdeltakelse.setBegrunnelse(BEGRUNNELSE);
        nyTiltaksdeltakelse.setTilDato(syntetiskDeltakelse.getTilDato());
        nyTiltaksdeltakelse.setFraDato(syntetiskDeltakelse.getFraDato());

        var rettighetRequest = new RettighetTiltaksdeltakelseRequest(Collections.singletonList(nyTiltaksdeltakelse));
        rettighetRequest.setPersonident(personident);
        rettighetRequest.setMiljoe(miljoe);

        return rettighetRequest;
    }

    public static FinnTiltakRequest getFinnTiltakRequest(String personident, String miljoe, NyttVedtakTiltak tiltaksdeltakelse) {
        var vedtak = NyttVedtakTiltak.builder()
                .tiltakKode(tiltaksdeltakelse.getTiltakKode())
                .tiltakProsentDeltid(tiltaksdeltakelse.getTiltakProsentDeltid())
                .tiltakVedtak(tiltaksdeltakelse.getTiltakVedtak())
                .tiltakYtelse(tiltaksdeltakelse.getTiltakYtelse())
                .tiltakAdminKode(tiltaksdeltakelse.getTiltakAdminKode())
                .build();
        vedtak.setFraDato(tiltaksdeltakelse.getFraDato());
        vedtak.setTilDato(tiltaksdeltakelse.getTilDato());

        return new FinnTiltakRequest(personident, miljoe, Collections.singletonList(vedtak));
    }

    public RettighetEndreDeltakerstatusRequest getRettighetEndreDeltakerstatusRequest(
            String ident,
            String miljoe,
            NyttVedtakTiltak tiltaksdeltakelse,
            String deltakerstatuskode
    ) {
        var vedtak = new NyttVedtakTiltak();
        vedtak.setDeltakerstatusKode(deltakerstatuskode);
        vedtak.setTiltakId(tiltaksdeltakelse.getTiltakId());

        if (deltakerstatuskoderMedAarsakkoder.containsKey(deltakerstatuskode)) {
            List<String> aarsakkoder = deltakerstatuskoderMedAarsakkoder.get(deltakerstatuskode);
            String aarsakkode = aarsakkoder.get(rand.nextInt(aarsakkoder.size()));
            vedtak.setAarsakKode(aarsakkode);
        }

        var rettighetRequest = new RettighetEndreDeltakerstatusRequest(Collections.singletonList(vedtak));
        rettighetRequest.setPersonident(ident);
        rettighetRequest.setMiljoe(miljoe);

        return rettighetRequest;
    }

    public static RettighetTiltakspengerRequest getRettighetTiltakspengerRequest(
            String personident,
            String miljoe,
            NyttVedtakTiltak vedtak
    ) {
        vedtak.setBegrunnelse(BEGRUNNELSE);

        var rettighetRequest = new RettighetTiltakspengerRequest(Collections.singletonList(vedtak));
        rettighetRequest.setPersonident(personident);
        rettighetRequest.setMiljoe(miljoe);

        return rettighetRequest;
    }

    public static RettighetTilleggsytelseRequest getRettighetTilleggsytelseRequest(
            String personident,
            String miljoe,
            NyttVedtakTiltak vedtak
    ) {
        vedtak.setBegrunnelse(BEGRUNNELSE);

        var rettighetRequest = new RettighetTilleggsytelseRequest(Collections.singletonList(vedtak));
        rettighetRequest.setPersonident(personident);
        rettighetRequest.setMiljoe(miljoe);

        return rettighetRequest;
    }

    public RettighetTiltaksaktivitetRequest getRettighetTiltaksaktivitetRequest(
            String personident,
            String miljoe,
            String rettighetKode,
            Vedtaksperiode vedtaksperiode
    ) {
        var statuskode = "BEHOV";
        var aktivitetkode = serviceUtils.velgKodeBasertPaaSannsynlighet(
                vedtakMedAktitivetskode.get(rettighetKode)).getKode();

        var nyttVedtakTiltak = new NyttVedtakTiltak();
        nyttVedtakTiltak.setAktivitetStatuskode(statuskode);
        nyttVedtakTiltak.setAktivitetkode(aktivitetkode);
        nyttVedtakTiltak.setBeskrivelse(BEGRUNNELSE);
        nyttVedtakTiltak.setFraDato(vedtaksperiode.getFom());
        nyttVedtakTiltak.setTilDato(vedtaksperiode.getTom());

        var rettighetRequest = new RettighetTiltaksaktivitetRequest(Collections.singletonList(nyttVedtakTiltak));
        rettighetRequest.setPersonident(personident);
        rettighetRequest.setMiljoe(miljoe);

        return rettighetRequest;
    }

    public static RettighetTilleggRequest getRettighetTilleggRequest(
            String personident,
            String miljoe,
            NyttVedtakTillegg vedtak
    ) {
        vedtak.setBegrunnelse(BEGRUNNELSE);

        var rettighetRequest = new RettighetTilleggRequest(Collections.singletonList(vedtak));
        rettighetRequest.setPersonident(personident);
        rettighetRequest.setMiljoe(miljoe);

        return rettighetRequest;
    }

    private static Forvalter buildForvalter(KontoinfoResponse identMedKontoinfo) {
        var konto = Konto.builder()
                .kontonr(identMedKontoinfo.getKontonummer())
                .build();
        var adresse = Adresse.builder()
                .adresseLinje1(identMedKontoinfo.getAdresseLinje1())
                .adresseLinje2(identMedKontoinfo.getAdresseLinje2())
                .adresseLinje3(identMedKontoinfo.getAdresseLinje3())
                .fodselsnr(identMedKontoinfo.getFnr())
                .landkode(identMedKontoinfo.getLandkode())
                .navn(identMedKontoinfo.getLandkode())
                .postnr(identMedKontoinfo.getPostnr())
                .build();
        return Forvalter.builder()
                .gjeldendeKontonr(konto)
                .utbetalingsadresse(adresse)
                .build();
    }

}
