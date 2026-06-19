package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.altinn3.Altinn3TilgangServiceConsumer;
import no.nav.dolly.consumer.altinn3.dto.Altinn3TilgangDTO;
import no.nav.dolly.consumer.brukerservice.BrukerServiceConsumer;
import no.nav.dolly.consumer.brukerservice.dto.BrukerDTO;
import no.nav.dolly.consumer.teamkatalog.TeamkatalogConsumer;
import no.nav.dolly.consumer.teamkatalog.dto.TeamkatalogDTO;
import no.nav.dolly.domain.dto.BestillingProgressDTO;
import no.nav.dolly.domain.dto.DashboardDollyTeamsDTO;
import no.nav.dolly.domain.dto.DashboardOrganisasjonerDTO;
import no.nav.dolly.domain.dto.DashboardOversiktDTO;
import no.nav.dolly.domain.dto.DashboardPersonerDTO;
import no.nav.dolly.domain.dto.DashboardTeamsDTO;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.projection.BestillingerFragment;
import no.nav.dolly.domain.projection.DollyTeam2Fragment;
import no.nav.dolly.domain.projection.DollyTeamFragment;
import no.nav.dolly.domain.projection.OrganisasjonFragment;
import no.nav.dolly.domain.projection.OversiktFragment;
import no.nav.dolly.domain.projection.TeamFragment;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

