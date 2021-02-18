package no.nav.registre.testnorge.generernavnservice.domain;

import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO;

@Value
@Builder
public class Navn {
    String adjektiv;
    String substantiv;
}
