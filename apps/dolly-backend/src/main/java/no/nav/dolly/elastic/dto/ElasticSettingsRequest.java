package no.nav.dolly.elastic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElasticSettingsRequest {

    private Settings settings;

    public static class Settings {

        Map
    }
}
