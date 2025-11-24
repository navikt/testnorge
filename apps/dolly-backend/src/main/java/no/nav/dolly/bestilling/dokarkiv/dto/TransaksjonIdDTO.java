package no.nav.dolly.bestilling.dokarkiv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransaksjonIdDTO {

    private String journalpostId;
    private String dokumentInfoId;
}
