package no.nav.dolly.bestilling.skjermingsregister.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SkjermingsDataRequest {

    private SkjermetPerson skjermetPerson;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class SkjermetPerson {

        private String personident;
        private String fornavn;
        private String etternavn;
        private LocalDateTime skjermetFra;
        private LocalDateTime skjermetTil;
    }
}
