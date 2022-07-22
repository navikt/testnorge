package no.nav.testnav.apps.bankkontoservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SfeTilbakeMelding {

    private TpsMeldingResponse svarStatus;
}
