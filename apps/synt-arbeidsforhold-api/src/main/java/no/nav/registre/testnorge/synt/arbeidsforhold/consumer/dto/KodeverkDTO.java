package no.nav.registre.testnorge.synt.arbeidsforhold.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Map;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class KodeverkDTO {
    Map<String, Object[]> betydninger;
}
