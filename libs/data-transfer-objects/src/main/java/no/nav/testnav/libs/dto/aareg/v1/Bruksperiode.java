package no.nav.testnav.libs.dto.aareg.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bruksperiode {

    private LocalDateTime fom;

    private LocalDateTime tom;
}