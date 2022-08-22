package no.nav.testnav.apps.importfratpsfservice.dto;

import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SkdEndringsmeldingGruppe {

    private Long id;

    @NotBlank
    @Size(min = 1, max = 50)
    private String navn;

    @Size(min = 1, max = 200)
    private String beskrivelse;

    private Long antallSider;

    private LocalDateTime opprettetDato;

    private String opprettetAv;

    private LocalDateTime endretDato;

    private String endretAv;
}