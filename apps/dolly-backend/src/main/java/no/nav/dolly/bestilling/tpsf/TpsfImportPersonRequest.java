package no.nav.dolly.bestilling.tpsf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TpsfImportPersonRequest {

    private String ident;
    private String miljoe;
}