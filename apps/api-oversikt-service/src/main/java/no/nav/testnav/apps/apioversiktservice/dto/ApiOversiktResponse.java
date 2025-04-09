package no.nav.testnav.apps.apioversiktservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiOversiktResponse {

    private List<System> systemer;

    public static class System {
        
        private String systemnavn;
        private String systembeskrivelse;
        private String systemeier;
        private String systemeierEpost;
        private String systemeierTelefon;
        private String systemeierNavn;
        private String systemeierTittel;
        private String systemeierAvdeling;
        private String systemeierEnhet;
        private List<String> tags;
    }
}
