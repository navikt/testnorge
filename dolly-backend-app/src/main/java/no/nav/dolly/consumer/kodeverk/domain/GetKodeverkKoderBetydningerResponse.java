package no.nav.dolly.consumer.kodeverk.domain;

import static java.util.Objects.isNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetKodeverkKoderBetydningerResponse {

    private Map<String, List<Betydning>> betydninger;

    public Map<String, List<Betydning>> getBetydninger() {
        if (isNull(betydninger)) {
            betydninger = new HashMap<>();
        }
        return this.betydninger;
    }
}
