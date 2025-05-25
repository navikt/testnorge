package no.nav.testnav.identpool.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ExistsDatoDTO {

    private Long antall;
    private LocalDate foedselsdato;
}