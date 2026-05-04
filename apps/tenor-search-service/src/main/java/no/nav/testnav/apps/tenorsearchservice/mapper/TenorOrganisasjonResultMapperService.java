package no.nav.testnav.apps.tenorsearchservice.mapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.TenorOrganisasjonRawResponse;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorOversiktOrganisasjonResponse;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.StringTokenizer;

import static java.util.Objects.nonNull;

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

            } catch (JacksonException e) {
                log.error("Feil ved konvertering av tenor organisasjon respons {}", e.getMessage(), e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Feil ved konvertering av tenor organisasjon response: %s".formatted(e.getMessage()), e);
            }
        } else {
            return null;
        }
    }

    @SneakyThrows
    private TenorOversiktOrganisasjonResponse.Organisasjon mapOrganisasjon(TenorOrganisasjonRawResponse.DokumentOrganisasjon dokument) {

        var organisasjonResponse = objectMapper.readValue(
                objectMapper.writeValueAsString(dokument),
                TenorOversiktOrganisasjonResponse.Organisasjon.class);

        log.info("Organisasjon response: {}", organisasjonResponse);

        organisasjonResponse.setOrganisasjonsnummer(dokument.getTenorMetadata().getId());
        organisasjonResponse.setKilder(dokument.getTenorMetadata().getKilder());
        try {
            if (nonNull(dokument.getTenorMetadata().getKildedata())) {
                organisasjonResponse.setBrregKildedata(objectMapper.readTree(dokument.getTenorMetadata().getKildedata()));
            }
        } catch (Exception e) {
            log.error("Feil ved konvertering av tenor organisasjon BRREG kildedata {} \nkildedata:\n{}",
                    e.getMessage(),
                    dokument.getTenorMetadata().getKildedata(), e);
        }

        return organisasjonResponse;
    }

    private List<TenorOversiktOrganisasjonResponse.Organisasjon> mapOrganisasjon(TenorOrganisasjonRawResponse response) {

        return response.getDokumentListe().stream()
                .map(this::mapOrganisasjon)
                .toList();
    }
}