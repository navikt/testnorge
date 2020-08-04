package no.nav.dolly.bestilling.skjermingsregister.domain;

import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private List<SkjermetPerson> skjermedePersoner;

    public List<SkjermetPerson> getSkjermedePersoner() {
        if (isNull(skjermedePersoner)) {
            return new ArrayList<>();
        }
        return skjermedePersoner;
    }

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
    }
}
