package no.nav.registre.arena.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import no.nav.registre.arena.core.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.RettighetSyntConsumer;
import no.nav.registre.arena.core.consumer.rs.request.RettighetUngUfoerRequest;
import no.nav.registre.arena.core.consumer.rs.request.UngUfoer;
import no.nav.registre.arena.core.consumer.rs.request.Vilkaar;
import no.nav.registre.arena.core.consumer.rs.responses.NyeBrukereResponse;
import no.nav.registre.arena.core.consumer.rs.responses.rettighet.UngUfoer.UngUfoerForvalterResponse;
import no.nav.registre.arena.core.consumer.rs.responses.rettighet.UngUfoer.UngUfoerSyntResponse;
import no.nav.registre.arena.domain.NyBrukerFeil;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Service
@RequiredArgsConstructor
public class RettighetService {

    private static final int MIN_ALDER_UNG_UFOER = 18;
    private static final int MAX_ALDER_UNG_UFOER = 36;
    private static final String BEGRUNNELSE = "Syntetisert rettighet";

    private final HodejegerenConsumer hodejegerenConsumer;
    private final RettighetSyntConsumer rettighetSyntConsumer;
    private final RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;
    private final SyntetiseringService syntetiseringService;
    private final Random rand;

    public List<UngUfoerForvalterResponse> genererUngUfoer(Long avspillergruppeId, String miljoe, int antallNyeIdenter) {
        List<String> levendeIdenterIAldersgruppe = hodejegerenConsumer.getLevende(avspillergruppeId, MIN_ALDER_UNG_UFOER, MAX_ALDER_UNG_UFOER);
        Collections.shuffle(levendeIdenterIAldersgruppe);
        List<String> utvalgteIdenter = levendeIdenterIAldersgruppe.subList(0, antallNyeIdenter);
        List<UngUfoerSyntResponse> syntetiserteRettigheter = rettighetSyntConsumer.syntetiserRettighetUngUfoer(utvalgteIdenter.size());

        List<RettighetUngUfoerRequest> rettigheter = new ArrayList<>();
        for (UngUfoerSyntResponse syntetisertRettighet : syntetiserteRettigheter) {
            List<UngUfoer> nyeAaungufoer = new ArrayList<>(Collections.singletonList(
                    UngUfoer.builder()
                            .begrunnelse(BEGRUNNELSE)
                            .fraDato(syntetisertRettighet.getFraDato())
                            .datoMottatt(syntetisertRettighet.getDatoMottatt())
                            .vedtaktype(syntetisertRettighet.getVedtaktype())
                            .vilkaar(buildVilkaarListe(syntetisertRettighet))
                            .utfall(syntetisertRettighet.getUtfall())
                            .build()
            ));
            rettigheter.add(
                    RettighetUngUfoerRequest.builder()
                            .personident(utvalgteIdenter.remove(rand.nextInt(utvalgteIdenter.size())))
                            .miljoe(miljoe)
                            .nyeAaungufor(nyeAaungufoer)
                            .build()
            );
        }

        List<String> identerIArena = syntetiseringService.hentEksisterendeArbeidsoekerIdenter();
        List<String> uregistrerteBrukere = rettigheter.stream().filter(rettighet -> !identerIArena.contains(rettighet.getPersonident())).map(RettighetUngUfoerRequest::getPersonident).collect(Collectors.toList());

        NyeBrukereResponse nyeBrukereResponse = syntetiseringService.byggArbeidsoekereOgLagreIHodejegeren(uregistrerteBrukere, miljoe);
        List<String> feiledeIdenter = nyeBrukereResponse.getNyBrukerFeilList().stream().map(NyBrukerFeil::getPersonident).collect(Collectors.toList());
        rettigheter.removeIf(rettighet -> feiledeIdenter.contains(rettighet.getPersonident()));

        return rettighetArenaForvalterConsumer.opprettRettighetUngUfoer(rettigheter);
    }

    private List<Vilkaar> buildVilkaarListe(UngUfoerSyntResponse rettighet) {
        List<Vilkaar> vilkaar = new ArrayList<>();
        if (rettighet.getAafor36() != null) {
            vilkaar.add(Vilkaar.builder()
                    .kode("AAFOR36")
                    .status(rettighet.getAafor36())
                    .build());
        }
        if (rettighet.getAanedssl() != null) {
            vilkaar.add(Vilkaar.builder()
                    .kode("AANEDSSL")
                    .status(rettighet.getAanedssl())
                    .build());

        }
        if (rettighet.getAassldok() != null) {
            vilkaar.add(Vilkaar.builder()
                    .kode("AASSLDOK")
                    .status(rettighet.getAassldok())
                    .build());
        }
        if (rettighet.getAaungneds() != null) {
            vilkaar.add(Vilkaar.builder()
                    .kode("AAUNGNEDS")
                    .status(rettighet.getAaungneds())
                    .build());
        }
        return vilkaar;
    }
}
