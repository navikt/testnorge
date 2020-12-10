package no.nav.registre.sdforvalter.consumer.rs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class KodeverkDTO {
    List<KodeDTO> koder;
}