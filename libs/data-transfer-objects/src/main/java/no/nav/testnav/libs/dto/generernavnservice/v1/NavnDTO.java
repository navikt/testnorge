package no.nav.testnav.libs.dto.generernavnservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NavnDTO {
    private String adjektiv;
    private String adverb;
    private String substantiv;
}
