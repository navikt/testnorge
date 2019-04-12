package no.nav.dolly.domain.resultset.arenastub;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class RsArenadata {

    @JsonIgnore
    private String personident;

    private Kvalifiseringsgruppe kvalifiseringsgruppe;
}
