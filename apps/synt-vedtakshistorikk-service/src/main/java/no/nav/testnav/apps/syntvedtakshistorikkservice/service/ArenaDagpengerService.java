package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.SyntDagpengerConsumer;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.DagpengerResponseDTO;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.NyeDagpenger;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.DagpengerRequestDTO;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.DagpengesoknadDTO;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.DagpengevedtakDTO;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.Dagpengerettighet;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.Vilkaar;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.GodkjenningerReellArbeidssoker;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.TaptArbeidstid;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.Dagpengeperiode;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger.Vedtaksperiode;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArenaDagpengerService {

    private final SyntDagpengerConsumer syntDagpengerConsumer;
    private final IdentService identService;
    private final ArenaForvalterService arenaForvalterService;
    private final InntektService inntektService;
    private final TagsService tagsService;
    private final Random rand = new Random();

    private static final String DAGO_ENHETSNR = "4450";
    private static final String DAGO_KOMMENTAR = "Hoveddokument: Søknad om dagpenger (ikke permittert)";
    private static final String PERM_ENHETSNR = "4455";
    private static final String PERM_KOMMENTAR = "Hoveddokument: Søknad om dagpenger ved permittering";
    private static final LocalDate MINIMUM_DATE = LocalDate.of(2019, 7, 1);

    public static final List<Vilkaar> DAGPENGER_VILKAAR =
            List.of(
                    new Vilkaar("GEOMOB", "J"),
                    new Vilkaar("HELDELT", "J"),
                    new Vilkaar("IFAFP", "J"),
                    new Vilkaar("IFFODSP", "J"),
                    new Vilkaar("IFGAFISK", "J"),
                    new Vilkaar("IFSYKEP", "J"),
                    new Vilkaar("OATVIST", "J"),
                    new Vilkaar("PATVIST", "J"),
                    new Vilkaar("MEDLFOLKT", "J"),
                    new Vilkaar("MELDMØT", "J"),
                    new Vilkaar("ARBFØR", "J"),
                    new Vilkaar("ARBVILL", "J"),
                    new Vilkaar("INORGE", "J"),
                    new Vilkaar("TILTDELT", "J"),
                    new Vilkaar("UNDER67", "J"),
                    new Vilkaar("UNDERUTD", "J"),
                    new Vilkaar("UTESTENG", "J"),
                    new Vilkaar("IFUFTRY", "J"),
                    new Vilkaar("TAPTINNT", "J"),
                    new Vilkaar("MOTTATTDOK", "J")
            );

    public Map<String, List<DagpengerResponseDTO>> registrerArenaBrukereMedDagpenger(int antall, String miljoe, boolean forenklet) {
        var utvalgteIdenter = identService.getUtvalgteIdenterIAldersgruppe(antall, 18, 66, true);

        Map<String, List<DagpengerResponseDTO>> responses = new HashMap<>();
        for (var ident : utvalgteIdenter) {
            var foedselsdato = ident.getFoedsel().getFoedselsdato();

            var minDate = foedselsdato.plusYears(18).isAfter(MINIMUM_DATE) ? foedselsdato.plusYears(18) : MINIMUM_DATE;

            var dato = LocalDate.now().minusMonths(rand.nextInt(Math.toIntExact(ChronoUnit.MONTHS.between(minDate, LocalDate.now()))));
            if (inntektService.opprettetInntektPaaIdentFoerDato(ident.getIdent(), dato)) {
                try {
                    arenaForvalterService.opprettArbeidssoekerDagpenger(ident, miljoe, dato);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    inntektService.deleteInntekterPaaIdent(ident.getIdent());
                    continue;
                }

                List<DagpengerResponseDTO> response;
                if (forenklet) {
                    response = sendForenkletDagpenger(ident.getIdent(), miljoe, dato);
                } else {
                    response = sendDagpenger(ident.getIdent(), miljoe, rand.nextDouble() > 0.2 ? dato : null);
                }
                responses.put(ident.getIdent(), response);
            }
        }
        if (!responses.isEmpty()) {
            var identer = new ArrayList<>(responses.keySet());
            var personer = utvalgteIdenter.stream().filter(person -> identer.contains(person.getIdent())).toList();
            tagsService.opprettetTagsPaaIdenterOgPartner(personer);
        }
        return responses;
    }

    public List<DagpengerResponseDTO> sendForenkletDagpenger(String ident, String miljoe, LocalDate vedtakdato) {
        var vedtakRequest = getDefaultDagpengevedtakRequest(ident, miljoe, vedtakdato, Dagpengerettighet.DAGO);
        return Collections.singletonList(arenaForvalterService.opprettDagpengevedtak(vedtakRequest));
    }

    private List<DagpengerResponseDTO> sendDagpenger(String ident, String miljoe, LocalDate vedtakdato) {
        var rettighetKode = rand.nextDouble() > 0.13 ? Dagpengerettighet.DAGO : Dagpengerettighet.PERM;

        var soknadRequest = getDagpengesoknadRequest(ident, miljoe, rettighetKode);
        var soknadResponse = arenaForvalterService.opprettMottaDagpengesoknad(soknadRequest);

        if (soknadResponse.getFeiledeDagpenger().isEmpty() && !soknadResponse.getNyeDagpenger().isEmpty() && nonNull(vedtakdato)) {
            var vedtakRequest = getDagpengevedtakRequest(ident, miljoe, vedtakdato, rettighetKode, soknadResponse.getNyeDagpenger().get(0));
            if (isNull(vedtakRequest)) {
                return Collections.singletonList(soknadResponse);
            }
            var vedtakResponse = arenaForvalterService.opprettMottaDagpengevedtak(vedtakRequest);
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

    private DagpengerRequestDTO getDagpengevedtakRequest(String personident, String miljoe, LocalDate startdato, Dagpengerettighet rettighetKode, NyeDagpenger soknadResponse) {
        var vedtak = syntDagpengerConsumer.syntetiserDagpengevedtak(rettighetKode, startdato);
        if (nonNull(vedtak)) {
            vedtak.setSakId(soknadResponse.getArenaSakId());
            vedtak.setOppgaveId(soknadResponse.getOppgaveId());

            return DagpengerRequestDTO.builder()
                    .personident(personident)
                    .miljoe(miljoe)
                    .nyeMottaDagpengevedtak(Collections.singletonList(vedtak))
                    .build();
        } else {
            return null;
        }
    }

    private DagpengerRequestDTO getDefaultDagpengevedtakRequest(String personident, String miljoe, LocalDate startdato, Dagpengerettighet rettighetKode) {
        var vedtak = DagpengevedtakDTO.builder()
                .vedtaksperiode(Vedtaksperiode.builder()
                        .fom(startdato)
                        .build())
                .datoMottatt(startdato)
                .rettighetKode(rettighetKode)
                .utfall("JA")
                .dagpengeperiode(Dagpengeperiode.builder()
                        .nullstillPeriodeteller("J")
                        .nullstillPermitteringsteller("N")
                        .build())
                .godkjenningerReellArbeidssoker(GodkjenningerReellArbeidssoker.builder()
                        .godkjentDeltidssoker("J")
                        .godkjentLokalArbeidssoker("J")
                        .godkjentUtdanning("J")
                        .build())
                .taptArbeidstid(TaptArbeidstid.builder()
                        .anvendtRegelKode("GJSNITT12MND")
                        .fastsattArbeidstid(30.0)
                        .naavaerendeArbeidstid(0.0)
                        .build())
                .vedtaktype("O")
                .vilkaar(DAGPENGER_VILKAAR)
                .build();

        return DagpengerRequestDTO.builder()
                .personident(personident)
                .miljoe(miljoe)
                .nyeDagp(Collections.singletonList(vedtak))
                .build();
    }

}
