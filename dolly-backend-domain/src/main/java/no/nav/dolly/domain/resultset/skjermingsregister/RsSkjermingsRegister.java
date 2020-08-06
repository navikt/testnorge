package no.nav.dolly.domain.resultset.skjermingsregister;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

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
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class RsSkjermingsRegister {

    private String etternavn;
    private String fornavn;
    private String personident;
    private LocalDateTime skjermetFra;
    private LocalDateTime skjermetTil;
}
