package no.nav.testnav.libs.dto.dolly.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinnesDTO {

    private Map<String, Boolean> iBruk;
}