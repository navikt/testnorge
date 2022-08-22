package no.nav.dolly.domain.resultset.skjerming;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsSkjerming {

    private LocalDateTime egenAnsattDatoFom;
    private LocalDateTime egenAnsattDatoTom;
}