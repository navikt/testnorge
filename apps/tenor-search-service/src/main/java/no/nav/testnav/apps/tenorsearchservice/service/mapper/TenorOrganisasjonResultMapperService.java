package no.nav.testnav.apps.tenorsearchservice.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.TenorOrganisasjonRawResponse;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOversiktOrganisasjonResponse;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.StringTokenizer;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenorOrganisasjonResultMapperService {

    private final ObjectMapper objectMapper;

    public TenorOversiktOrganisasjonResponse mapOrganisasjon(TenorResponse resultat, String query) {

        return TenorOversiktOrganisasjonResponse.builder()
                .status(resultat.getStatus())
                .error(resultat.getError())
                .data(convertOrganisasjoner(resultat))
                .query(query)
                .build();
    }

    private TenorOversiktOrganisasjonResponse.Data convertOrganisasjoner(TenorResponse tenorResponse) {

        if (tenorResponse.getStatus().is2xxSuccessful()) {
            log.info("Mottok tenor respons: {}", Json.pretty(tenorResponse.getData()));
            try {
                var preamble = new StringBuilder();
                var noHyphenCharsInValues = new StringTokenizer(tenorResponse.getData().toString(), "-");
                while (noHyphenCharsInValues.hasMoreTokens()) {
                    preamble.append(StringUtils.capitalize(noHyphenCharsInValues.nextToken()));
                }

                var response = objectMapper.readValue(preamble.toString(), TenorOrganisasjonRawResponse.class);
                return TenorOversiktOrganisasjonResponse.Data.builder()
                        .rader(response.getRader())
                        .treff(response.getTreff())
                        .offset(response.getOffset())
                        .nesteSide(response.getNesteSide())
                        .seed(response.getSeed())
                        .organisasjoner(mapOrganisasjon(response))
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

    private TenorOversiktOrganisasjonResponse.Organisasjon mapOrganisasjon(TenorOrganisasjonRawResponse.DokumentOrganisasjon dokument) {

        return TenorOversiktOrganisasjonResponse.Organisasjon.builder()
                .organisasjonsnummer(dokument.getTenorMetadata().getId())
                .kilder(dokument.getTenorMetadata().getKilder())
                .navn(dokument.getNavn())
                .build();

    }

    private List<TenorOversiktOrganisasjonResponse.Organisasjon> mapOrganisasjon(TenorOrganisasjonRawResponse response) {

        return response.getDokumentListe().stream()
                .map(this::mapOrganisasjon)
                .toList();
    }
}
