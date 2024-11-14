package no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
public class OrganisasjonDeleteDTO {

    private List<String> data;

    public List<String> getData() {

        if (isNull(data)) {
            data = new ArrayList<>();
        }
        return data;
    }

    public OrganisasjonDeleteDTO(Map<String, String> organisasjon) {

        data = organisasjon.entrySet().stream()
                .map(entry -> "%s:%s".formatted(entry.getKey(), entry.getValue()))
                .toList();
    }
}