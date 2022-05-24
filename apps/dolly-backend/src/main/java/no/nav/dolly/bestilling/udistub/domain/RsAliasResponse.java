package no.nav.dolly.bestilling.udistub.domain;

import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsAliasResponse {

    private Persondata hovedperson;
    private List<Persondata> aliaser;

    public List<Persondata> getAliaser() {
        if (isNull(aliaser)) {
            aliaser = new ArrayList<>();
        }
        return aliaser;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Persondata {

        private String ident;
        private LocalDateTime fodselsdato;
        private Personnavn navn;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Personnavn {

        private String fornavn;
        private String mellomnavn;
        private String etternavn;
    }
}
