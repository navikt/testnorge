package no.nav.dolly.bestilling.dokarkiv.domain;

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
public class JoarkTransaksjon {

    private String journalpostId;
    private String dokumentInfoId;
}
