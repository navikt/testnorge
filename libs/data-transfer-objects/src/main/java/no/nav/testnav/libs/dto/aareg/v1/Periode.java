package no.nav.testnav.libs.dto.aareg.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Periode {

    private LocalDate fom;

    private LocalDate tom;
}
