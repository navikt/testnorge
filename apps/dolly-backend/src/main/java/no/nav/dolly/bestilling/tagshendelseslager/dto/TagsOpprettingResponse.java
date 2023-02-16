package no.nav.dolly.bestilling.tagshendelseslager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagsOpprettingResponse {

    private HttpStatus status;

    private String message;
    private List<Detaljert> details;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Detaljert {

        private String name;
        private String message;
    }
}
