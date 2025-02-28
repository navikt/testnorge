package no.nav.dolly.consumer.brukerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TilgangDTO {

    private List<String> brukere;

    public List<String> getBrukere() {

        if (isNull(brukere)) {
            brukere = new ArrayList<>();
        }
        return brukere;
    }
}
