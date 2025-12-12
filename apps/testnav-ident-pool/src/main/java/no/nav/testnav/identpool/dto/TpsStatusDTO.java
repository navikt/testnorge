package no.nav.testnav.identpool.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TpsStatusDTO {

    private String ident;
    private boolean inUse;

    private boolean dirty;

    public boolean isAvailable() {

        return !inUse;
    }
}
