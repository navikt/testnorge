package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.dto;

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
