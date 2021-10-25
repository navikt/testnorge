package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FolkeregistermetadataDTO {

    private LocalDate ajourholdstidspunkt;
    private Boolean gjeldende;
    private LocalDate gyldighetstidspunkt;
    private LocalDate opphoerstidspunkt;
}