package no.nav.registre.testnorge.generernavnservice.domain;

import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Navn {

    private String adjektiv;
    private String substantiv;
}
