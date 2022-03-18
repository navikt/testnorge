package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.rettighet.RettighetRequest;
import no.nav.testnav.libs.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.testnav.libs.servletcore.util.IdentUtil;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.DatoUtils.setDatoPeriodeVedtakInnenforMaxAntallMaaneder;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.RequestUtils.getRettighetAap115Request;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.RequestUtils.getRettighetAapRequest;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.RequestUtils.getRettighetFritakMeldekortRequest;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.RequestUtils.getRettighetTvungenForvaltningRequest;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.RequestUtils.getRettighetUngUfoerRequest;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.ARENA_AAP_UNG_UFOER_DATE_LIMIT;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.SYKEPENGEERSTATNING_MAKS_PERIODE;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.AKTIVITETSFASE_SYKEPENGEERSTATNING;


@Slf4j
@Service
@RequiredArgsConstructor
public class ArenaAapService {

    private final IdentService identService;

    public List<NyttVedtakAap> getIkkeAvsluttendeVedtakAap115(
            List<NyttVedtakAap> aap115
    ) {
        List<NyttVedtakAap> vedtaksliste = new ArrayList<>();
        if (aap115 != null && !aap115.isEmpty()) {
            vedtaksliste = aap115.stream().filter(vedtak -> !vedtak.getVedtaktype().equals("S"))
                    .collect(Collectors.toList());
        }
        return vedtaksliste;
    }

    public List<NyttVedtakAap> getAvsluttendeVedtakAap115(
            List<NyttVedtakAap> aap115
    ) {
        List<NyttVedtakAap> vedtaksliste = new ArrayList<>();
        if (aap115 != null && !aap115.isEmpty()) {
            vedtaksliste = aap115.stream().filter(vedtak -> vedtak.getVedtaktype().equals("S"))
                    .collect(Collectors.toList());
        }
        return vedtaksliste;
    }

    public void opprettVedtakAap115(
            List<NyttVedtakAap> aap115,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        if (aap115 != null && !aap115.isEmpty()) {
            for (var vedtak : aap115) {
                rettigheter.add(getRettighetAap115Request(personident, miljoe, vedtak));
            }
        }
    }

    public void opprettVedtakAap(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var aap = historikk.getAap();
        if (aap != null && !aap.isEmpty()) {
            for (var vedtak : aap) {
                rettigheter.add(getRettighetAapRequest(personident, miljoe, vedtak));
            }

        }
    }

    public void opprettVedtakUngUfoer(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var foedselsdato = IdentUtil.getFoedselsdatoFraIdent(personident);
        var ungUfoer = historikk.getUngUfoer();
        if (ungUfoer != null && !ungUfoer.isEmpty()) {
            for (var vedtak : ungUfoer) {
                var rettighetRequest = getRettighetUngUfoerRequest(personident, miljoe, foedselsdato, vedtak);

                if (rettighetRequest != null) {
                    rettigheter.add(rettighetRequest);
                }
            }

        }
    }

    public void opprettVedtakTvungenForvaltning(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var tvungenForvaltning = historikk.getTvungenForvaltning();
        if (tvungenForvaltning != null && !tvungenForvaltning.isEmpty()) {
            for (var vedtak : tvungenForvaltning) {
                var kontoinfo = identService.getIdentMedKontoinformasjon();
                if (kontoinfo == null) break;
                var rettighetRequest = getRettighetTvungenForvaltningRequest(
                        personident,
                        miljoe,
                        kontoinfo,
                        vedtak);
                rettigheter.add(rettighetRequest);
            }
        }
    }

    public void opprettVedtakFritakMeldekort(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var fritakMeldekort = historikk.getFritakMeldekort();
        if (fritakMeldekort != null && !fritakMeldekort.isEmpty()) {
            for (var vedtak : fritakMeldekort) {
                rettigheter.add(getRettighetFritakMeldekortRequest(personident, miljoe, vedtak));
            }
        }
    }


    public void oppdaterAapSykepengeerstatningDatoer(List<NyttVedtakAap> aapVedtak) {
        if (aapVedtak != null) {
            var antallDagerEndret = 0;
            for (var vedtak : aapVedtak) {
                if (AKTIVITETSFASE_SYKEPENGEERSTATNING.equals(vedtak.getAktivitetsfase()) && vedtak.getFraDato() != null) {
                    vedtak.setFraDato(vedtak.getFraDato().minusDays(antallDagerEndret));
                    if (vedtak.getTilDato() == null) {
                        vedtak.setTilDato(vedtak.getFraDato().plusMonths(6));
                    } else {
                        vedtak.setTilDato(vedtak.getTilDato().minusDays(antallDagerEndret));

                        var originalTilDato = vedtak.getTilDato();
                        setDatoPeriodeVedtakInnenforMaxAntallMaaneder(vedtak, SYKEPENGEERSTATNING_MAKS_PERIODE);
                        var nyTilDato = vedtak.getTilDato();

                        antallDagerEndret += ChronoUnit.DAYS.between(nyTilDato, originalTilDato);
                    }
                }
            }
        }
    }


    public List<NyttVedtakAap> fjernAapUngUfoerMedUgyldigeDatoer(List<NyttVedtakAap> ungUfoer) {
        List<NyttVedtakAap> nyUngUfoer = new ArrayList<>();
        if (ungUfoer != null) {
            nyUngUfoer = ungUfoer.stream().filter(vedtak ->
                            !vedtak.getFraDato().isAfter(ARENA_AAP_UNG_UFOER_DATE_LIMIT))
                    .collect(Collectors.toList());
        }

        return nyUngUfoer.isEmpty() ? null : nyUngUfoer;
    }

}
