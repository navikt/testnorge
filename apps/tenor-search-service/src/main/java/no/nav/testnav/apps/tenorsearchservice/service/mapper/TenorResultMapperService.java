package no.nav.testnav.apps.tenorsearchservice.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.TenorOrganisasjonRawResponse;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.TenorRawResponse;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOversiktResponse;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenorResultMapperService {

    private final ObjectMapper objectMapper;

    public TenorOversiktResponse map(TenorResponse resultat, String query) {

        return TenorOversiktResponse.builder()
                .status(resultat.getStatus())
                .error(resultat.getError())
                .data(convert(resultat))
                .query(query)
                .build();
    }

    public TenorOversiktResponse mapOrganisasjoner(TenorResponse resultat, String query) {

        return TenorOversiktResponse.builder()
                .status(resultat.getStatus())
                .error(resultat.getError())
                .data(convertOrganisasjoner(resultat))
                .query(query)
                .build();
    }

    private TenorOversiktResponse.Data convert(TenorResponse tenorResponse) {

        if (tenorResponse.getStatus().is2xxSuccessful()) {
            log.info("Mottok tenor respons: {}", Json.pretty(tenorResponse.getData()));
            try {
                var preamble = new StringBuilder();
                var noHyphenCharsInValues = new StringTokenizer(tenorResponse.getData().toString(), "-");
                while (noHyphenCharsInValues.hasMoreTokens()) {
                    preamble.append(StringUtils.capitalize(noHyphenCharsInValues.nextToken()));
                }

                var response = objectMapper.readValue(preamble.toString(), TenorRawResponse.class);
                return TenorOversiktResponse.Data.builder()
                        .rader(response.getRader())
                        .treff(response.getTreff())
                        .offset(response.getOffset())
                        .nesteSide(response.getNesteSide())
                        .seed(response.getSeed())
                        .personer(map(response))
                        .build();

            } catch (JsonProcessingException e) {
                log.error("Feil ved konvertering av tenor respons {}", e.getMessage(), e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Feil ved konvertering av tenor response: %s".formatted(e.getMessage()), e);
            }
        } else {
            return null;
        }
    }

    private TenorOversiktResponse.Data convertOrganisasjoner(TenorResponse tenorResponse) {

        if (tenorResponse.getStatus().is2xxSuccessful()) {
            log.info("Mottok tenor respons: {}", Json.pretty(tenorResponse.getData()));
            try {
                var preamble = new StringBuilder();
                var noHyphenCharsInValues = new StringTokenizer(tenorResponse.getData().toString(), "-");
                while (noHyphenCharsInValues.hasMoreTokens()) {
                    preamble.append(StringUtils.capitalize(noHyphenCharsInValues.nextToken()));
                }

                var response = objectMapper.readValue(preamble.toString(), TenorOrganisasjonRawResponse.class);
                return TenorOversiktResponse.Data.builder()
                        .rader(response.getRader())
                        .treff(response.getTreff())
                        .offset(response.getOffset())
                        .nesteSide(response.getNesteSide())
                        .seed(response.getSeed())
                        .organisasjoner(mapOrganisasjoner(response))
                        .build();

            } catch (JsonProcessingException e) {
                log.error("Feil ved konvertering av tenor respons {}", e.getMessage(), e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Feil ved konvertering av tenor response: %s".formatted(e.getMessage()), e);
            }
        } else {
            return null;
        }
    }

    private TenorOversiktResponse.Organisasjon mapOrganisasjon(TenorOrganisasjonRawResponse.DokumentOrganisasjon dokument) {


        try {
            var organisasjon = objectMapper.readValue(dokument.getTenorMetadata().getKildedata(), TenorOversiktResponse.Organisasjon.class);

            organisasjon.setNavn(dokument.getNavn());
            organisasjon.setOrganisasjonsnummer(dokument.getTenorMetadata().getId());

            return organisasjon;
        } catch (JsonProcessingException e) {
            log.error("Error while mapping JSON to Organisasjon object", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error while mapping JSON to Organisasjon object: %s".formatted(e.getMessage()), e);
        }

    }

    private List<TenorOversiktResponse.Organisasjon> mapOrganisasjoner(TenorOrganisasjonRawResponse response) {

        return response.getDokumentListe().stream()
                .map(this::mapOrganisasjon)
                .toList();
    }

    private static List<TenorOversiktResponse.Person> map(TenorRawResponse response) {

        return response.getDokumentListe().stream()
                .map(TenorResultMapperService::map)
                .toList();
    }

    private static TenorOversiktResponse.Person map(TenorRawResponse.Dokument dokument) {

        return TenorOversiktResponse.Person.builder()
                .id(dokument.getId())
                .fornavn(dokument.getFornavn())
                .etternavn(dokument.getEtternavn())
                .tenorRelasjoner(map(dokument.getTenorRelasjoner()))
                .build();
    }

    private static List<TenorOversiktResponse.TenorRelasjon> map(TenorRawResponse.TenorRelasjoner tenorRelasjoner) {

        return nonNull(tenorRelasjoner) ?
                Arrays.stream(tenorRelasjoner.getClass().getMethods())
                        .filter(metode -> metode.getName().startsWith("get"))
                        .filter(metode -> metode.getReturnType().equals(List.class))
                        .map(metode -> {
                            try {
                                return (List<?>) metode.invoke(tenorRelasjoner);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                log.error("Kunne ikke hente relasjoner", e);
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .flatMap(List::stream)
                        .map(tenorRelasjon -> tenorRelasjon.getClass().getSimpleName())
                        .distinct()
                        .sorted()
                        .map(TenorOversiktResponse.TenorRelasjon::valueOf)
                        .toList() :
                emptyList();
    }
}
