package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.ArenaForvalterConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.SyntDagpengerConsumer;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.DagpengerResponseDTO;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.NyeDagpenger;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.DagpengerRequestDTO;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.DagpengevedtakDTO;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.Dagpengerettighet;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.DagpengesoknadDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ArenaDagpengerService {

    private final SyntDagpengerConsumer syntDagpengerConsumer;
    private final IdentService identService;
    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final Random rand = new Random();
    private final String DAGO_ENHETSNR = "4450";
    private final String DAGO_KOMMENTAR = "Hoveddokument: Søknad om dagpenger (ikke permittert)";
    private final String PERM_ENHETSNR = "4455";
    private final String PERM_KOMMENTAR = "Hoveddokument: Søknad om dagpenger ved permittering";

    public DagpengevedtakDTO getSyntetiskDagpengevedtak(Dagpengerettighet rettighet) {
        return syntDagpengerConsumer.syntetiserDagpengevedtak(rettighet);
    }

    public Map<String, List<DagpengerResponseDTO>> registrerArenaBrukereMedDagpenger(int antall, String miljoe){
        //TODO må være bosatt? min/max alder?
        var utvalgteIdenter = identService.getUtvalgteIdenterIAldersgruppe(antall, 18, 66, false);

        Map<String, List<DagpengerResponseDTO>> responses = new HashMap<>();
        for (var ident: utvalgteIdenter){
            // TODO registrer ident i Arena forst?
            // TODO registrer inntektsmeldinger på ident
            var response = sendDagpenger(ident.getIdent(), miljoe, rand.nextDouble() > 0.2);
            responses.put(ident.getIdent(), response);
        }
        return responses;
    }

    public List<DagpengerResponseDTO> sendDagpenger(String ident, String miljoe, Boolean sendVedtak) {
        var rettighetKode = rand.nextDouble() > 0.13 ? Dagpengerettighet.DAGO : Dagpengerettighet.PERM;

        var soknadRequest = getDagpengesoknadRequest(ident, miljoe, rettighetKode);
        var soknadResponse = arenaForvalterConsumer.opprettDagpengerSoknad(soknadRequest);

        if (soknadResponse.getFeiledeDagpenger().isEmpty() && !soknadResponse.getNyeDagpenger().isEmpty() && sendVedtak) {
            var vedtakRequest = getDagpengevedtakRequest(ident, miljoe, rettighetKode, soknadResponse.getNyeDagpenger().get(0));
            var vedtakResponse = arenaForvalterConsumer.opprettDagpengerVedtak(vedtakRequest);
            return Arrays.asList(soknadResponse, vedtakResponse);
        } else {
            return Collections.singletonList(soknadResponse);
        }
    }

    private DagpengerRequestDTO getDagpengesoknadRequest(String personident, String miljoe, Dagpengerettighet rettighetKode) {
        var soknad = rettighetKode.equals(Dagpengerettighet.DAGO) ?
                new DagpengesoknadDTO(DAGO_ENHETSNR, DAGO_KOMMENTAR) : new DagpengesoknadDTO(PERM_ENHETSNR, PERM_KOMMENTAR);

        return DagpengerRequestDTO.builder()
                .personident(personident)
                .miljoe(miljoe)
                .nyeMottaDagpengesoknad(Collections.singletonList(soknad))
                .build();
    }

    private DagpengerRequestDTO getDagpengevedtakRequest(String personident, String miljoe, Dagpengerettighet rettighetKode, NyeDagpenger soknadResponse) {
        var vedtak = getSyntetiskDagpengevedtak(rettighetKode);
        vedtak.setSakId(soknadResponse.getArenaSakId());
        vedtak.setOppgaveId(soknadResponse.getOppgaveId());

        return DagpengerRequestDTO.builder()
                .personident(personident)
                .miljoe(miljoe)
                .nyeMottaDagpengevedtak(Collections.singletonList(vedtak))
                .build();
    }

}
