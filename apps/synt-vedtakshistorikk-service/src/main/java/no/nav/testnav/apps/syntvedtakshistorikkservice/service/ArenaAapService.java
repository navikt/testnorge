package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.rettighet.RettighetRequest;
import no.nav.testnav.libs.data.dollysearchservice.v1.legacy.PersonDTO;
import no.nav.testnav.libs.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.NyttVedtakAap;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.DatoUtils.setDatoPeriodeVedtakInnenforMaxAntallMaaneder;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.RequestUtils.getRettighetAap115Request;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.RequestUtils.getRettighetAapRequest;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.RequestUtils.getRettighetFritakMeldekortRequest;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.RequestUtils.getRettighetTvungenForvaltningRequest;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.RequestUtils.getRettighetUngUfoerRequest;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.ARENA_AAP_UNG_UFOER_DATE_LIMIT;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.SYKEPENGEERSTATNING_MAKS_PERIODE;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.AKTIVITETSFASE_SYKEPENGEERSTATNING;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.VedtakUtils.getAapSekvenser;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class ArenaAapService {

    private final IdentService identService;

    public List<NyttVedtakAap> getIkkeAvsluttendeVedtakAap115(
            List<NyttVedtakAap> aap115
    ) {
        List<NyttVedtakAap> vedtaksliste = new ArrayList<>();
        if (nonNull(aap115) && !aap115.isEmpty()) {
            vedtaksliste = aap115.stream().filter(vedtak -> !vedtak.getVedtaktype().equals("S")).toList();
        }
        return vedtaksliste;
    }

    public List<NyttVedtakAap> getAvsluttendeVedtakAap115(
            List<NyttVedtakAap> aap115
    ) {
        List<NyttVedtakAap> vedtaksliste = new ArrayList<>();
        if (nonNull(aap115) && !aap115.isEmpty()) {
            vedtaksliste = aap115.stream().filter(vedtak -> vedtak.getVedtaktype().equals("S")).toList();
        }
        return vedtaksliste;
    }

    public void opprettVedtakAap115(
            List<NyttVedtakAap> aap115,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        if (nonNull(aap115) && !aap115.isEmpty()) {
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
        if (nonNull(aap) && !aap.isEmpty()) {
            for (var vedtak : aap) {
                rettigheter.add(getRettighetAapRequest(personident, miljoe, vedtak));
            }

        }
    }

    public void opprettVedtakUngUfoer(
            Vedtakshistorikk historikk,
            PersonDTO person,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var foedselsdato = person.getFoedselsdato().getFoedselsdato();
        var ungUfoer = historikk.getUngUfoer();
        if (nonNull(ungUfoer) && !ungUfoer.isEmpty()) {
            for (var vedtak : ungUfoer) {
                var rettighetRequest = getRettighetUngUfoerRequest(person.getIdent(), miljoe, foedselsdato, vedtak);

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
                if (isNull(kontoinfo)) break;
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
        if (nonNull(fritakMeldekort) && !fritakMeldekort.isEmpty()) {
            for (var vedtak : fritakMeldekort) {
                rettigheter.add(getRettighetFritakMeldekortRequest(personident, miljoe, vedtak));
            }
        }
    }


    public void oppdaterAapSykepengeerstatningDatoer(Vedtakshistorikk historikk) {
        var aapVedtak = historikk.getAap();
        if (nonNull(aapVedtak) && !aapVedtak.isEmpty()) {
            var oppdaterteVedtak = new ArrayList<NyttVedtakAap>(aapVedtak.size());
            var aapSekvenser = getAapSekvenser(aapVedtak);

            for (var sekvens : aapSekvenser) {
                var antallDagerEndret = 0;
                for (var vedtak : sekvens) {
                    var nyendring = oppdaterVedtakDatoBasertPaaAktivitetsfase(antallDagerEndret, vedtak);
                    antallDagerEndret += nyendring;
                    oppdaterteVedtak.add(vedtak);
                }
            }
            historikk.setAap(oppdaterteVedtak);
        }
    }

    private long oppdaterVedtakDatoBasertPaaAktivitetsfase(int antallDagerEndret, NyttVedtakAap vedtak){
        if (nonNull(vedtak.getFraDato())) {
            vedtak.setFraDato(vedtak.getFraDato().minusDays(antallDagerEndret));
            if (AKTIVITETSFASE_SYKEPENGEERSTATNING.equals(vedtak.getAktivitetsfase())) {
                if (isNull(vedtak.getTilDato())) {
                    vedtak.setTilDato(vedtak.getFraDato().plusMonths(6));
                } else {
                    vedtak.setTilDato(vedtak.getTilDato().minusDays(antallDagerEndret));

                    var originalTilDato = vedtak.getTilDato();
                    setDatoPeriodeVedtakInnenforMaxAntallMaaneder(vedtak, SYKEPENGEERSTATNING_MAKS_PERIODE);
                    var nyTilDato = vedtak.getTilDato();

                    return ChronoUnit.DAYS.between(nyTilDato, originalTilDato);
                }
            } else if (nonNull(vedtak.getTilDato())) {
                vedtak.setTilDato(vedtak.getTilDato().minusDays(antallDagerEndret));
            }
        }
        return 0;
    }


    public List<NyttVedtakAap> fjernAapUngUfoerMedUgyldigeDatoer(List<NyttVedtakAap> ungUfoer) {
        List<NyttVedtakAap> nyUngUfoer = new ArrayList<>();
        if (nonNull(ungUfoer)) {
            nyUngUfoer = ungUfoer.stream()
                    .filter(vedtak -> !vedtak.getFraDato().isAfter(ARENA_AAP_UNG_UFOER_DATE_LIMIT))
                    .toList();
        }

        return nyUngUfoer.isEmpty() ? null : nyUngUfoer;
    }

}
