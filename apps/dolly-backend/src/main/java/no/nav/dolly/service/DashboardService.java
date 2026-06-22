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
import no.nav.dolly.domain.dto.DashboardBestillingerDTO;
import no.nav.dolly.domain.dto.DashboardTeamsDTO;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.projection.BestillingerFragment;
import no.nav.dolly.domain.projection.DollyTeam2Fragment;
import no.nav.dolly.domain.projection.DollyTeamFragment;
import no.nav.dolly.domain.projection.OrganisasjonFragment;
import no.nav.dolly.domain.projection.OversiktFragment;
import no.nav.dolly.domain.projection.TeamFragment;
import no.nav.dolly.domain.resultset.BAFeilkoder;
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
import tools.jackson.databind.node.StringNode;

import java.time.Month;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Math.toIntExact;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final String INGEN_TEAM = "Tilhører ikke noe team";
    private static final Set<String> IDENTITETSFELT = Set.of("sistOppdatert", "bestillingId", "ident", "master");
    private static final Pattern AAREG_KODE = Pattern.compile("BA\\d{2,3}");

    private final Altinn3TilgangServiceConsumer altinn3TilgangServiceConsumer;
    private final BestillingProgressRepository bestillingProgressRepository;
    private final BestillingRepository bestillingRepository;
    private final BrukerRepository brukerRepository;
    private final BrukerServiceConsumer brukerServiceConsumer;
    private final JsonMapper jsonMapper;
    private final R2dbcEntityTemplate entityTemplate;
    private final TeamRepository teamRepository;
    private final TeamkatalogConsumer teamkatalogConsumer;

    public Flux<DashboardBestillingerDTO> getBestillingerStatus(int year, Month month) {

        var interval = "%4d-%02d".formatted(year, month.getValue());
        return bestillingRepository.findBestillingerOrderBySistOppdatert(interval)
                .groupBy(BestillingerFragment::getDato)
                .flatMap(Flux::collectList)
                .map(fragmentliste ->
                        DashboardBestillingerDTO.builder()
                                .dato(fragmentliste.getFirst().getDato())
                                .bestillinger(fragmentliste.stream()
                                        .mapToLong(BestillingerFragment::getBestillingid)
                                        .distinct()
                                        .count())
                                .personerTotalt(fragmentliste.stream()
                                        .mapToLong(BestillingerFragment::getPersoner).sum())
                                .nye(sumByStatus(fragmentliste, BestillingerFragment::getGjenopprettstatus, "NYBESTILLING"))
                                .gjenopprettede(sumByStatus(fragmentliste, BestillingerFragment::getGjenopprettstatus, "GJENOPPRETTING"))
                                .navIdenter(sumByStatus(fragmentliste, BestillingerFragment::getMaster, "PDLF"))
                                .testnorgeIdenter(sumByStatus(fragmentliste, BestillingerFragment::getMaster, "PDL"))
                                .build())
                .sort(Comparator.comparing(DashboardBestillingerDTO::getDato).reversed());
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
                        .map(fragments -> Tuples.of(groupFragmentsByTeam(fragments, teams), fragments.getFirst().getInterval())))
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
                        .map(fragments -> Tuples.of(groupFragmentsByOrganisasjon(fragments, oppslag.getT2()), fragments.getFirst().getInterval()))
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

    public Flux<JsonNode> getFeilstatusSummert(int year, Month month) {

        var filter = "%4d-%02d".formatted(year, month.getValue());
        return buildFeilWhereFragment()
                .map(feilFilter -> "select b.sist_oppdatert::date bestilling_dato, bp.* " +
                                   "from bestilling b " +
                                   "join bestilling_progress bp on bp.bestilling_id = b.id " +
                                   "where to_char(b.sist_oppdatert, 'YYYY-MM') = :range " +
                                   "and (" + feilFilter + ") order by bestilling_dato")
                .flatMapMany(query -> entityTemplate.getDatabaseClient()
                        .sql(query)
                        .bind("range", filter)
                        .map((row, metadata) -> entityTemplate.getConverter()
                                .read(BestillingProgressDTO.class, row, metadata))
                        .all())
                .groupBy(BestillingProgressDTO::getBestillingDato)
                .concatMap(Flux::collectList)
                .concatMap(this::tilFeilstatusSummert);
    }

    public Flux<JsonNode> getFeilstatusDetaljert(int year, Month month, int day) {

        var filter = "%4d-%02d-%02d".formatted(year, month.getValue(), day);
        return buildFeilWhereFragment()
                .map(feilFilter -> "select b.sist_oppdatert, bp.* " +
                                   "from bestilling b " +
                                   "join bestilling_progress bp on bp.bestilling_id = b.id " +
                                   "where to_char(b.sist_oppdatert, 'YYYY-MM-DD') = :range " +
                                   "and (" + feilFilter + ") order by b.sist_oppdatert")
                .flatMapMany(query -> entityTemplate.getDatabaseClient()
                        .sql(query)
                        .bind("range", filter)
                        .map((row, metadata) -> entityTemplate.getConverter()
                                .read(BestillingProgressDTO.class, row, metadata))
                        .all())
                .flatMap(this::tilFeilJson);
    }

    public Flux<DashboardOversiktDTO> getPerioderOversikt() {

        return bestillingRepository.findByAvailIntervals()
                .groupBy(OversiktFragment::getMaaned)
                .flatMap(Flux::collectList)
                .map(fragmenter -> {
                    var aarManed = fragmenter.getFirst().getMaaned();
                    var yearMonth = YearMonth.parse(aarManed);
                    return DashboardOversiktDTO.builder()
                            .aarManed(aarManed)
                            .aar(yearMonth.getYear())
                            .maaned(yearMonth.getMonth())
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
                            .build();
                })
                .sort(Comparator.comparing(DashboardOversiktDTO::getAarManed).reversed());
    }

    private static long sumByStatus(List<BestillingerFragment> fragments,
                                    Function<BestillingerFragment, Object> statusGetter,
                                    Object value) {

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
            if (Objects.nonNull(orgNummer)) {
                grouped.computeIfAbsent(orgNummer, _ -> new HashSet<>()).add(fragment.getBrukerid());
            }
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

    private Mono<String> buildFeilWhereFragment() {
        return bestillingProgressRepository.findStatusColumns()
                .reduce(new StringJoiner(" or "), (joiner, column) ->
                        joiner.add("lower(bp." + column + ") like '%feil%'"))
                .map(StringJoiner::toString)
                .map(s -> s.isBlank() ? "false" : s);
    }

    private Mono<JsonNode> tilFeilstatusSummert(List<BestillingProgressDTO> bestillingProgressDTOS) {

        Map<String, Object> resultat = new HashMap<>();
        bestillingProgressDTOS.forEach(progress -> {
            var kilde = (ObjectNode) jsonMapper.valueToTree(progress);

            kilde.propertyNames().forEach(navn -> {
                var verdi = kilde.get(navn);
                if (!IDENTITETSFELT.contains(navn)) {
                    if ("bestillingDato".equals(navn)) {
                        resultat.putIfAbsent(navn, verdi);
                    } else if (verdi.isString() && verdi.asString().toLowerCase().contains("feil")) {
                        resultat.merge(navn, 1, (existing, one) -> (Integer) existing + (Integer) one);
                    }
                }
            });
        });
        var summert = resultat.entrySet().stream()
                .collect(Collectors.toMap(entry -> {
                            var key = entry.getKey();
                            if (key.contains("Status")) {
                                return key.replace("Status", "Feil");
                            } else if ("feil".equals(key)) {
                                return "andreFeil";
                            } else {
                                return key;
                            }
                        },
                        Map.Entry::getValue));
        return summert.isEmpty() ? Mono.empty() : Mono.just(jsonMapper.valueToTree(summert));
    }

    private Mono<JsonNode> tilFeilJson(BestillingProgressDTO progress) {

        var kilde = (ObjectNode) jsonMapper.valueToTree(progress);
        var resultat = jsonMapper.createObjectNode();

        kilde.propertyNames().forEach(navn -> {
            var verdi = kilde.get(navn);
            if (IDENTITETSFELT.contains(navn)) {
                resultat.set(navn, verdi);
            } else if (verdi.isString() && verdi.asString().toLowerCase().contains("feil")) {
                resultat.set(navn, tilJsonEllerTekst(verdi, navn));
            }
        });
        return Mono.just(resultat);
    }

    private JsonNode tilJsonEllerTekst(JsonNode json, String navn) {

        var verdi = lookupAaregKode(json.asString().trim()
                .replace("=", ":"));
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
        return new StringNode(verdi);
    }

    private static String lookupAaregKode(String status) {

        var matcher = AAREG_KODE.matcher(status);
        return matcher.find() ?
                status.replace(matcher.group(), BAFeilkoder.valueOf(matcher.group()).getBeskrivelse()) :
                status;
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
}
