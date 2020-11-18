package no.nav.registre.arena.core.service.util;

import static no.nav.registre.arena.core.service.util.ServiceUtils.BEGRUNNELSE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.TiltakArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.request.RettighetFinnTiltakRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtak;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class VedtakUtils {

    private final TiltakArenaForvalterConsumer tiltakArenaForvalterConsumer;


    public void setDatoPeriodeVedtakInnenforMaxAntallMaaneder(
            NyttVedtak vedtak,
            int antallMaaneder
    ) {
        var tilDato = vedtak.getTilDato();
        if (tilDato != null) {
            var tilDatoLimit = vedtak.getFraDato().plusMonths(antallMaaneder);

            if (tilDato.isAfter(tilDatoLimit)) {
                vedtak.setTilDato(tilDatoLimit);
            }
        }
    }

    public List<NyttVedtakTiltak> oppdaterVedtakslisteBasertPaaTiltaksdeltakelse(
            List<NyttVedtakTiltak> vedtaksliste,
            List<NyttVedtakTiltak> tiltaksdeltakelser
    ) {
        List<NyttVedtakTiltak> nyVedtaksliste = new ArrayList<>();

        for (var vedtak : vedtaksliste) {
            var deltakelse = finnNoedvendigTiltaksdeltakelse(vedtak, tiltaksdeltakelser);
            if (deltakelse != null) {
                vedtak.setTilDato(deltakelse.getTilDato());
                vedtak.setFraDato(deltakelse.getFraDato());
                nyVedtaksliste.add(vedtak);
            }
        }
        return nyVedtaksliste;
    }

    private NyttVedtakTiltak finnNoedvendigTiltaksdeltakelse(NyttVedtakTiltak vedtak, List<NyttVedtakTiltak> tiltaksdeltakelser) {
        if (tiltaksdeltakelser != null && !tiltaksdeltakelser.isEmpty()) {
            var fraDato = vedtak.getFraDato();
            var tilDato = vedtak.getTilDato();

            if (fraDato != null) {
                for (var deltakelse : tiltaksdeltakelser) {
                    var fraDatoDeltakelse = deltakelse.getFraDato();
                    var tilDatoDeltakelse = deltakelse.getTilDato();

                    if ((fraDatoDeltakelse != null && fraDato.isAfter(fraDatoDeltakelse.minusDays(1))) &&
                            (tilDato == null || tilDatoDeltakelse != null && tilDato.isBefore(tilDatoDeltakelse.plusDays(1)))) {
                        return deltakelse;
                    }

                }
            }
        }
        return null;
    }

    public NyttVedtakTiltak finnTiltak(String personident, String miljoe, NyttVedtakTiltak tiltaksdeltakelse) {
        var finnTiltak = getVedtakForFinnTiltakRequest(tiltaksdeltakelse);

        NyttVedtakTiltak tiltak = null;
        var rettighetRequest = new RettighetFinnTiltakRequest(Collections.singletonList(finnTiltak));

        rettighetRequest.setPersonident(personident);
        rettighetRequest.setMiljoe(miljoe);
        var response = tiltakArenaForvalterConsumer.finnTiltak(rettighetRequest);
        if (response != null && !response.getNyeRettigheterTiltak().isEmpty()) {
            tiltak = response.getNyeRettigheterTiltak().get(0);
        } else {
            log.info("Fant ikke tiltak for tiltakdeltakelse.");
        }
        return tiltak;
    }

    public NyttVedtakTiltak getVedtakForTiltaksdeltakelseRequest(NyttVedtakTiltak syntetiskDeltakelse) {
        var nyTiltaksdeltakelse = NyttVedtakTiltak.builder()
                .lagOppgave(syntetiskDeltakelse.getLagOppgave())
                .tiltakId(syntetiskDeltakelse.getTiltakId())
                .build();
        nyTiltaksdeltakelse.setBegrunnelse(BEGRUNNELSE);
        nyTiltaksdeltakelse.setTilDato(syntetiskDeltakelse.getTilDato());
        nyTiltaksdeltakelse.setFraDato(syntetiskDeltakelse.getFraDato());

        return nyTiltaksdeltakelse;
    }

    private NyttVedtakTiltak getVedtakForFinnTiltakRequest(NyttVedtakTiltak tiltaksdeltakelse) {
        var vedtak = NyttVedtakTiltak.builder()
                .tiltakKode(tiltaksdeltakelse.getTiltakKode())
                .tiltakProsentDeltid(tiltaksdeltakelse.getTiltakProsentDeltid())
                .tiltakVedtak(tiltaksdeltakelse.getTiltakVedtak())
                .tiltakYtelse(tiltaksdeltakelse.getTiltakYtelse())
                .tiltakAdminKode(tiltaksdeltakelse.getTiltakAdminKode())
                .build();
        vedtak.setFraDato(tiltaksdeltakelse.getFraDato());
        vedtak.setTilDato(tiltaksdeltakelse.getTilDato());
        return vedtak;
    }

    public List<NyttVedtakTiltak> removeOverlappingTiltakVedtak(
            List<NyttVedtakTiltak> vedtaksliste,
            List<? extends NyttVedtak> relatedVedtak
    ) {

        if (vedtaksliste == null || vedtaksliste.isEmpty()) {
            return vedtaksliste;
        }

        List<NyttVedtakTiltak> nyeVedtak = new ArrayList<>();

        List<NyttVedtak> vedtakToCompare = new ArrayList<>();
        if (relatedVedtak != null && relatedVedtak.isEmpty()) {
            vedtakToCompare.addAll(relatedVedtak);
        }

        for (var vedtak : vedtaksliste) {
            if (nyeVedtak.isEmpty() || (!harOverlappendeVedtak(vedtak, vedtakToCompare))) {
                nyeVedtak.add(vedtak);
                vedtakToCompare.add(vedtak);
            }
        }

        return nyeVedtak;
    }

    public List<NyttVedtakTiltak> removeOverlappingTiltakSequences(List<NyttVedtakTiltak> vedtaksliste) {
        if (vedtaksliste == null || vedtaksliste.isEmpty()) {
            return vedtaksliste;
        }

        List<NyttVedtakTiltak> nyeVedtak = new ArrayList<>();

        var vedtakSequences = getVedtakSequences(vedtaksliste);

        for (var sequence : vedtakSequences) {
            var validSequence = true;
            for (var vedtak : sequence) {
                if (!nyeVedtak.isEmpty() && harOverlappendeVedtak(vedtak, nyeVedtak)) {
                    validSequence = false;
                    break;
                }
            }
            if (validSequence) {
                nyeVedtak.addAll(sequence);
            }
        }

        return nyeVedtak;
    }

    private boolean harOverlappendeVedtak(NyttVedtakTiltak vedtak, List<? extends NyttVedtak> vedtaksliste) {
        var fraDato = vedtak.getFraDato();
        var tilDato = vedtak.getTilDato();

        for (var item : vedtaksliste) {
            var fraDatoItem = item.getFraDato();
            var tilDatoItem = item.getTilDato();

            if ((fraDato == fraDatoItem) ||
                    (fraDato.isBefore(fraDatoItem) && tilDato.isAfter(fraDatoItem)) ||
                    (fraDato.isAfter(fraDatoItem) && fraDato.isBefore(tilDatoItem))) {
                return true;
            }
        }
        return false;
    }

    private List<List<NyttVedtakTiltak>> getVedtakSequences(List<NyttVedtakTiltak> vedtak) {
        List<List<NyttVedtakTiltak>> vedtakSequences = new ArrayList<>();
        List<NyttVedtakTiltak> sequence = new ArrayList<>();
        for (var tiltak : vedtak) {
            if (tiltak.getVedtaktype() != null && tiltak.getVedtaktype().equals("O")) {
                vedtakSequences.add(sequence);
                sequence.clear();
            }
            sequence.add(tiltak);
        }

        return vedtakSequences;
    }
}
