package no.nav.testnav.libs.data.pdlforvalter.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolkeregistermetadataDTO implements Serializable {

    private LocalDateTime ajourholdstidspunkt;
    private Boolean gjeldende;
    private LocalDateTime gyldighetstidspunkt;
    private LocalDateTime opphoerstidspunkt;
}