import java.time.Month;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Math.toIntExact;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final String INGEN_TEAM = "Tilhører ikke noe team";
    private static final Set<String> IDENTITETSFELT = Set.of("sistOppdatert", "bestillingId", "ident", "master");

    private final Altinn3TilgangServiceConsumer altinn3TilgangServiceConsumer;
    private final BestillingRepository bestillingRepository;
    private final BestillingProgressRepository bestillingProgressRepository;
    private final BrukerRepository brukerRepository;
    private final BrukerServiceConsumer brukerServiceConsumer;
    private final R2dbcEntityTemplate entityTemplate;
    private final JsonMapper jsonMapper;
    private final TeamkatalogConsumer teamkatalogConsumer;
    private final TeamRepository teamRepository;

    public Flux<DashboardPersonerDTO> getPersonerStatus() {

        return bestillingRepository.findBestillingerOrderBySistOppdatert()
                .groupBy(BestillingerFragment::getDato)
                .flatMap(Flux::collectList)
                .map(fragmentliste ->
                        DashboardPersonerDTO.builder()
                                .dato(fragmentliste.getFirst().getDato())
                                .personerTotalt(fragmentliste.stream()
                                        .mapToLong(BestillingerFragment::getPersoner).sum())
                                .nye(sumByStatus(fragmentliste, BestillingerFragment::getGjenopprettstatus, "NYBESTILLING"))
                                .gjenopprettede(sumByStatus(fragmentliste, BestillingerFragment::getGjenopprettstatus, "GJENOPPRETTING"))
                                .pdlFeil(sumByStatus(fragmentliste, BestillingerFragment::getPdlstatus, "FEIL"))
                                .andreFeil(sumByStatus(fragmentliste, BestillingerFragment::getAnnenstatus, "FEIL"))
                                .build())
                .sort(Comparator.comparing(DashboardPersonerDTO::getDato).reversed());
    }

    public Flux<DashboardTeamsDTO> getTeamsStatus() {

        return brukerRepository.findAll()
                .filter(bruker -> isNotBlank(bruker.getEpost()))
                .map(Bruker::getEpost)
                .distinct()
                .buffer(500)
                .flatMap(teamkatalogConsumer::getTeamForEpost, 3)
                .collect(Collectors.toMap(TeamkatalogDTO::getEmail, TeamkatalogDTO::getTeamNavn))
                .flatMapMany(teams -> bestillingRepository.findBestillingerForTeamsOrderBySistOppdatert()
                        .groupBy(TeamFragment::getInterval)
                        .flatMap(Flux::collectList)
                        .flatMap(fragments -> Mono.just(
                                Tuples.of(groupFragmentsByTeam(fragments, teams), fragments.getFirst().getInterval()))))
                .map(tuple ->
                        DashboardTeamsDTO.builder()
                                .interval(tuple.getT2())
                                .teams(tuple.getT1().entrySet().stream()
                                        .map(entry -> new DashboardTeamsDTO.Entry(entry.getKey(), entry.getValue().size()))
                                        .sorted(Comparator.comparing(DashboardTeamsDTO.Entry::getTeam))
                                        .toList())
                                .totaltAntallTeams(tuple.getT1().size())
                                .totaltUnikeBrukere((int) tuple.getT1().values().stream()
                                        .flatMap(Set::stream)
                                        .distinct()
                                        .count())
                                .build())
                .sort(Comparator.comparing(DashboardTeamsDTO::getInterval).reversed());
    }

    public Flux<DashboardOrganisasjonerDTO> getOrganisasjonerStatus() {

        return Mono.zip(altinn3TilgangServiceConsumer.getOrganisasjoner()
                                .collect(Collectors.toMap(Altinn3TilgangDTO::getOrganisasjonsnummer, value -> value)),
                        brukerServiceConsumer.getAlleBrukere()
                                .collect(Collectors.toMap(BrukerDTO::getId, BrukerDTO::getOrganisasjonsnummer)))
                .flatMapMany(oppslag -> bestillingRepository.findBestillingerForOrganisasjonerOrderBySistOppdatert()
                        .groupBy(OrganisasjonFragment::getInterval)
                        .flatMap(Flux::collectList)
                        .flatMap(fragments -> Mono.just(
                                Tuples.of(groupFragmentsByOrganisasjon(fragments, oppslag.getT2()), fragments.getFirst().getInterval())))
                        .map(tuple -> DashboardOrganisasjonerDTO.builder()
                                .interval(tuple.getT2())
                                .organisasjoner(tuple.getT1().entrySet().stream()
                                        .map(entry -> toOrganisasjonEntry(entry.getKey(), entry.getValue(), oppslag.getT1()))
                                        .sorted(Comparator.comparing(DashboardOrganisasjonerDTO.Entry::getNavn))
                                        .toList())
                                .totaltAntallOrganisasjoner(tuple.getT1().size())
                                .totaltUnikeBrukere((int) tuple.getT1().values().stream()
                                        .flatMap(Set::stream)
                                        .distinct()
                                        .count())
                                .build()))
                .sort(Comparator.comparing(DashboardOrganisasjonerDTO::getInterval).reversed());
    }

    public Flux<DashboardDollyTeamsDTO> getDollyTeamsStatus() {

        return teamRepository.findAllTeamBrukere()
                .collect(Collectors.toMap(DollyTeam2Fragment::getBrukerid, DollyTeam2Fragment::getAntall))
                .flatMapMany(oppslag -> bestillingRepository.findBestillingerForDollyTeamsOrderBySistOppdatert()
                        .groupBy(DollyTeamFragment::getInterval)
                        .flatMap(Flux::collectList)
                        .map(fragments -> {
                            var teams = fragments.stream()
                                    .map(fragment -> toDollyTeamEntry(fragment, oppslag))
                                    .sorted(Comparator.comparing(DashboardDollyTeamsDTO.Entry::getNavn))
                                    .toList();
                            return DashboardDollyTeamsDTO.builder()
                                    .interval(fragments.getFirst().getInterval())
                                    .teams(teams)
                                    .totaltAntallTeams(teams.size())
                                    .totaltAntallMedlemmer(teams.stream()
                                            .mapToInt(DashboardDollyTeamsDTO.Entry::getAntallMedlemmer)
                                            .sum())
                                    .build();
                        }))
                .sort(Comparator.comparing(DashboardDollyTeamsDTO::getInterval).reversed());
    }

    private static long sumByStatus(List<BestillingerFragment> fragments,
                                    Function<BestillingerFragment, String> statusGetter,
                                    String value) {

        return fragments.stream()
                .filter(f -> value.equals(statusGetter.apply(f)))
                .mapToLong(BestillingerFragment::getPersoner)
                .sum();
    }

    private static Map<String, Set<String>> groupFragmentsByTeam(List<TeamFragment> fragments,
                                                                 Map<String, List<String>> teams) {

        var grouped = new HashMap<String, Set<String>>();
        fragments.forEach(fragment -> {
            var teamNames = teams.getOrDefault(fragment.getEpost(), List.of(INGEN_TEAM));
            teamNames.forEach(team ->
                    grouped.computeIfAbsent(team, _ -> new HashSet<>()).add(fragment.getEpost()));
        });
        return grouped;
    }

    private static Map<String, Set<String>> groupFragmentsByOrganisasjon(List<OrganisasjonFragment> fragments,
                                                                         Map<String, String> brukerToOrgnummer) {

        var grouped = new HashMap<String, Set<String>>();
        fragments.forEach(fragment -> {
            var orgNummer = brukerToOrgnummer.get(fragment.getBrukerid());
            grouped.computeIfAbsent(orgNummer, _ -> new HashSet<>()).add(fragment.getBrukerid());
        });
        return grouped;
    }

    private static DashboardOrganisasjonerDTO.Entry toOrganisasjonEntry(String orgNummer,
                                                                        Set<String> brukere,
                                                                        Map<String, Altinn3TilgangDTO> organisasjonerOppslag) {

        var info = organisasjonerOppslag.getOrDefault(orgNummer, Altinn3TilgangDTO.builder()
                .navn("Ukjent organisasjon")
                .organisasjonsform("Ukjent organisasjonsform")
                .build());
        return new DashboardOrganisasjonerDTO.Entry(orgNummer, info.getNavn(), info.getOrganisasjonsform(), brukere.size());
    }

    private static DashboardDollyTeamsDTO.Entry toDollyTeamEntry(DollyTeamFragment fragment,
                                                                 Map<String, Long> oppslag) {

        var info = fragment.getInformasjon().split("\\|");
        return new DashboardDollyTeamsDTO.Entry(info[0], info[1], toIntExact(oppslag.get(info[2])));
    }

    public Flux<JsonNode> getFeilstatusSummert(int year, Month month) {

        var filter = "%4d-%02d".formatted(year, month.getValue());
        return bestillingProgressRepository.findStatusColumns()
                .reduce(new StringBuilder(), (StringBuilder sb, String column) ->
                        sb.append(" or lower(bp.")
                                .append(column)
                                .append(") like '%feil%'"))
                .map(kolonner -> "select b.sist_oppdatert::date bestilling_dato, bp.* " +
                                 "from bestilling b " +
                                 "join bestilling_progress bp on bp.bestilling_id = b.id " +
                                 "where to_char(b.sist_oppdatert, 'YYYY-MM') = :range " +
                                 "and (" +
                                 kolonner.substring(4) +
                                 ") order by bestilling_dato")
                .flatMapMany(query -> entityTemplate.getDatabaseClient()
                        .sql(query)
                        .bind("range", filter)
                        .map((row, metadata) -> entityTemplate.getConverter()
                                .read(BestillingProgressDTO.class, row, metadata))
                        .all())
                .groupBy(BestillingProgressDTO::getBestillingDato)
                .flatMap(Flux::collectList)
                .flatMap(this::tilFeilstatusSummert);
    }

    public Flux<JsonNode> getFeilstatusDetaljert(int year, Month month, int day) {

        var filter = "%4d-%02d-%02d".formatted(year, month.getValue(), day);
        return bestillingProgressRepository.findStatusColumns()
                .reduce(new StringBuilder(), (StringBuilder sb, String column) ->
                        sb.append(" or lower(bp.")
                                .append(column)
                                .append(") like '%feil%'"))
                .map(kolonner -> "select b.sist_oppdatert, bp.* " +
                                 "from bestilling b " +
                                 "join bestilling_progress bp on bp.bestilling_id = b.id " +
                                 "where to_char(b.sist_oppdatert, 'YYYY-MM-DD') = :range " +
                                 "and (" +
                                 kolonner.substring(4) +
                                 ") order by b.sist_oppdatert")
                .flatMapMany(query -> entityTemplate.getDatabaseClient()
                        .sql(query)
                        .bind("range", filter)
                        .map((row, metadata) -> entityTemplate.getConverter()
                                .read(BestillingProgressDTO.class, row, metadata))
                        .all())
                .sort(Comparator.comparing(BestillingProgressDTO::getSistOppdatert))
                .flatMap(this::tilFeilJson);
    }

    private Mono<JsonNode> tilFeilstatusSummert(List<BestillingProgressDTO> bestillingProgressDTOS) {

        var resultat = new AtomicReference<>(new HashMap<>());
        bestillingProgressDTOS.forEach(progress -> {
            var kilde = (ObjectNode) jsonMapper.valueToTree(progress);

            for (var navn : kilde.propertyNames()) {
                var verdi = kilde.get(navn);
                if (!IDENTITETSFELT.contains(navn)) {
                    if ("bestillingDato".equals(navn)) {
                        resultat.get().putIfAbsent(navn, verdi);
                    } else if (verdi.isString() && verdi.asString().toLowerCase().contains("feil")) {
                        if (resultat.get().containsKey(navn)) {
                            var teller = (Integer) resultat.get().get(navn);
                            resultat.get().put(navn, teller + 1);
                        } else {
                            resultat.get().put(navn, 1);
                        }
                    }
                }
            }
        });
        return Flux.fromIterable(resultat.get().entrySet())
                .collect(Collectors.toMap(entry -> {
                            if (((String) entry.getKey()).contains("Status")) {
                                return ((String) entry.getKey())
                                        .replace("Status", "Feil");
                            } else if (entry.getKey().equals("feil")) {
                                return "andreFeil";
                            } else {
                                return entry.getKey();
                            }
                        }
                        , Map.Entry::getValue))
                .flatMap(summert -> summert.isEmpty() ? Mono.empty() :
                        Mono.just(jsonMapper.valueToTree(summert)));
    }

    private Mono<JsonNode> tilFeilJson(BestillingProgressDTO progress) {

        var kilde = (ObjectNode) jsonMapper.valueToTree(progress);
        var resultat = jsonMapper.createObjectNode();

        for (var navn : kilde.propertyNames()) {
            var verdi = kilde.get(navn);
            if (IDENTITETSFELT.contains(navn)) {
                resultat.set(navn, verdi);
            } else if (verdi.isString() && verdi.asString().toLowerCase().contains("feil")) {
                resultat.set(navn, tilJsonEllerTekst(verdi, navn));
            }
        }
        return Mono.just(resultat);
    }

    private JsonNode tilJsonEllerTekst(JsonNode json, String navn) {

        var verdi = json.asString().trim();
        if (verdi.startsWith("{") || verdi.startsWith("[")) {
            try {
                if (!"pdlOrdreStatus".equals(navn)) {
                    return jsonMapper.readTree(verdi);
                } else {
                    var ordreRespons = jsonMapper.readValue(verdi, OrdreResponseDTO.class);
                    var feilRespons = getFeil(ordreRespons);
                    var responsTekst = jsonMapper.writeValueAsString(feilRespons);
                    return jsonMapper.readTree(responsTekst);
                }
            } catch (JacksonException e) {
                log.debug("Statusfelt er ikke gyldig JSON – beholdes som tekst", e);
            }
        }
        return json;
    }

    private static OrdreResponseDTO getFeil(OrdreResponseDTO response) {

        return OrdreResponseDTO.builder()
                .hovedperson(getError(response.getHovedperson()))
                .relasjoner(response.getRelasjoner().stream()
                        .map(DashboardService::getError)
                        .toList())
                .build();
    }

    private static OrdreResponseDTO.PersonHendelserDTO getError(OrdreResponseDTO.PersonHendelserDTO hendelser) {

        return OrdreResponseDTO.PersonHendelserDTO.builder()
                .ident(hendelser.getIdent())
                .ordrer(hendelser.getOrdrer().stream()
                        .filter(status -> status.getHendelser().stream()
                                .anyMatch(hendelse -> isNotBlank(hendelse.getError())))
                        .map(status -> OrdreResponseDTO.PdlStatusDTO.builder()
                                .ident(status.getIdent())
                                .infoElement(status.getInfoElement())
                                .hendelser(status.getHendelser().stream()
                                        .filter(hendelse -> isNotBlank(hendelse.getError()))
                                        .map(hendelse -> OrdreResponseDTO.HendelseDTO.builder()
                                                .id(hendelse.getId())
                                                .status(hendelse.getStatus())
                                                .error(hendelse.getError())
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .build();
    }

    public Flux<DashboardOversiktDTO> getPerioderOversikt() {

        return bestillingRepository.findByAvailIntervals()
                .groupBy(OversiktFragment::getMaaned)
                .flatMap(Flux::collectList)
                .map(fragmenter -> DashboardOversiktDTO.builder()
                        .aarManed(fragmenter.getFirst().getMaaned())
                        .aar(Integer.parseInt(fragmenter.getFirst().getMaaned().substring(0, 4)))
                        .maaned(Month.of(Integer.parseInt(fragmenter.getFirst().getMaaned().substring(5))))
                        .totaltAntallPersoner(fragmenter.stream()
                                .map(OversiktFragment::getAntall)
                                .mapToInt(Long::intValue)
                                .sum())
                        .nye(fragmenter.stream()
                                .filter(fragment -> "NYBESTILLING".equals(fragment.getGjenopprettstatus()))
                                .map(OversiktFragment::getAntall)
                                .mapToInt(Long::intValue)
                                .sum())
                        .gjenopprettede(fragmenter.stream()
                                .filter(fragment -> "GJENOPPRETTING".equals(fragment.getGjenopprettstatus()))
                                .map(OversiktFragment::getAntall)
                                .mapToInt(Long::intValue)
                                .sum())
                        .build())
                .sort(Comparator.comparing(DashboardOversiktDTO::getAarManed).reversed());
    }
}
